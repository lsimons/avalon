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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.components.ContainerConstants;
import org.apache.avalon.phoenix.components.assembler.AssemblyException;
import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;
import org.apache.avalon.phoenix.containerkit.registry.ComponentProfile;
import org.apache.avalon.phoenix.containerkit.registry.PartitionProfile;
import org.apache.avalon.phoenix.containerkit.registry.ProfileBuilder;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.avalon.phoenix.interfaces.ClassLoaderSet;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.DeployerMBean;
import org.apache.avalon.phoenix.interfaces.DeploymentException;
import org.apache.avalon.phoenix.interfaces.Installation;
import org.apache.avalon.phoenix.interfaces.InstallationException;
import org.apache.avalon.phoenix.interfaces.Installer;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;
import org.apache.avalon.phoenix.tools.verifier.SarVerifier;
import org.apache.avalon.phoenix.tools.verifier.VerifyException;

/**
 * Deploy .sar files into a kernel using this class.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class DefaultDeployer
    extends AbstractLogEnabled
    implements Deployer, Serviceable, Initializable, Disposable, DeployerMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultDeployer.class );

    private final SarVerifier m_verifier = new SarVerifier();
    private final ProfileBuilder m_builder = new PhoenixProfileBuilder();
    private final Map m_installations = new Hashtable();
    private LogManager m_logManager;
    private Kernel m_kernel;
    private Installer m_installer;
    private ConfigurationRepository m_repository;
    private ClassLoaderManager m_classLoaderManager;
    private ConfigurationValidator m_validator;

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
        m_repository = (ConfigurationRepository)serviceManager.
            lookup( ConfigurationRepository.ROLE );
        m_classLoaderManager = (ClassLoaderManager)serviceManager.
            lookup( ClassLoaderManager.ROLE );
        m_logManager = (LogManager)serviceManager.lookup( LogManager.ROLE );
        m_validator = (ConfigurationValidator)serviceManager.lookup( ConfigurationValidator.ROLE );
        m_installer = (Installer)serviceManager.lookup( Installer.ROLE );
    }

    public void initialize()
        throws Exception
    {
        setupLogger( m_builder );
        setupLogger( m_verifier );

    }

    /**
     * Dispose the dpeloyer which effectively means undeploying
     * all the currently deployed apps.
     */
    public void dispose()
    {
        final Set set = m_installations.keySet();
        final String[] applications =
            (String[])set.toArray( new String[ set.size() ] );
        for( int i = 0; i < applications.length; i++ )
        {
            final String name = applications[ i ];
            try
            {
                undeploy( name );
            }
            catch( final DeploymentException de )
            {
                final String message =
                    REZ.getString( "deploy.undeploy-indispose.error",
                                   name,
                                   de.getMessage() );
                getLogger().error( message, de );
            }
        }
    }

    /**
     * Redeploy an application.
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     */
    public void redeploy( final String name )
        throws DeploymentException
    {
        final Installation installation =
            (Installation)m_installations.get( name );
        if( null == installation )
        {
            final String message =
                REZ.getString( "deploy.no-deployment.error", name );
            throw new DeploymentException( message );
        }
        try
        {
            final URL location = installation.getSource().toURL();
            undeploy( name );
            deploy( name, location );
        }
        catch( final Exception e )
        {
            throw new DeploymentException( e.getMessage(), e );
        }
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
        final Installation installation =
            (Installation)m_installations.remove( name );
        if( null == installation )
        {
            final String message =
                REZ.getString( "deploy.no-deployment.error", name );
            throw new DeploymentException( message );
        }
        try
        {
            final Application application = m_kernel.getApplication( name );
            final String[] blocks = application.getBlockNames();

            m_kernel.removeApplication( name );

            for( int i = 0; i < blocks.length; i++ )
            {
                //remove configuration and schema from repository and validator
                m_repository.removeConfiguration( name, blocks[ i ] );
                m_validator.removeSchema( name, blocks[ i ] );
            }

            m_installer.uninstall( installation );
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
        if( m_installations.containsKey( name ) )
        {
            final String message =
                REZ.getString( "deploy.already-deployed.error",
                               name );
            throw new DeploymentException( message );
        }

        /*
         * Clear all the reosurces out of ResourceManager cache
         * so that reloaded applications will have their i18n bundles
         * reloaded.
         */
        ResourceManager.clearResourceCache();

        Installation installation = null;
        boolean success = false;
        try
        {
            //m_baseWorkDirectory
            installation = m_installer.install( name, location );

            final Configuration config = getConfigurationFor( installation.getConfig() );
            final Configuration environment = getConfigurationFor( installation.getEnvironment() );
            final Configuration assembly = getConfigurationFor( installation.getAssembly() );

            final File directory = installation.getHomeDirectory();

            final DefaultContext context = new DefaultContext();
            context.put( BlockContext.APP_NAME, name );
            context.put( BlockContext.APP_HOME_DIR, directory );

            final ClassLoaderSet classLoaderSet =
                m_classLoaderManager.createClassLoaderSet( environment,
                                                           installation.getHomeDirectory(),
                                                           installation.getWorkDirectory() );
            final ClassLoader classLoader = classLoaderSet.getDefaultClassLoader();

            context.put( "classloader", classLoader );

            final Map parameters = new HashMap();
            parameters.put( ContainerConstants.ASSEMBLY_NAME, name );
            parameters.put( ContainerConstants.ASSEMBLY_CONFIG, assembly );
            parameters.put( ContainerConstants.ASSEMBLY_CLASSLOADER, classLoader );

            //assemble all the blocks for application
            final PartitionProfile profile = m_builder.buildProfile( parameters );

            storeConfigurationSchemas( profile, classLoader );
            verify( profile, classLoader );

            //Setup configuration for all the applications blocks
            setupConfiguration( profile.getMetaData(), config.getChildren() );

            final Configuration logs = environment.getChild( "logs" );
            final Logger logger =
                m_logManager.createHierarchy( logs, context );

            //Finally add application to kernel
            m_kernel.addApplication( profile,
                                     installation.getHomeDirectory(),
                                     installation.getWorkDirectory(),
                                     classLoader,
                                     logger,
                                     classLoaderSet.getClassLoaders() );

            m_installations.put( name, installation );

            final String message =
                REZ.getString( "deploy.notice.sar.add",
                               name );
            getLogger().debug( message );
            success = true;
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
        finally
        {
            if( !success && null != installation )
            {
                try
                {
                    m_installer.uninstall( installation );
                }
                catch( final InstallationException ie )
                {
                    getLogger().error( ie.getMessage(), ie );
                }
            }
        }
    }

    /**
     * Verify that the application conforms to our requirements.
     *
     * @param profile the application profile
     * @throws VerifyException on error
     */
    private void verify( final PartitionProfile profile,
                         final ClassLoader classLoader )
        throws VerifyException
    {
        try
        {
            m_verifier.verifySar( profile, classLoader );
        }
        catch( org.apache.avalon.phoenix.framework.tools.verifier.VerifyException e )
        {
            throw new VerifyException( e.getMessage(), e.getCause() );
        }
    }

    /**
     * Store the configuration schemas for this application
     *
     * @param profile the application profile
     * @throws DeploymentException upon invalid schema
     */
    private void storeConfigurationSchemas( final PartitionProfile profile,
                                            final ClassLoader classLoader )
        throws DeploymentException
    {
        final String application = profile.getMetaData().getName();
        final PartitionProfile partition = profile.getPartition( ContainerConstants.BLOCK_PARTITION );
        final ComponentProfile[] blocks = partition.getComponents();
        int i = 0;
        String name = null;

        try
        {
            for( i = 0; i < blocks.length; i++ )
            {
                final ComponentProfile block = blocks[ i ];
                final SchemaDescriptor descriptor = block.getInfo().getConfigurationSchema();
                name = block.getMetaData().getName();

                if( null != descriptor && !descriptor.getType().equals( "" ) )
                {
                    final String implementationKey =
                        block.getInfo().getDescriptor().getImplementationKey();

                    m_validator.addSchema( application,
                                           name,
                                           descriptor.getType(),
                                           getConfigurationSchemaURL( name,
                                                                      implementationKey,
                                                                      classLoader )
                    );
                }
            }
        }
        catch( ConfigurationException e )
        {
            //uh-oh, bad schema bad bad!
            final String message =
                REZ.getString( "deploy.error.config.schema.invalid",
                               name );

            //back out any schemas that we have already stored for this app
            while( --i >= 0 )
            {
                m_validator.removeSchema( application,
                                          blocks[ i ].getMetaData().getName() );
            }

            throw new DeploymentException( message, e );
        }
    }

    private String getConfigurationSchemaURL( final String name,
                                              final String classname,
                                              final ClassLoader classLoader )
        throws DeploymentException
    {
        final String resourceName = classname.replace( '.', '/' ) + "-schema.xml";

        final URL resource = classLoader.getResource( resourceName );
        if( null == resource )
        {

            final String message =
                REZ.getString( "deploy.error.config.schema.missing",
                               name,
                               resourceName );
            throw new DeploymentException( message );
        }
        else
        {
            return resource.toString();
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
     * @param metaData the SarMetaData.
     * @param configurations the block configurations.
     * @throws DeploymentException if an error occurs
     */
    private void setupConfiguration( final PartitionMetaData metaData,
                                     final Configuration[] configurations )
        throws DeploymentException
    {
        final String application = metaData.getName();
        final PartitionMetaData listenerPartition =
            metaData.getPartition( ContainerConstants.LISTENER_PARTITION );
        final PartitionMetaData blockPartition =
            metaData.getPartition( ContainerConstants.BLOCK_PARTITION );
        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String name = configuration.getName();
            final boolean listener =
                null != listenerPartition.getComponent( name );
            final boolean block =
                null != blockPartition.getComponent( name );
            if( !block && !listener )
            {
                final String message =
                    REZ.getString( "deploy.error.extra.config",
                                   name );
                throw new DeploymentException( message );
            }

            try
            {
                //No way to validate listener configuration--yet
                if( listener || m_validator.isFeasiblyValid( application, name, configuration ) )
                {
                    m_repository.storeConfiguration( application,
                                                     name,
                                                     configuration );
                }
                else
                {
                    final String message =
                        REZ.getString( "deploy.error.config.invalid", name );
                    throw new DeploymentException( message );
                }
            }
            catch( final ConfigurationException ce )
            {
                throw new DeploymentException( ce.getMessage(), ce );
            }
        }
    }
}
