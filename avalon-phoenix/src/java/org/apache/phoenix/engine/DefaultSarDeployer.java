/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

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
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.DefaultComponentManager;
import org.apache.avalon.DefaultContext;
import org.apache.avalon.atlantis.Application;
import org.apache.avalon.atlantis.Kernel;
import org.apache.avalon.camelot.AbstractCamelotDeployer;
import org.apache.avalon.camelot.CamelotUtil;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.DefaultRegistry;
import org.apache.avalon.camelot.Deployer;
import org.apache.avalon.camelot.DeployerUtil;
import org.apache.avalon.camelot.DeploymentException;
import org.apache.avalon.camelot.Locator;
import org.apache.avalon.camelot.Registry;
import org.apache.avalon.camelot.RegistryException;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.util.io.FileUtil;
import org.apache.avalon.util.io.IOUtil;
import org.apache.phoenix.BlockContext;
import org.apache.phoenix.engine.blocks.BlockEntry;
import org.apache.phoenix.engine.blocks.DefaultBlockDeployer;
import org.apache.phoenix.engine.blocks.RoleEntry;
import org.apache.phoenix.metainfo.BlockInfo;

/**
 * This class deploys a .sar file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultSarDeployer
    extends AbstractCamelotDeployer
{
    protected File            m_deployDirectory;

    /**
     * Default constructor.
     */
    public DefaultSarDeployer()
    {
        m_deployToContainer = true;
        m_autoUndeploy = true;
        m_type = "Sar";
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
            expandTo( file, destination );
            deployFromDirectory( file, name, destination );
        }
    }

    protected void expandTo( final File file, final File directory )
        throws DeploymentException
    {
        final ZipFile zipFile = DeployerUtil.getZipFileFor( file );

        if( !needsExpanding( zipFile, directory ) )
        {
            return;
        }

        directory.mkdirs();

        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            if( entry.isDirectory() ) continue;

            final String name = entry.getName().replace( '/', File.separatorChar );
            if( !shouldExpandEntry( entry.getName() ) ) continue;

            final File destination = new File( directory, name );

            InputStream input = null;
            OutputStream output = null;

            try
            {
                destination.getParentFile().mkdirs();
                output = new FileOutputStream( destination );
                input = zipFile.getInputStream( entry );
                IOUtil.copy( input, output );
            }
            catch( final IOException ioe )
            {
                throw new DeploymentException( "Error extracting " + name, ioe );
            }
            finally
            {
                IOUtil.shutdownStream( input );
                IOUtil.shutdownStream( output );
            }
        }
    }

    protected boolean shouldExpandEntry( final String name )
    {
        if( name.startsWith( "META-INF" ) ) return false;
        else return true;
    }

    protected boolean needsExpanding( final ZipFile zipFile, final File directory )
    {
        return !directory.exists();
    }

    protected File getDestinationFor( final String location, final File file )
    {
        final String name =
            FileUtil.removeExtention( FileUtil.removePath( file.getName() ) );

        if( null != m_deployDirectory )
        {
            return (new File( m_deployDirectory, name )).getAbsoluteFile();
        }
        else
        {
            return (new File( file.getParentFile(), name )).getAbsoluteFile();
        }
    }

    protected Kernel getKernel()
        throws DeploymentException
    {
        if( !(m_container instanceof Kernel) )
        {
            throw new DeploymentException( "Can only deploy to a kernel container" );
        }
        else
        {
            return (Kernel)m_container;
        }
    }

    protected void buildEntry( final String name,
                               final ServerApplicationEntry entry,
                               final File archive,
                               final File directory )
        throws DeploymentException
    {
        //final File file = new File( directory, "SAR-INF" + File.separator + "sar-inf.xml" );

        //setup the ServerApplications context
        final DefaultContext context = new DefaultContext();
        context.put( SarContextResources.APP_ARCHIVE, archive );
        context.put( SarContextResources.APP_HOME_DIR, directory );
        context.put( SarContextResources.APP_NAME, name );
        entry.setContext( context );

        //setup the ServerApplications component manager
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        componentManager.put( "org.apache.avalon.camelot.Registry",
                              new DefaultRegistry( BlockInfo.class ) );
        componentManager.put( "org.apache.avalon.camelot.Registry/Locator",
                              new DefaultRegistry( Locator.class ) );
        entry.setComponentManager( componentManager );

        //setup the ServerApplications configuration manager
        final File file = new File( directory, "conf" + File.separator + "server.xml" );
        final Configuration configuration = getConfigurationFor( file );
        entry.setConfiguration( configuration );
    }

    protected void deployFromDirectory( final File archive,
                                        final String name,
                                        final File directory )
        throws DeploymentException
    {
        getLogger().info( "deploying from archive (" + archive +
                          ") expanded into directory " + directory );

        final ServerApplicationEntry entry = new ServerApplicationEntry();
        buildEntry( name, entry, archive, directory );
        addEntry( name, entry );

        final Kernel kernel = getKernel();
        Application application = null;
        try
        {
            application = kernel.getApplication( name );
        }
        catch( final ContainerException ce )
        {
            throw new DeploymentException( "Error preparing server application", ce );
        }

        //rework next bit so it grabs deployments from archive
        final Deployer deployer = getBlockDeployer( entry );
        final File blocksDirectory = new File( directory, "blocks" );
        CamelotUtil.deployFromDirectory( deployer, blocksDirectory, ".bar" );

        final File file =
            new File( directory, "conf" + File.separator + "assembly.xml" );

        try
        {
            final Configuration configuration = getConfigurationFor( file );
            final Configuration[] blocks = configuration.getChildren( "block" );
            handleBlocks( application, entry, blocks );
        }
        catch( final ComponentManagerException cme )
        {
            throw new DeploymentException( "Error setting up registries", cme );
        }
        catch( final ConfigurationException ce )
        {
            throw new DeploymentException( "Error in assembly.xml", ce );
        }
    }

    protected Configuration getConfigurationFor( final File file )
        throws DeploymentException
    {
        try
        {
            final FileInputStream input = new FileInputStream( file );
            return DeployerUtil.buildConfiguration( input );
        }
        catch( final IOException ioe )
        {
            throw new DeploymentException( "Error reading " + file, ioe );
        }
    }

    protected Deployer getBlockDeployer( final ServerApplicationEntry entry )
        throws DeploymentException
    {
        final Deployer deployer = new DefaultBlockDeployer();
        setupLogger( deployer );

        if( deployer instanceof Composer )
        {
            try { ((Composer)deployer).compose( entry.getComponentManager() ); }
            catch( final Exception e )
            {
                throw new DeploymentException( "Error composing block deployer", e );
            }
        }

        return deployer;
    }

    protected void handleBlocks( final Application application,
                                 final ServerApplicationEntry saEntry,
                                 final Configuration[] blocks )
        throws ComponentManagerException, ConfigurationException, DeploymentException
    {
        final ComponentManager componentManager = saEntry.getComponentManager();

        final Registry infoRegistry =
            (Registry)componentManager.lookup( "org.apache.avalon.camelot.Registry" );

        final Registry locatorRegistry = (Registry)componentManager.
            lookup( "org.apache.avalon.camelot.Registry/Locator" );

        for( int i = 0; i < blocks.length; i++ )
        {
            final Configuration block = blocks[ i ];
            final String name = block.getAttribute("name");
            final String className = block.getAttribute("class");

            BlockInfo info = null;

            try { info = (BlockInfo)infoRegistry.getInfo( className, BlockInfo.class ); }
            catch( final RegistryException re )
            {
                throw new DeploymentException( "Failed to aquire BlockInfo for " + className,
                                               re );
            }

            Locator locator = null;
            try { locator = (Locator)locatorRegistry.getInfo( className, Locator.class ); }
            catch( final RegistryException re )
            {
                throw new DeploymentException( "Failed to aquire Locator for " + className,
                                               re );
            }

            final Configuration[] provides = block.getChildren( "provide" );
            final ArrayList roleList = new ArrayList();
            for( int j = 0; j < provides.length; j++ )
            {
                final Configuration provide = provides[ j ];
                final String requiredName = provide.getAttribute("name");
                final String role = provide.getAttribute("role");

                roleList.add( new RoleEntry( requiredName, role ) );
            }

            final RoleEntry[] roles = (RoleEntry[]) roleList.toArray( new RoleEntry[ 0 ] );
            final BlockEntry entry = new BlockEntry( roles );
            entry.setLocator( locator );
            entry.setBlockInfo( info );
            entry.setConfiguration( block.getChild( "configuration" ) );

            try { application.add( name, entry ); }
            catch( final ContainerException ce )
            {
                throw new DeploymentException( "Error adding component to container", ce );
            }

            getLogger().debug( "Adding " + m_type + "Entry " + name + " as " + entry );
        }
    }
}
