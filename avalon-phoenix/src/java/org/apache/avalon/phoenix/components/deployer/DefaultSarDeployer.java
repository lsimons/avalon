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
import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.installer.Installation;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.phoenix.components.kapi.SarEntry;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;

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
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param directory the directory to deploy from
     * @exception DeploymentException if an error occurs
     */
    public void deploy( final String name, final Installation installation )
        throws DeploymentException
    {
        Configuration configuration = null;

        //assemble all the blocks for application
        configuration = getConfigurationFor( installation.getAssembly() );
        final Configuration[] blockConfig = configuration.getChildren( "block" );
        final BlockMetaData[] blocks = assembleBlocks( blockConfig );

        final Configuration[] listenerConfig = configuration.getChildren( "block-listener" );
        final BlockListenerMetaData[] listeners = assembleBlockListeners( listenerConfig );

        final SarMetaData metaData = 
            new SarMetaData( name,
                             installation.getDirectory(),
                             installation.getClassPath(),
                             blocks,
                             listeners );

        final SarEntry entry = new SarEntry( metaData );

        //Loader server.xml for application
        configuration = getConfigurationFor( installation.getServer() );
        entry.setConfiguration( configuration );

        //Setup configuration for all the applications blocks
        configuration = getConfigurationFor( installation.getConfig() );
        setupConfiguration( name, entry, configuration.getChildren() );

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
    private void addEntry( final String name, final SarEntry entry )
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
    private BlockMetaData[] assembleBlocks( final Configuration[] blocks )
        throws DeploymentException
    {
        final ArrayList blockSet = new ArrayList();
        for( int i = 0; i < blocks.length; i++ )
        {
            final Configuration block = blocks[ i ];

            try
            {
                final String name = block.getAttribute( "name" );
                final String classname = block.getAttribute( "class" );
                final Configuration[] provides = block.getChildren( "provide" );

                final DependencyMetaData[] roles = buildDependencyMetaDatas( provides );
                final BlockMetaData blockMetaData = new BlockMetaData( name, classname, roles );
                blockSet.add( blockMetaData );

                final String message = REZ.getString( "deploy.notice.block.add", name );
                getLogger().debug( message );
            }
            catch( final ConfigurationException ce )
            {
                final String message = REZ.getString( "deploy.error.assembly.malformed" );
                throw new DeploymentException( message, ce );
            }
        }

        return (BlockMetaData[])blockSet.toArray( new BlockMetaData[ 0 ] );
    }


    /**
     * Process assembly.xml and create a list of BlockListenerMetaDatas.
     *
     * @param blockListeners the assembly data for blockListeners
     * @return the  created BlockListenerMetaDatas
     * @exception DeploymentException if an error occurs
     */
    private BlockListenerMetaData[] assembleBlockListeners( final Configuration[] listeners )
        throws DeploymentException
    {
        final ArrayList listenersMetaData = new ArrayList();
        for( int i = 0; i < listeners.length; i++ )
        {
            final Configuration listener = listeners[ i ];

            try
            {
                final String name = listener.getAttribute( "name" );
                final String className = listener.getAttribute( "class" );

                final BlockListenerMetaData entry = new BlockListenerMetaData( name, className );
                listenersMetaData.add( entry );

                final String message = REZ.getString( "deploy.notice.listener.add", name );
                getLogger().debug( message );
            }
            catch( final ConfigurationException ce )
            {
                final String message = REZ.getString( "deploy.error.assembly.malformed" );
                throw new DeploymentException( message, ce );
            }
        }

        return (BlockListenerMetaData[])listenersMetaData.toArray( new BlockListenerMetaData[ 0 ] );
    }

    /**
     * Helper method to build an array of DependencyMetaDatas from input config data.
     *
     * @param provides the set of provides elements for block
     * @return the created DependencyMetaData array
     * @exception ConfigurationException if config data is malformed
     */
    private DependencyMetaData[] buildDependencyMetaDatas( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList dependencies = new ArrayList();
        for( int j = 0; j < provides.length; j++ )
        {
            final Configuration provide = provides[ j ];
            final String requiredName = provide.getAttribute( "name" );
            final String role = provide.getAttribute( "role" );

            dependencies.add( new DependencyMetaData( requiredName, role ) );
        }

        return (DependencyMetaData[])dependencies.toArray( new DependencyMetaData[ 0 ] );
    }

    /**
     * Setup Configuration for all the BlockEntrys in ServerApplication.
     *
     * @param appName the name of Application.
     * @param saEntry the ServerApplication Entry.
     * @param configurations the block configurations.
     * @exception DeploymentException if an error occurs
     */
    private void setupConfiguration( final String appName,
                                     final SarEntry saEntry,
                                     final Configuration[] configurations )
        throws DeploymentException
    {
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String name = configuration.getName();
            
            if( null == getBlock( name, saEntry.getMetaData().getBlocks() ) )
            {
                if( null == getBlockListener( name, saEntry.getMetaData().getListeners() ) )
                {
                    final String message = REZ.getString( "deploy.error.extra.config", name );
                    throw new DeploymentException( message );
                }                
            }

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
     * @return the BlockEntry or null if not found
     */
    private BlockMetaData getBlock( final String name, final BlockMetaData[] blocks )
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            final String other = blocks[ i ].getName();
            if( other.equals( name ) )
            {
                return blocks[ i ];
            }
        }

        return null;
    }

    /**
     * Helper method to get BlockListenerEntry with specified name from an array of BlockListenerEntrys.
     *
     * @param name the block entrys name
     * @param blockEntrys the set of BlockListenerEntry objects to search
     * @return the BlockListenerEntry or null if not found
     */
    private BlockListenerMetaData getBlockListener( final String name,
                                                    final BlockListenerMetaData[] listeners )
    {
        for( int i = 0; i < listeners.length; i++ )
        {
            if( listeners[ i ].getName().equals( name ) )
            {
                return listeners[ i ];
            }
        }

        return null;
    }
}
