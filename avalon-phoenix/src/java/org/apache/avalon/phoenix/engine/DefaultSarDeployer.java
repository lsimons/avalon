/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.InvertedFileFilter;
import org.apache.avalon.excalibur.io.PrefixFileFilter;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.atlantis.Application;
import org.apache.avalon.framework.atlantis.Kernel;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.camelot.ContainerException;
import org.apache.avalon.framework.camelot.Deployer;
import org.apache.avalon.framework.camelot.DeploymentException;
import org.apache.avalon.framework.camelot.Locator;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.engine.blocks.BlockEntry;
import org.apache.avalon.phoenix.engine.blocks.RoleEntry;

/**
 * This class deploys a .sar file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultSarDeployer
    extends AbstractLoggable
    implements Deployer, Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultSarDeployer.class );

    private static final String    ASSEMBLY_XML  = "conf" + File.separator + "assembly.xml";
    private static final String    CONFIG_XML    = "conf" + File.separator + "config.xml";
    private static final String    SERVER_XML    = "conf" + File.separator + "server.xml";

    private final DefaultConfigurationBuilder  m_builder  = new DefaultConfigurationBuilder();

    private File            m_deployDirectory;
    private Container       m_container;
    private ZipExpander     m_expander         = new ZipExpander();

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

    public void undeploy( final String location )
        throws DeploymentException
    {
        final String message = REZ.getString( "deploy.error.undeploy.unsupported" );
        throw new DeploymentException( message );
    }

    public void deploy( final String location, final URL url )
        throws DeploymentException
    {
        final File file = getFileFor( url );
        final String message = REZ.format( "deploy.notice.deploying", file, location );
        getLogger().info( message );
        deployFromFile( location, file );
    }

    private File getFileFor( final URL url )
        throws DeploymentException
    {
        if( !url.getProtocol().equals( "file" ) )
        {
            final String message = REZ.getString( "deploy.error.deploy.nonlocal" );
            throw new DeploymentException( message );
        }

        File file = new File( url.getFile() );
        file = file.getAbsoluteFile();

        if( !file.exists() )
        {
            final String message = REZ.format( "deploy.error.deploy.nofile", file );
            throw new DeploymentException( message );
        }

        return file;
    }

    private void expand( final File file, final File destination )
        throws DeploymentException
    {
        try
        {
            final String message = REZ.format( "deploy.notice.expanding", file, destination );
            getLogger().info( message );
            
            final InvertedFileFilter filter =
                new InvertedFileFilter( new PrefixFileFilter( "META-INF" ) );
            m_expander.expand( file, destination, filter );
        }
        catch( final IOException ioe )
        {
            final String message = REZ.format( "deploy.error.expanding", file, destination );
            throw new DeploymentException( message, ioe );
        }
        
        final String message = REZ.format( "deploy.notice.expanded", file, destination );
        getLogger().info( message );        
    }

    private void deployFromFile( final String name, final File file )
        throws DeploymentException
    {
        File destination = null;

        if( file.isDirectory() )
        {
            destination = file;
        }
        else
        {
            destination = getDestinationFor( name, file );
            expand( file, destination );
        }
    
        try { deployFromDirectory( name, destination ); }
        catch( final DeploymentException de )
        {
            throw de;
        }
        catch( final Exception e )
        {
            final String message = REZ.format( "deploy.error.deploy.failed", name, destination );
            throw new DeploymentException( message, e );
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
                             final File directory )
        throws DeploymentException
    {
        entry.setHomeDirectory( directory );

        //setup the ServerApplications configuration manager
        final File file = new File( directory, SERVER_XML );
        final Configuration configuration = getConfigurationFor( file );
        entry.setConfiguration( configuration );
    }

    private void deployFromDirectory( final String name, final File directory )
        throws DeploymentException
    {
        final ServerApplicationEntry entry = new ServerApplicationEntry();
        buildEntry( name, entry, directory );
        addEntry( name, entry );

        Application application = null;
        try { application = ((Kernel)m_container).getApplication( name ); }
        catch( final ContainerException ce )
        {
            throw new DeploymentException( ce.getMessage(), ce );
        }

        final File blocksDirectory = new File( directory, "blocks" );

        File file = new File( directory, ASSEMBLY_XML );

        Configuration configuration = getConfigurationFor( file );
        final Configuration[] blocks = configuration.getChildren( "block" );
        final BlockEntry[] blockEntrys = assembleBlocks( application, entry, blocks );

        entry.setBlockEntrys( blockEntrys );

        file = new File( directory, CONFIG_XML );

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
            final String message = REZ.format( "deploy.error.sar.add", name );
            throw new DeploymentException( message, ce );
        }

        final String message = REZ.format( "deploy.notice.sar.add", name );
        getLogger().debug( message );
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
            final String message = REZ.format( "deploy.error.config.create", file );
            throw new DeploymentException( message, e );
        }
    }

    private BlockEntry[] assembleBlocks( final Application application,
                                         final ServerApplicationEntry saEntry,
                                         final Configuration[] blocks )
        throws DeploymentException
    {
        final ArrayList blockEntrys = new ArrayList();
        for( int i = 0; i < blocks.length; i++ )
        {
            final Configuration block = blocks[ i ];

            try
            {
                final String name = block.getAttribute( "name" );
                final String className = block.getAttribute( "class" );
                final Configuration[] provides = block.getChildren( "provide" );

                final RoleEntry[] roles = buildRoleEntrys( provides );
                final Locator locator = new Locator( className, null );
                final BlockEntry entry = new BlockEntry( name, roles, locator );
                blockEntrys.add( entry );

                final String message = REZ.format( "deploy.notice.block.add", name );
                getLogger().debug( message );
            }
            catch( final ConfigurationException ce )
            {
                final String message = REZ.getString( "deploy.error.assembly.malformed" );
                throw new DeploymentException( message, ce );
            }
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

            final String message = REZ.format( "deploy.notice.block.config", name );
            getLogger().debug( message );
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

        final String message = REZ.format( "deploy.notice.block.missing", name );
        throw new DeploymentException( message );
    }
}
