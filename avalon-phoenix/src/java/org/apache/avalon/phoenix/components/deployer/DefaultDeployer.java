/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.deployer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.DeployerMBean;
import org.apache.avalon.phoenix.interfaces.DeploymentException;
import org.apache.avalon.phoenix.interfaces.DeploymentRecorder;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.tools.assembler.Assembler;
import org.apache.avalon.phoenix.tools.assembler.AssemblyException;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;
import org.apache.avalon.phoenix.tools.installer.Installation;
import org.apache.avalon.phoenix.tools.installer.Installer;
import org.apache.avalon.phoenix.tools.verifier.SarVerifier;
import org.apache.log.Hierarchy;

/**
 * Deploy .sar files into a kernel using this class.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultDeployer
    extends AbstractLogEnabled
    implements Deployer, Serviceable, Initializable, DeployerMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultDeployer.class );

    private final Assembler m_assembler = new Assembler();
    private final SarVerifier m_verifier = new SarVerifier();
    private final Installer m_installer = new Installer();

    private LogManager m_logManager;
    private Kernel m_kernel;
    private ConfigurationRepository m_repository;
    private ClassLoaderManager m_classLoaderManager;
    private DeploymentRecorder m_recorder;

    /**
     * Retrieve relevant services needed to deploy.
     *
     * @param serviceManager the ComponentManager
     * @throws ServiceException if an error occurs
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_kernel = (Kernel)serviceManager.lookup( Kernel.ROLE );
        m_repository = (ConfigurationRepository)serviceManager.lookup( ConfigurationRepository.ROLE );
        m_classLoaderManager = (ClassLoaderManager)serviceManager.lookup( ClassLoaderManager.ROLE );
        m_logManager = (LogManager)serviceManager.lookup( LogManager.ROLE );
        m_recorder = (DeploymentRecorder)serviceManager.lookup( DeploymentRecorder.ROLE );
    }

    public void initialize()
        throws Exception
    {
        setupLogger( m_installer );
        setupLogger( m_assembler );
        setupLogger( m_verifier );
    }

    /**
     * Undeploy an application.
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     */
    public void undeploy( final String name )
        throws DeploymentException
    {
        try
        {
            final Application application = m_kernel.getApplication( name );
            final String[] blocks = application.getBlockNames();

            m_kernel.removeApplication( name );

            for( int i = 0; i < blocks.length; i++ )
            {
                //remove configuration from repository
                m_repository.storeConfiguration( name, blocks[ i ], null );
            }

            final Installation installation = m_recorder.fetchInstallation( name );
            m_installer.uninstall( installation );

            //erase installation information
            m_recorder.recordInstallation( name, null );
        }
        catch( final Exception e )
        {
            throw new DeploymentException( e.getMessage(), e );
        }
    }

    /**
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param sarURL the location to deploy from represented as String
     * @throws DeploymentException if an error occurs
     */
    public void deploy( final String name, final String sarURL )
        throws DeploymentException
    {
        try
        {
            deploy( name, new URL( sarURL ) );
        }
        catch( MalformedURLException mue )
        {
            throw new DeploymentException( mue.getMessage(), mue );
        }
    }

    /**
     * Deploy an application from an installation.
     *
     * @param name the name of application
     * @param location the location to deploy from
     * @throws DeploymentException if an error occurs
     */
    public void deploy( final String name, final URL location )
        throws DeploymentException
    {
        try
        {
            Installation installation = m_recorder.fetchInstallation( name );

            if( null == installation )
            {
                //fresh installation
                installation = m_installer.install( location );
                m_recorder.recordInstallation( name, installation );
            }

            final Configuration config = getConfigurationFor( installation.getConfig() );
            final Configuration server = getConfigurationFor( installation.getEnvironment() );
            final Configuration assembly = getConfigurationFor( installation.getAssembly() );

            final File directory = installation.getDirectory();

            final ClassLoader classLoader =
                m_classLoaderManager.createClassLoader( server,
                                                        installation.getSource(),
                                                        directory,
                                                        installation.getClassPath() );
            //assemble all the blocks for application
            final SarMetaData metaData =
                m_assembler.assembleSar( name, assembly, directory, classLoader );

            m_verifier.verifySar( metaData, classLoader );

            //Setup configuration for all the applications blocks
            setupConfiguration( name, metaData, config.getChildren() );

            final Configuration logs = server.getChild( "logs" );
            final Hierarchy hierarchy = m_logManager.createHierarchy( metaData, logs );

            //Finally add application to kernel
            m_kernel.addApplication( metaData, classLoader, hierarchy, server );

            final String message =
                REZ.getString( "deploy.notice.sar.add", name, installation.getClassPath() );
            getLogger().debug( message );
        }
        catch( final DeploymentException de )
        {
            throw de;
        }
        catch( final AssemblyException ae )
        {
            throw new DeploymentException( ae.getMessage(), ae );
        }
        catch( final Exception e )
        {
            //From classloaderManager/kernel
            throw new DeploymentException( e.getMessage(), e );
        }
    }

    /**
     * Helper method to load configuration data.
     *
     * @param location the location of configuration data as a url
     * @return the Configuration
     * @throws DeploymentException if an error occurs
     */
    private Configuration getConfigurationFor( final String location )
        throws DeploymentException
    {
        try
        {
            return ConfigurationBuilder.build( location );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "deploy.error.config.create", location );
            getLogger().error( message, e );
            throw new DeploymentException( message, e );
        }
    }

    /**
     * Setup Configuration for all the Blocks/BlockListeners in Sar.
     *
     * @param appName the name of Application.
     * @param metaData the SarMetaData.
     * @param configurations the block configurations.
     * @throws DeploymentException if an error occurs
     */
    private void setupConfiguration( final String appName,
                                     final SarMetaData metaData,
                                     final Configuration[] configurations )
        throws DeploymentException
    {
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String name = configuration.getName();

            if( !hasBlock( name, metaData.getBlocks() ) &&
                !hasBlockListener( name, metaData.getListeners() ) )
            {
                final String message = REZ.getString( "deploy.error.extra.config", name );
                throw new DeploymentException( message );
            }

            try
            {
                m_repository.storeConfiguration( appName, name, configuration );
            }
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
