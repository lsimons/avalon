/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.embeddor;

import java.io.File;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.manager.SystemManager;
import org.apache.avalon.phoenix.components.installer.Installer;
import org.apache.avalon.phoenix.components.installer.Installation;
import org.apache.avalon.phoenix.components.deployer.Deployer;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="donaldp@apache.org">Peter Donald</a>
 */
public class DefaultEmbeddor
    extends AbstractLoggable
    implements Embeddor, Parameterizable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultEmbeddor.class );

    private static final String    PHOENIX_HOME         =
        System.getProperty( "phoenix.home", ".." );

    private static final String    DEFAULT_LOG_FILE     = PHOENIX_HOME + "/logs/phoenix.log";
    private static final String    DEFAULT_APPS_PATH    = PHOENIX_HOME + "/apps";

    private static final String    DEFAULT_INSTALLER    =
        System.getProperty( "phoenix.installer",
                            "org.apache.avalon.phoenix.components.installer.DefaultInstaller" );

    private static final String    DEFAULT_DEPLOYER     =
        System.getProperty( "phoenix.deployer",
                            "org.apache.avalon.phoenix.components.deployer.DefaultSarDeployer" );

    private static final String    DEFAULT_KERNEL       =
        System.getProperty( "phoenix.kernel",
                            "org.apache.avalon.phoenix.components.kernel.DefaultKernel" );

    private static final String    DEFAULT_MANAGER      =
        System.getProperty( "phoenix.manager",
                            "org.apache.avalon.phoenix.components.manager.NoopSystemManager" );

    private static final String    DEFAULT_REPOSITORY   =
        System.getProperty( "phoenix.repository",
                            "org.apache.avalon.phoenix.components.configuration.DefaultConfigurationRepository" );

    private final static String  DEFAULT_FORMAT =
        "%{time} [%7.7{priority}] <<%{category}>> (%{context}): %{message}\\n%{throwable}";

    private Parameters     m_parameters;

    private Application              m_kernel;
    private Installer                m_installer;
    private Deployer                 m_deployer;
    private SystemManager            m_systemManager;
    private ConfigurationRepository  m_repository;

    private boolean                  m_shutdown;

    /**
     * Set parameters for this component.
     * This must be called after contextualize() and before initialize()
     *
     * Make sure to provide all the neccessary information through
     * these parameters. All information it needs consists of strings.
     * Neccessary are:
     * <ul>
     * <li><b>kernel-class</b>, the classname of the
     * org.apache.avalon.phoenix.engine.ServerKernel to be used.</li>
     * <li><b>deployer-class</b>, the classname of the
     * org.apache.avalon.framework.camelot.Deployer to be used.</li>
     * <li><b>kernel-configuration-source</b>, the location
     * of the configuration file to be used for configuring the
     * kernel. (If kernel is configurable)</li>
     * <li><b>log-destination</b>, the file to save log
     * messages in. If omitted, no logs are written.</li>
     * <li>TODO: <b>facilities-directory</b>, the directory in
     * which the  facilities you wish to load into the kernel
     * are stored (in .far format).<br />
     * When ommited, the default facilities are used.</li>
     * <li><b>applications-directory</b>, the directory in which
     * the defaul applications to be loaded by the kernel are stored
     * (in .sar format).<br />
     * When ommited, no applications are loaded.</li>
     * </ul>
     *
     * @param parameters the Parameters for embeddor
     * @exception ParameterException if an error occurs
     */
    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_parameters = parameters;
    }

    /**
     * Creates the core handlers - logger, deployer, Manager and
     * Kernel. Note that these are not set up properly until you have
     * called the <code>run()</code> method.
     */
    public void initialize()
        throws Exception
    {
        createComponents();
    }

    /**
     * Setup and Start the Logger, Deployer, SystemManager and Kernel.
     *
     * @exception Exception if an error occurs
     */
    public void start()
        throws Exception
    {
        try
        {
            // setup core handler components
            setupComponents();
            m_systemManager.start();
            m_kernel.start();

            //Uncomment next bit to try registering...
            //TODO: Logger and SystemManager itself aswell???
            m_systemManager.register( "Phoenix.Kernel", m_kernel );
            m_systemManager.register( "Phoenix.Embeddor", this );
        }
        catch( final Exception e )
        {
            // whoops!
            final String message = REZ.getString( "embeddor.error.start.failed" );
            getLogger().fatalError( message, e );
            throw e;
        }
    }

    /**
     * This is the main method of the embeddor. It sets up the core
     * components, and then deploys the <code>Facilities</code>. These
     * are registered with the Kernel and the Manager. The same
     * happens for the <code>Applications</code>.
     * Now, the Kernel is taken through its lifecycle. When it is
     * finished, as well as all the applications running in it, it
     * is shut down, after which the PhoenixEmbeddor is as well.
     */
    public void execute()
        throws Exception
    {
        deployDefaultApplications();

        // loop until <code>Shutdown</code> is created.
        while( !m_shutdown )
        {
            // wait() for shutdown() to take action...
            try { synchronized( this ) { wait(); } }
            catch( final InterruptedException e ) {}
        }
    }

    /**
     * Shutdown all the resources associated with kernel.
     */
    public void stop()
        throws Exception
    {
        if( null != m_systemManager )
        {
            m_systemManager.unregister( "Phoenix.Kernel" );
            m_systemManager.unregister( "Phoenix.Embeddor" );
            m_systemManager.stop();
        }

        if( null != m_kernel )
        {
            m_kernel.stop();
        }
    }

    /**
     * Release all the resources associated with kernel.
     */
    public void dispose()
    {
        if( null != m_systemManager )
        {
            m_systemManager.dispose();
            m_systemManager = null;
        }

        if( null != m_kernel )
        {
            m_kernel.dispose();
            m_kernel = null;
        }

        m_repository = null;
        m_deployer = null;
        m_installer = null;

        System.gc(); // make sure resources are released
    }

    /**
     * Shut down the Embeddor together with the
     * Logger, Deployer, Manager and Kernel.
     */
    public void shutdown()
    {
        m_shutdown = true;
        synchronized( this ) { notifyAll(); }
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////

    /**
     * Create the logger, deployer and kernel components.
     * Note that these components are not ready to be used
     * until setupComponents() is called.
     */
    private void createComponents()
        throws Exception
    {
        final Logger logger = createLogger();
        setLogger( logger );

        m_installer = createInstaller();
        m_repository = createRepository();
        m_deployer = createDeployer();
        m_systemManager = createSystemManager();
        m_kernel = createKernel();
    }

    /**
     * Setup the deployer and kernel components.
     * Note that after this method these components are ready to be used
     */
    private void setupComponents()
        throws Exception
    {
        setupLogger( m_repository );
        setupLogger( m_installer );

        try
        {
            setupDeployer();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.setup.deployer" );
            getLogger().fatalError( message, e );
            throw e;
        }

        try
        {
            setupSystemManager();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.setup.manager" );
            getLogger().fatalError( message, e );
            throw e;
        }

        try
        {
            setupKernel();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.setup.kernel" );
            getLogger().fatalError( message, e );
            throw e;
        }
    }

    /**
     * Uses <code>org.apache.log.Hierarchy</code> to create a new
     * logger using "Phoenix" as its category, DEBUG as its
     * priority and the log-destination from Parameters as its
     * destination.
     * TODO: allow configurable priorities and multiple
     * logtargets.
     */
    private Logger createLogger()
        throws Exception
    {
        final String logDestination =
            m_parameters.getParameter( "log-destination", DEFAULT_LOG_FILE );

        final String logPriority =
            m_parameters.getParameter( "log-priority", "INFO" );

        final AvalonFormatter formatter = new AvalonFormatter( DEFAULT_FORMAT );
        final File file = new File( logDestination );
        final FileTarget logTarget = new FileTarget( file, false, formatter );

        //Create an anonymous hierarchy so no other
        //components can get access to logging hierarchy
        final Hierarchy hierarchy = new Hierarchy();
        final Logger logger = hierarchy.getLoggerFor( "Phoenix" );
        logger.setLogTargets( new LogTarget[] { logTarget } );
        logger.setPriority( Priority.getPriorityForName( logPriority ) );

        logger.info( "Logger started" );
        return logger;
    }

    /**
     * Creates a new repository from the Parameters's repository-class variable.
     *
     * @return the new ConfigurationRepository
     * @exception ConfigurationException if an error occurs
     */
    private ConfigurationRepository createRepository()
        throws ConfigurationException
    {
        final String className =
            m_parameters.getParameter( "repository-class", DEFAULT_REPOSITORY );
        try
        {
            return (ConfigurationRepository)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.create.repository", className );
            getLogger().warn( message, e );
            throw new ConfigurationException( message, e );
        }
    }

    /**
     * Creates a new installer from the Parameters's installer-class variable.
     *
     * @return the new Installer
     * @exception ConfigurationException if an error occurs
     */
    private Installer createInstaller()
        throws ConfigurationException
    {
        final String className =
            m_parameters.getParameter( "installer-class", DEFAULT_INSTALLER );
        try
        {
            return (Installer)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.create.installer", className );
            getLogger().warn( message, e );
            throw new ConfigurationException( message, e );
        }
    }

    /**
     * Creates a new deployer from the Parameters's deployer-class variable.
     *
     * @return the new Deployer
     * @exception ConfigurationException if an error occurs
     */
    private Deployer createDeployer()
        throws ConfigurationException
    {
        final String className =
            m_parameters.getParameter( "deployer-class", DEFAULT_DEPLOYER );
        try
        {
            return (Deployer)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.create.deployer", className );
            getLogger().warn( message, e );
            throw new ConfigurationException( message, e );
        }
    }

    /**
     * Sets up the Deployer. If it is Loggable, it gets a reference
     * to the Embeddor's logger. If it is Contextualizable it is
     * passed a Context. If it is a Composable it is given a
     * ComponentManager which references the Kernel, cast to a
     * Container.
     */
    private void setupDeployer()
        throws Exception
    {
        setupLogger( m_deployer );

        if( m_deployer instanceof Composable )
        {
            final DefaultComponentManager componentManager = new DefaultComponentManager();
            componentManager.put( Container.ROLE, (Container)m_kernel );
            componentManager.put( ConfigurationRepository.ROLE, m_repository );
            componentManager.put( Installer.ROLE, m_installer );
            ((Composable)m_deployer).compose( componentManager );
        }
    }

    /**
     * The deployer is used to load the applications from the
     * default-apps-location specified in Parameters.
     * TODO: load facilities from .fars as well.
     *
     * @exception Exception if an error occurs
     */
    protected void deployDefaultApplications()
        throws Exception
    {
        final String defaultAppsLocation =
            m_parameters.getParameter( "applications-directory", DEFAULT_APPS_PATH );

        if( null != defaultAppsLocation )
        {
            final File directory = new File( defaultAppsLocation );

            final ExtensionFileFilter filter = new ExtensionFileFilter( ".sar" );
            final File[] files = directory.listFiles( filter );
            if( null != files )
            {
                deployFiles( files );
            }
        }
    }

    private void deployFiles( final File[] files )
        throws Exception
    {
        for( int i = 0; i < files.length; i++ )
        {
            final String filename = files[ i ].getName();

            int index = filename.lastIndexOf( '.' );
            if( -1 == index ) index = filename.length();

            final String name = filename.substring( 0, index );
            final File file = files[ i ].getCanonicalFile();

            deployFile( name, file );
        }
    }

    protected final void deployFile( final String name, final File file )
        throws Exception
    {
        final Installation installation = m_installer.install( name, file.toURL() );
        m_deployer.deploy( name, installation );
    }

    /**
     * Creates a new SystemManager from the Parameters's manager-class parameter.
     *
     * @return the created SystemManager
     * @exception ConfigurationException if an error occurs
     */
    private SystemManager createSystemManager()
        throws ConfigurationException
    {
        final String className =
            m_parameters.getParameter( "manager-class", DEFAULT_MANAGER );
        try
        {
            return (SystemManager)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.create.manager", className );
            getLogger().warn( message, e );
            throw new ConfigurationException( message, e );
        }
    }

    /**
     * Sets up the SystemManager. We determine whether it supports Loggable
     * and Configurable and supply information based on that.
     *
     * @exception Exception if an error occurs
     */
    private void setupSystemManager()
        throws Exception
    {
        setupLogger( m_systemManager );

        if( m_systemManager instanceof Parameterizable )
        {
            ((Parameterizable)m_systemManager).parameterize( m_parameters );
        }
        else if( m_systemManager instanceof Configurable )
        {
            final String location = m_parameters.getParameter( "manager-configuration-source", null );
            final Configuration configuration = getConfigurationFor( location );

            ((Configurable)m_systemManager).configure( configuration );
        }

        m_systemManager.initialize();
    }

    /**
     * Creates a new kernel from the Parameters's kernel-class parameter.
     *
     * @return the created Kernel
     * @exception ConfigurationException if an error occurs
     */
    private Application createKernel()
        throws ConfigurationException
    {
        final String className = m_parameters.getParameter( "kernel-class", DEFAULT_KERNEL );
        try
        {
            return (Application)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "embeddor.error.create.kernel", className );
            getLogger().warn( message, e );
            throw new ConfigurationException( message, e );
        }
    }

    /**
     * Sets up the Kernel. We determine whether it supports Loggable
     * and Configurable and supply information based on that.
     *
     * @exception Exception if an error occurs
     */
    private void setupKernel()
        throws Exception
    {
        setupLogger( m_kernel );

        if( m_kernel instanceof Composable )
        {
            final DefaultComponentManager componentManager = new DefaultComponentManager();
            componentManager.put( SystemManager.ROLE, m_systemManager );
            componentManager.put( ConfigurationRepository.ROLE, m_repository );
            ((Composable)m_kernel).compose( componentManager );
        }

        /*
          if( m_kernel instanceof Parameterizable )
          {
          final String location = m_parameters.getParameter( "kernel-parameters-source", null );
          final Parameters parameters = getParametersFor( location );

          ((Parameterizable)m_kernel).parameterize( parameters );
          }
          else
        */

        if( m_kernel instanceof Configurable )
        {
            final String location = m_parameters.getParameter( "kernel-configuration-source", null );
            final Configuration configuration = getConfigurationFor( location );

            ((Configurable)m_kernel).configure( configuration );
        }

        m_kernel.initialize();
    }

    /**
     * Allow subclasses to get access to deployer.
     *
     * @return the Deployer
     */
    protected final Deployer getDeployer()
    {
        return m_deployer;
    }

    /**
     * Allow subclasses to get access to kernel.
     *
     * @return the Kernel
     */
    protected final Application getKernel()
    {
        return m_kernel;
    }

    /**
     * Allow subclasses to get access to parameters.
     *
     * @return the Parameters
     */
    protected final Parameters getParameters()
    {
        return m_parameters;
    }

    /**
     * Helper method to retrieve configuration from a location on filesystem.
     *
     * @param location the location of configuration
     * @return the configuration
     * @exception Exception if an error occurs
     */
    private Configuration getConfigurationFor( final String location )
        throws Exception
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        return builder.buildFromFile( location );
    }
}
