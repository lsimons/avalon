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
import org.apache.avalon.phoenix.components.kapi.SarEntry;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.tools.assembler.DefaultAssembler;
import org.apache.avalon.phoenix.tools.assembler.AssemblyException;

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

    private final DefaultConfigurationBuilder  m_builder  = new DefaultConfigurationBuilder();
    private final DefaultAssembler m_assembler = new DefaultAssembler();

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
        final Configuration config = getConfigurationFor( installation.getConfig() );
        final Configuration server = getConfigurationFor( installation.getServer() );
        final Configuration assembly = getConfigurationFor( installation.getAssembly() );

        try
        {
            //assemble all the blocks for application
            final File directory = installation.getDirectory();
            final SarMetaData metaData = 
                m_assembler.assembleSar( name, assembly, directory );
            
            final SarEntry entry = 
                new SarEntry( metaData, installation.getClassPath(), server );

            //Setup configuration for all the applications blocks
            setupConfiguration( name, entry, config.getChildren() );

            //Finally add application to kernel
            m_container.add( name, entry );

            final String message = REZ.getString( "deploy.notice.sar.add", name );
            getLogger().debug( message );
        }
        catch( final ContainerException ce )
        {
            throw new DeploymentException( ce.getMessage(), ce );
        }
        catch( final AssemblyException ae )
        {
            throw new DeploymentException( ae.getMessage(), ae );
        }
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
            
            if( !hasBlock( name, saEntry.getMetaData().getBlocks() ) &&
                !hasBlockListener( name, saEntry.getMetaData().getListeners() ) )
            {
                final String message = REZ.getString( "deploy.error.extra.config", name );
                throw new DeploymentException( message );
            }

            try { m_repository.storeConfiguration( appName, name, configuration ); }
            catch( final ConfigurationException ce )
            {
                throw new DeploymentException( ce.getMessage(), ce );
            }
        }
    }

    /**
     * Return true if specified array contains entry with specified name.
     *
     * @param name the blocks name
     * @param blocks the set of BlockMetaData objects to search
     * @return true if block present, false otherwise
     */
    private boolean hasBlock( final String name, final BlockMetaData[] blocks )
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            final String other = blocks[ i ].getName();
            if( other.equals( name ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Return true if specified array contains entry with specified name.
     *
     * @param name the blocks name
     * @param listeners the set of BlockListenerMetaData objects to search
     * @return true if block present, false otherwise
     */
    private boolean hasBlockListener( final String name,
                                      final BlockListenerMetaData[] listeners )
    {
        for( int i = 0; i < listeners.length; i++ )
        {
            if( listeners[ i ].getName().equals( name ) )
            {
                return true;
            }
        }

        return false;
    }
}
