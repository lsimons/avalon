/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.deployer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
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
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.phoenix.components.kapi.RoleEntry;
import org.apache.avalon.phoenix.components.kapi.ServerApplicationEntry;
import org.apache.avalon.phoenix.components.installer.InstallationException;
import org.apache.avalon.phoenix.components.installer.Installation;
import org.apache.avalon.phoenix.components.installer.Installer;
import org.apache.avalon.phoenix.components.installer.DefaultInstaller;

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
    private Installer                m_installer;

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
        //m_installer = (Installer)componentManager.lookup( Installer.ROLE );
        m_installer = new DefaultInstaller();
        setupLogger( m_installer );
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
        try
        {
            final Installation installation = m_installer.install( name, url );
            deployFromInstallation( name, installation );
        }
        catch( final InstallationException ie )
        {
            throw new DeploymentException( "Error installing " + name, ie );
        }
        //final File file = getFileFor( url );
        //final String message = REZ.getString( "deploy.notice.deploying", file, name );
        //getLogger().info( message );
    }

    /**
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param directory the directory to deploy from
     * @exception DeploymentException if an error occurs
     */
    private void deployFromInstallation( final String name, final Installation installation )
        throws DeploymentException
    {
        final ServerApplicationEntry entry = new ServerApplicationEntry();
        entry.setHomeDirectory( installation.getDirectory() );

        //Loader server.xml for application
        Configuration configuration = getConfigurationFor( installation.getServer() );
        entry.setConfiguration( configuration );

        //Setup applications classpath
        entry.setClassPath( installation.getClassPath() );

        //assemble all the blocks for application
        configuration = getConfigurationFor( installation.getAssembly() );
        final Configuration[] blocks = configuration.getChildren( "block" );
        final BlockEntry[] blockEntrys = assembleBlocks( entry, blocks );
        entry.setBlockEntrys( blockEntrys );

        //Setup configuration for all the applications blocks
        configuration = getConfigurationFor( installation.getConfig() );
        configureBlocks( name, entry, configuration.getChildren() );

        //Finally add application to kernel
        addEntry( name, entry );
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
            final String message = REZ.getString( "deploy.error.sar.add", name );
            throw new DeploymentException( message, ce );
        }

        final String message = REZ.getString( "deploy.notice.sar.add", name );
        getLogger().debug( message );
    }

    /**
     * Helper method to load configuration data.
     *
     * @param file the source of configuration data
     * @return the Configuration
     * @exception DeploymentException if an error occurs
     */
    private Configuration getConfigurationFor( final URL url )
        throws DeploymentException
    {
        try
        {
            return m_builder.build( url.toString() );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "deploy.error.config.create", url );
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

                final String message = REZ.getString( "deploy.notice.block.add", name );
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

            final String message = REZ.getString( "deploy.notice.block.config", name );
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

        final String message = REZ.getString( "deploy.notice.block.missing", name );
        throw new DeploymentException( message );
    }
}
