/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.deployer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.InvertedFileFilter;
import org.apache.avalon.excalibur.io.PrefixFileFilter;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.atlantis.Application;
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
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.engine.ServerApplicationEntry;

/**
 * Deploy .sar files into a kernel using this class.
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

    private File                     m_deployDirectory;
    private Container                m_container;
    private ConfigurationRepository  m_repository;
    private ZipExpander              m_expander         = new ZipExpander();

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
        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
    }

    /**
     * undeploy an application.
     * Currently not implemented.
     *
     * @param name the name of deployment
     * @exception DeploymentException if an error occurs
     */
    public void undeploy( final String name )
        throws DeploymentException
    {
        final String message = REZ.getString( "deploy.error.undeploy.unsupported" );
        throw new DeploymentException( message );
    }

    /**
     * Deploy a named application from a url.
     * The URL represents location of deployment archive.
     *
     * @param name the  name of application
     * @param url the URL of deployment
     * @exception DeploymentException if an error occurs
     */
    public void deploy( final String name, final URL url )
        throws DeploymentException
    {
        final File file = getFileFor( url );
        final String message = REZ.format( "deploy.notice.deploying", file, name );
        getLogger().info( message );

        if( file.isDirectory() )
        {
            deployFromDirectory( name, file );
        }
        else
        {
            final File destination = getDestinationFor( name, file );
            expand( file, destination );
            deployFromDirectory( name, destination );
        }
    }

    /**
     * Get File object for URL.
     * Currently it assumes that URL is a file URL but in the
     * future it will allow downloading of remote URLs thus enabling
     * a deploy from anywhere functionality.
     *
     * @param url the url of deployment
     * @return the File for deployment
     * @exception DeploymentException if an error occurs
     */
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

    /**
     * Expand file to destination directory.
     *
     * @param file the archive to expand
     * @param destination the destination to expand to
     * @exception DeploymentException if an error occurs
     */
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

    /**
     * Get destination that .sar should be expanded to.
     *
     * @param name the name of server application
     * @param file the file object representing .sar archive
     * @return the destination to expand archive
     */
    private File getDestinationFor( final String name, final File file )
    {
        final String base =
            FileUtil.removeExtension( FileUtil.removePath( file.getName() ) );

        if( null != m_deployDirectory )
        {
            return (new File( m_deployDirectory, base )).getAbsoluteFile();
        }
        else
        {
            return (new File( file.getParentFile(), base )).getAbsoluteFile();
        }
    }

    /**
     * Deploy an application from a directory.
     *
     * @param name the name of application
     * @param directory the directory to deploy from
     * @exception DeploymentException if an error occurs
     */
    private void deployFromDirectory( final String name, final File directory )
        throws DeploymentException
    {
        final ServerApplicationEntry entry = new ServerApplicationEntry();
        entry.setHomeDirectory( directory );

        //Loader server.xml for application
        File file = new File( directory, SERVER_XML );
        Configuration configuration = getConfigurationFor( file );
        entry.setConfiguration( configuration );

        //Setup applications classpath
        final URL[] urls = getClassPath( directory );
        entry.setClassPath( urls );

        //assemble all the blocks for application
        file = new File( directory, ASSEMBLY_XML );
        configuration = getConfigurationFor( file );
        final Configuration[] blocks = configuration.getChildren( "block" );
        final BlockEntry[] blockEntrys = assembleBlocks( entry, blocks );
        entry.setBlockEntrys( blockEntrys );

        //Setup configuration for all the applications blocks
        file = new File( directory, CONFIG_XML );
        configuration = getConfigurationFor( file );
        configureBlocks( name, entry, configuration.getChildren() );

        //Finally add application to kernel
        addEntry( name, entry );
    }

    /**
     * Get Classpath for application.
     *
     * @return the list of URLs in ClassPath
     */
    private URL[] getClassPath( final File directory )
    {
        final File blockDir = new File( directory, "blocks" );
        final File libDir = new File( directory, "lib" );

        final ArrayList urls = new ArrayList();
        getURLs( urls, blockDir, new String[] { ".bar" } );
        getURLs( urls, libDir, new String[] { ".jar", ".zip" } );
        return (URL[])urls.toArray( new URL[0] );
    }

    /**
     * Add all matching files in directory to url list.
     *
     * @param urls the url list
     * @param directory the directory to scan
     * @param extentions the list of extensions to match
     * @exception MalformedURLException if an error occurs
     */
    private void getURLs( final ArrayList urls, final File directory, final String[] extensions )
    {
        final ExtensionFileFilter filter = new ExtensionFileFilter( extensions );
        final File[] files = directory.listFiles( filter );
        if( null == files ) return;
        for( int i = 0; i < files.length; i++ )
        {
            try { urls.add( files[ i ].toURL() ); }
            catch( final MalformedURLException mue )
            {
                //should never occur
            }
        }
    }

    /**
     * Add server application entry to kernel.
     *
     * @param name the name of application
     * @param entry the entry
     * @exception DeploymentException if an error occurs
     */
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

    /**
     * Helper method to load configuration data.
     *
     * @param file the source of configuration data
     * @return the Configuration
     * @exception DeploymentException if an error occurs
     */
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

    /**
     * Process assembly.xml and create a list of BlockEntrys.
     *
     * @param saEntry the ServerApplication Entry
     * @param blocks the assembly data for blocks
     * @return the  created BlockEntrys
     * @exception DeploymentException if an error occurs
     */
    private BlockEntry[] assembleBlocks( final ServerApplicationEntry saEntry,
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

    /**
     * Helper method to build an array of RoleEntrys from input config data.
     *
     * @param provides the set of provides elements for block
     * @return the created RoleEntry array
     * @exception ConfigurationException if config data is malformed
     */
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

    /**
     * Setup Configuration for all the BlockEntrys in ServerApplication.
     *
     * @param appName the name of Application.
     * @param saEntry the ServerApplication Entry.
     * @param configurations the block configurations.
     * @exception DeploymentException if an error occurs
     */
    private void configureBlocks( final String appName,
                                  final ServerApplicationEntry saEntry,
                                  final Configuration[] configurations )
        throws DeploymentException
    {
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String name = configuration.getName();
            final BlockEntry entry = getBlockEntry( name, saEntry.getBlockEntrys() );

            try { m_repository.storeConfiguration( appName, name, configuration ); }
            catch( final ConfigurationException ce )
            {
                throw new DeploymentException( ce.getMessage(), ce );
            }

            final String message = REZ.format( "deploy.notice.block.config", name );
            getLogger().debug( message );
        }
    }

    /**
     * Helper method to get BlockEntry with specified name from an array of BlockEntrys.
     *
     * @param name the block entrys name
     * @param blockEntrys the set of BlockEntry objects to search
     * @return the BlockEntry
     * @exception DeploymentException if BlockEntry not found
     */
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
