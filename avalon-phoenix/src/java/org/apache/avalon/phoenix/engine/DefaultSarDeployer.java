/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.avalon.framework.atlantis.Application;
import org.apache.avalon.framework.atlantis.Kernel;
import org.apache.avalon.framework.camelot.AbstractDeployer;
import org.apache.avalon.framework.camelot.CamelotUtil;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.camelot.ContainerException;
import org.apache.avalon.framework.camelot.Deployer;
import org.apache.avalon.framework.camelot.DeploymentException;
import org.apache.avalon.framework.camelot.Info;
import org.apache.avalon.framework.camelot.Locator;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.excalibur.io.InvertedFileFilter;
import org.apache.avalon.excalibur.io.PrefixFileFilter;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.engine.blocks.BlockEntry;
import org.apache.avalon.phoenix.engine.blocks.RoleEntry;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * This class deploys a .sar file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultSarDeployer
    extends AbstractDeployer
    implements Composable
{
    private final DefaultConfigurationBuilder  m_builder  = new DefaultConfigurationBuilder();

    private File            m_deployDirectory;
    private Container       m_container;
    private ZipExpander     m_expander         = new ZipExpander();

    /**
     * Default constructor.
     */
    public DefaultSarDeployer()
    {
        m_autoUndeploy = true;
        m_type = "Sar";
    }

    /**
     * Retrieve relevent services needed to deploy.
     *
     * @param componentManager the ComponentManager
     * @exception ComponentException if an error occurs
     */
    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_container = (Container)componentManager.lookup( Container.ROLE );
    }

    protected void deployFromFile( final String name, final File file )
        throws DeploymentException
    {
        if( file.isDirectory() )
        {
            throw new DeploymentException( "Deploying directories is not supported" );
        }
        else
        {
            final File destination = getDestinationFor( name, file );

            try
            {
                final InvertedFileFilter filter = 
                    new InvertedFileFilter( new PrefixFileFilter( "META-INF" ) );
                m_expander.expand( file, destination, filter );
            }
            catch( final IOException ioe )
            {
                throw new DeploymentException( "Error expanding deployment", ioe );
            }

            try { deployFromDirectory( file, name, destination ); }
            catch( final DeploymentException de )
            {
                throw de;
            }
            catch( final Exception e )
            {
                throw new DeploymentException( "Error deploying from " + destination, e );                
            }
        }
    }

    private File getDestinationFor( final String location, final File file )
    {
        final String name =
            FileUtil.removeExtension( FileUtil.removePath( file.getName() ) );

        if( null != m_deployDirectory )
        {
            return (new File( m_deployDirectory, name )).getAbsoluteFile();
        }
        else
        {
            return (new File( file.getParentFile(), name )).getAbsoluteFile();
        }
    }

    private void buildEntry( final String name,
                             final ServerApplicationEntry entry,
                             final File archive,
                             final File directory )
        throws Exception
    {
        //final File file = new File( directory, "SAR-INF" + File.separator + "sar-inf.xml" );
        entry.setHomeDirectory( directory );

        //setup the ServerApplications configuration manager
        final File file = new File( directory, "conf" + File.separator + "server.xml" );
        final Configuration configuration = getConfigurationFor( file );
        entry.setConfiguration( configuration );
    }

    private void deployFromDirectory( final File archive,
                                      final String name,
                                      final File directory )
        throws Exception
    {
        getLogger().info( "deploying from archive (" + archive +
                          ") expanded into directory " + directory );

        final ServerApplicationEntry entry = new ServerApplicationEntry();
        buildEntry( name, entry, archive, directory );
        addEntry( name, entry );

        final Application application = ((Kernel)m_container).getApplication( name );

        final File blocksDirectory = new File( directory, "blocks" );

        File file =
            new File( directory, "conf" + File.separator + "assembly.xml" );
        
        Configuration configuration = getConfigurationFor( file );
        final Configuration[] blocks = configuration.getChildren( "block" );
        final BlockEntry[] blockEntrys = assembleBlocks( application, entry, blocks );

        entry.setBlockEntrys( blockEntrys );

        file = new File( directory, "conf" + File.separator + "config.xml" );
        
        configuration = getConfigurationFor( file );
        configureBlocks( entry, configuration.getChildren() );
    }

    private void addEntry( final String name, final ServerApplicationEntry entry )
        throws DeploymentException
    {
        try
        {
            m_container.add( name, entry );
        }
        catch( final ContainerException ce )
        {
            throw new DeploymentException( "Error adding component to container", ce );
        }

        getLogger().debug( "Adding SarEntry " + name + " as " + entry );
    }

    private Configuration getConfigurationFor( final File file )
        throws DeploymentException
    {
        try
        {
            return m_builder.buildFromFile( file );
        }
        catch( final Exception e )
        {
            throw new DeploymentException( "Error building configuration from " + file, e );
        }
    }

    private BlockEntry[] assembleBlocks( final Application application,
                                         final ServerApplicationEntry saEntry,
                                         final Configuration[] blocks )
        throws ComponentException, ConfigurationException, DeploymentException
    {
        final ArrayList blockEntrys = new ArrayList();

        for( int i = 0; i < blocks.length; i++ )
        {
            final Configuration block = blocks[ i ];

            final String name = block.getAttribute("name");
            final String className = block.getAttribute("class");

            final Configuration[] provides = block.getChildren( "provide" );
            final RoleEntry[] roles = buildRoleEntrys( provides );

            final BlockEntry entry = new BlockEntry( name, roles );

            final Locator locator = new Locator( className, null );
            entry.setLocator( locator );

            entry.setConfiguration( block.getChild( "configuration" ) );

            blockEntrys.add( entry );
            getLogger().debug( "Adding BlockEntry " + name + " as " + entry );
        }

        return (BlockEntry[])blockEntrys.toArray( new BlockEntry[ 0 ] );
    }

    private RoleEntry[] buildRoleEntrys( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList roleList = new ArrayList();
        for( int j = 0; j < provides.length; j++ )
        {
            final Configuration provide = provides[ j ];
            final String requiredName = provide.getAttribute( "name" );
            final String role = provide.getAttribute( "role" );
            
            roleList.add( new RoleEntry( requiredName, role ) );
        }
        
        return (RoleEntry[])roleList.toArray( new RoleEntry[ 0 ] );
    }

    private void configureBlocks( final ServerApplicationEntry saEntry,
                                  final Configuration[] configurations )
        throws DeploymentException
    {
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String name = configuration.getName();
            final BlockEntry entry = getBlockEntry( name, saEntry.getBlockEntrys() );

            entry.setConfiguration( configuration );

            getLogger().debug( "Loaded configuration for block " + name );
        }
    }

    private BlockEntry getBlockEntry( final String name, 
                                      final BlockEntry[] blockEntrys )
        throws DeploymentException
    {
        for( int i = 0; i < blockEntrys.length; i++ )
        {
            if( blockEntrys[ i ].getName().equals( name ) )
            {
                return blockEntrys[ i ];
            }
        }

        throw new DeploymentException( "Unable to locate block named '" + name + "'" );
    }
}
