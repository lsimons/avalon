/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.deployer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.DeployerMBean;
import org.apache.avalon.phoenix.interfaces.DeploymentException;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.tools.assembler.Assembler;
import org.apache.avalon.phoenix.tools.assembler.AssemblyException;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;
import org.apache.avalon.phoenix.components.deployer.installer.Installation;
import org.apache.avalon.phoenix.components.deployer.installer.Installer;
import org.apache.avalon.phoenix.tools.verifier.SarVerifier;
import org.apache.log.Hierarchy;

/**
 * Deploy .sar files into a kernel using this class.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultDeployer
    extends AbstractLogEnabled
    implements Deployer, Parameterizable, Serviceable, Initializable, Disposable, DeployerMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultDeployer.class );

    private final Assembler m_assembler = new Assembler();
    private final SarVerifier m_verifier = new SarVerifier();
    private final Installer m_installer = new Installer();
    private final Map m_installations = new Hashtable();

    private LogManager m_logManager;
    private Kernel m_kernel;
    private ConfigurationRepository m_repository;
    private ClassLoaderManager m_classLoaderManager;

    /**
     * The directory which is used as the base for
     * extracting all temporary files from archives. It is
     * expected that the temporary files will be deleted when
     * the .sar file is undeployed.
     */
    private File m_baseWorkDirectory;

    /**
     * Retrieve parameter that specifies work directory.
     *
     * @param parameters the parameters to read
     * @throws ParameterException if invlaid work directory
     */
    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        final String phoenixHome = parameters.getParameter( "phoenix.home" );
        final String defaultWorkDir = phoenixHome + File.separator + "work";
        final String rawWorkDir =
            parameters.getParameter( "phoenix.work.dir", defaultWorkDir );

        final File dir = new File( rawWorkDir );
        try
        {
            m_baseWorkDirectory = dir.getCanonicalFile();
        }
        catch( final IOException ioe )
        {
            m_baseWorkDirectory = dir.getAbsoluteFile();
        }
    }

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
    }

    public void initialize()
        throws Exception
    {
        initWorkDirectory();

        setupLogger( m_installer );
        setupLogger( m_assembler );
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
            (String[])set.toArray( new String[set.size() ] );
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
                //remove configuration from repository
                m_repository.storeConfiguration( name, blocks[ i ], null );
            }

            m_installer.uninstall( installation, m_baseWorkDirectory );
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
        try
        {
            //m_baseWorkDirectory
            final Installation installation =
                m_installer.install( location, m_baseWorkDirectory );

            final Configuration config = getConfigurationFor( installation.getConfig() );
            final Configuration environment = getConfigurationFor( installation.getEnvironment() );
            final Configuration assembly = getConfigurationFor( installation.getAssembly() );

            final File directory = installation.getDirectory();

            final ClassLoader classLoader =
                m_classLoaderManager.createClassLoader( environment,
                                                        installation.getSource(),
                                                        installation.getDirectory(),
                                                        installation.getWorkDirectory(),
                                                        installation.getClassPath() );
            //assemble all the blocks for application
            final SarMetaData metaData =
                m_assembler.assembleSar( name, assembly, directory, classLoader );

            m_verifier.verifySar( metaData, classLoader );

            //Setup configuration for all the applications blocks
            setupConfiguration( metaData, config.getChildren() );

            final Configuration logs = environment.getChild( "logs" );
            final Hierarchy hierarchy = m_logManager.createHierarchy( metaData, logs );

            //Finally add application to kernel
            m_kernel.addApplication( metaData, classLoader, hierarchy, environment );

            m_installations.put( metaData.getName(), installation );

            final String message =
                REZ.getString( "deploy.notice.sar.add",
                               metaData.getName(),
                               installation.getClassPath() );
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
     * @param metaData the SarMetaData.
     * @param configurations the block configurations.
     * @throws DeploymentException if an error occurs
     */
    private void setupConfiguration( final SarMetaData metaData,
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
                final String message =
                    REZ.getString( "deploy.error.extra.config",
                                   name );
                throw new DeploymentException( message );
            }

            try
            {
                m_repository.storeConfiguration( metaData.getName(),
                                                 name,
                                                 configuration );
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

    /**
     * Make sure that the work directory is created and not a file.
     *
     * @throws Exception if work directory can not be created or is a file
     */
    private void initWorkDirectory()
        throws Exception
    {
        if( !m_baseWorkDirectory.exists() )
        {
            final String message =
                REZ.getString( "deploy.create-dir.notice",
                               m_baseWorkDirectory );
            getLogger().info( message );

            if( !m_baseWorkDirectory.mkdirs() )
            {
                final String error =
                    REZ.getString( "deploy.workdir-nocreate.error",
                                   m_baseWorkDirectory );
                throw new Exception( error );
            }
        }

        if( !m_baseWorkDirectory.isDirectory() )
        {
            final String message =
                REZ.getString( "deploy.workdir-notadir.error",
                               m_baseWorkDirectory );
            throw new Exception( message );
        }
    }
}
