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
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.DeploymentRecorder;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.interfaces.PackageRepository;
import org.apache.avalon.phoenix.interfaces.SystemManager;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="peter@apache.org">Peter Donald</a>
 */
public class DefaultEmbeddor
    extends AbstractLogEnabled
    implements Embeddor, Parameterizable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultEmbeddor.class );

    private static final String DEFAULT_LOG_FILE = "/logs/phoenix.log";
    private static final String DEFAULT_APPS_PATH = "/apps";

    private final static String DEFAULT_FORMAT =
        "%{time} [%7.7{priority}] (%{category}): %{message}\\n%{throwable}";

    private Parameters m_parameters;
    private String m_phoenixHome;

    private ClassLoaderManager m_classLoaderManager;
    private ConfigurationRepository m_repository;
    private Kernel m_kernel;
    private Deployer m_deployer;
    private DeploymentRecorder m_recorder;
    private LogManager m_logManager;
    private SystemManager m_systemManager;
    private PackageRepository m_packageRepository;

    private boolean m_shutdown;

    /**
     * Set parameters for this component.
     * This must be called after contextualize() and before initialize()
     *
     * Make sure to provide all the neccessary information through
     * these parameters. All information it needs consists of strings.
     * There are two types of strings included in parameters. The first
     * type include parameters used to setup proeprties of the embeddor.
     * The second type include the implementation names of the components
     * that the Embeddor manages. For instance if you want to replace the
     * <code>ConfigurationRepository</code> with your own repository you
     * would pass in a parameter such as;</p>
     * <p>org.apache.avalon.phoenix.interfaces.ConfigurationRepository =
     * com.biz.MyCustomConfigurationRepository</p>
     *
     * <p>Of the other type of parameters, the following are supported by
     * the DefaultEmbeddor implementation of Embeddor. Note that some of 
     * the embedded components may support other parameters.</p>
     * <ul>
     * <li><b>phoenix.home</b>, the home directory of phoenix. Defaults 
     * to "..".</li>
     * <li><b>log-destination</b>, the file to save log
     * messages in. If omitted, ${phoenix.home}/logs/phoenix.log is used.</li>
     * <li><b>log-priority</b>, the priority at which log messages are filteres.
     * If omitted, then INFO will be default level used.</li>
     * <li><b>applications-directory</b>, the directory in which
     * the defaul applications to be loaded by the kernel are stored
     * (in .sar format). Defaults to ${phoenix.home}/apps</li>
     * </ul>
     *
     * @param parameters the Parameters for embeddor
     * @exception ParameterException if an error occurs
     */
    public synchronized void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_parameters = createDefaultParameters();
        m_parameters.merge( parameters );

        m_phoenixHome = m_parameters.getParameter( "phoenix.home" );
    }

    /**
     * Creates the core handlers - logger, deployer, Manager and
     * Kernel. Note that these are not set up properly until you have
     * called the <code>run()</code> method.
     */
    public synchronized void initialize()
        throws Exception
    {
        try
        {
            createComponents();

            // setup core handler components
            setupComponent( m_packageRepository, "packages" );
            setupComponent( m_logManager, "logs" );
            setupComponent( m_classLoaderManager, "classes" );
            setupComponent( m_repository, "config" );
            setupComponent( m_deployer, "deployer" );
            setupComponent( m_recorder, "recorder" );
            setupComponent( m_systemManager, "manager" );
            setupComponent( m_kernel, "kernel" );
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
            try
            {
                synchronized( this )
                {
                    wait();
                }
            }
            catch( final InterruptedException e )
            {
            }
        }
    }

    /**
     * Release all the resources associated with kernel.
     */
    public synchronized void dispose()
    {
        shutdown();
        try
        {
            shutdownComponent( m_systemManager );
            shutdownComponent( m_recorder );
            shutdownComponent( m_deployer );
            shutdownComponent( m_kernel );
            shutdownComponent( m_repository );
            shutdownComponent( m_logManager );
            shutdownComponent( m_classLoaderManager );
            shutdownComponent( m_packageRepository );
        }
        catch( final Exception e )
        {
            // whoops!
            final String message = REZ.getString( "embeddor.error.shutdown.failed" );
            getLogger().fatalError( message, e );
        }

        m_packageRepository = null;
        m_systemManager = null;
        m_kernel = null;
        m_repository = null;
        m_classLoaderManager = null;
        m_logManager = null;
        m_deployer = null;
        System.gc(); // make sure resources are released
    }

    /**
     * Request the Embeddor shutsdown.
     */
    public void shutdown()
    {
        m_shutdown = true;
        synchronized( this )
        {
            notifyAll();
        }
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////

    /**
     * Create the logger, deployer and kernel components.
     * Note that these components are not ready to be used
     * until setupComponents() is called.
     */
    private synchronized void createComponents()
        throws Exception
    {
        final Logger logger = createLogger();
        enableLogging( logger );

        String component = null;

        component = m_parameters.getParameter( PackageRepository.ROLE );
        m_packageRepository = (PackageRepository)createComponent( component, PackageRepository.class );

        component = m_parameters.getParameter( ConfigurationRepository.ROLE );
        m_repository = (ConfigurationRepository)createComponent( component, ConfigurationRepository.class );

        component = m_parameters.getParameter( LogManager.ROLE );
        m_logManager = (LogManager)createComponent( component, LogManager.class );

        component = m_parameters.getParameter( ClassLoaderManager.ROLE );
        m_classLoaderManager = (ClassLoaderManager)createComponent( component, ClassLoaderManager.class );

        component = m_parameters.getParameter( Deployer.ROLE );
        m_deployer = (Deployer)createComponent( component, Deployer.class );

        component = m_parameters.getParameter( DeploymentRecorder.ROLE );
        m_recorder = (DeploymentRecorder)createComponent( component, DeploymentRecorder.class );

        component = m_parameters.getParameter( SystemManager.ROLE );
        m_systemManager = (SystemManager)createComponent( component, SystemManager.class );

        component = m_parameters.getParameter( Kernel.ROLE );
        m_kernel = (Kernel)createComponent( component, Kernel.class );
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
            m_parameters.getParameter( "log-destination", m_phoenixHome + DEFAULT_LOG_FILE );
        final String logPriority =
            m_parameters.getParameter( "log-priority", "INFO" );
        final AvalonFormatter formatter = new AvalonFormatter( DEFAULT_FORMAT );
        final File file = new File( logDestination );
        final FileTarget logTarget = new FileTarget( file, false, formatter );

        //Create an anonymous hierarchy so no other
        //components can get access to logging hierarchy
        final Hierarchy hierarchy = new Hierarchy();
        final org.apache.log.Logger logger = hierarchy.getLoggerFor( "Phoenix" );
        logger.setLogTargets( new LogTarget[]{ logTarget } );
        logger.setPriority( Priority.getPriorityForName( logPriority ) );
        logger.info( "Logger started" );
        return new LogKitLogger( logger );
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
        //Name of optional application specified on CLI
        final String application = m_parameters.getParameter( "application-location", null );
        if( null != application )
        {
            final File file = new File( application );
            deployFile( file );
        }
        final String defaultAppsLocation =
            m_parameters.getParameter( "applications-directory", m_phoenixHome + DEFAULT_APPS_PATH );
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
            deployFile( files[ i ] );
        }
    }

    private void deployFile( final File file )
        throws Exception
    {
        final String filename = file.getName();
        int index = filename.lastIndexOf( '.' );
        if( -1 == index ) index = filename.length();
        final String name = filename.substring( 0, index );
        final File canonicalFile = file.getCanonicalFile();
        deployFile( name, canonicalFile );
    }

    protected final synchronized void deployFile( final String name, final File file )
        throws Exception
    {
        m_deployer.deploy( name, file.toURL() );
    }

    /**
     * Setup a component and run it through al of it's
     * setup lifecycle stages.
     *
     * @param component the component
     * @exception Exception if an error occurs
     */
    private void setupComponent( final Component component, final String loggerName )
        throws Exception
    {
        setupLogger( component, loggerName );
        if( component instanceof Composable )
        {
            final ComponentManager componentManager = getComponentManager();
            ( (Composable)component ).compose( componentManager );
        }
        if( component instanceof Parameterizable )
        {
            ( (Parameterizable)component ).parameterize( m_parameters );
        }
        if( component instanceof Initializable )
        {
            ( (Initializable)component ).initialize();
        }
        if( component instanceof Startable )
        {
            ( (Startable)component ).start();
        }
    }

    /**
     * Shutdown a component and run it through al of it's
     * shutdown lifecycle stages.
     *
     * @param component the component
     * @exception Exception if an error occurs
     */
    private void shutdownComponent( final Component component )
        throws Exception
    {
        if( null == component ) return;
        if( component instanceof Startable )
        {
            ( (Startable)component ).stop();
        }
        if( component instanceof Disposable )
        {
            ( (Disposable)component ).dispose();
        }
    }

    /**
     * Create a component that implements an interface.
     *
     * @param component the name of the component
     * @param clazz the name of interface/type
     * @return the created object
     * @exception Exception if an error occurs
     */
    private Object createComponent( final String component, final Class clazz )
        throws Exception
    {
        try
        {
            final Object object = Class.forName( component ).newInstance();
            if( !clazz.isInstance( object ) )
            {
                final String message = REZ.getString( "bad-type.error", component, clazz.getName() );
                throw new Exception( message );
            }
            return object;
        }
        catch( final IllegalAccessException iae )
        {
            final String message = REZ.getString( "bad-ctor.error", clazz.getName(), component );
            throw new CascadingException( message, iae );
        }
        catch( final InstantiationException ie )
        {
            final String message =
                REZ.getString( "no-instantiate.error", clazz.getName(), component );
            throw new CascadingException( message, ie );
        }
        catch( final ClassNotFoundException cnfe )
        {
            final String message =
                REZ.getString( "no-class.error", clazz.getName(), component );
            throw new CascadingException( message, cnfe );
        }
    }

    private ComponentManager getComponentManager()
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        componentManager.put( Embeddor.ROLE, this );
        componentManager.put( LogManager.ROLE, m_logManager );
        componentManager.put( PackageRepository.ROLE, m_packageRepository );
        componentManager.put( ClassLoaderManager.ROLE, m_classLoaderManager );
        componentManager.put( ConfigurationRepository.ROLE, m_repository );
        componentManager.put( Deployer.ROLE, m_deployer );
        componentManager.put( DeploymentRecorder.ROLE, m_recorder );
        componentManager.put( SystemManager.ROLE, m_systemManager );
        componentManager.put( Kernel.ROLE, m_kernel );
        return componentManager;
    }

    /**
     * Create default properties which includes default names of all components.
     * Overide this in sub-classes to change values.
     *
     * @return the Parameters
     */
    protected Parameters createDefaultParameters()
    {
        final Parameters defaults = new Parameters();
        defaults.setParameter( "phoenix.home", ".." );

        final String PREFIX = "org.apache.avalon.phoenix.components.";
        defaults.setParameter( Deployer.ROLE, PREFIX + "deployer.DefaultDeployer" );
        defaults.setParameter( DeploymentRecorder.ROLE, PREFIX + "deployer.DefaultDeploymentRecorder" );
        //defaults.setParameter( DeploymentRecorder.ROLE, PREFIX + "deployer.PersistentDeploymentRecorder" );
        defaults.setParameter( LogManager.ROLE, PREFIX + "logger.DefaultLogManager" );
        defaults.setParameter( Kernel.ROLE, PREFIX + "kernel.DefaultKernel" );
        defaults.setParameter( SystemManager.ROLE, PREFIX + "manager.NoopSystemManager" );
        defaults.setParameter( ConfigurationRepository.ROLE,
                               PREFIX + "configuration.DefaultConfigurationRepository" );
        defaults.setParameter( ClassLoaderManager.ROLE,
                               PREFIX + "classloader.DefaultClassLoaderManager" );
        defaults.setParameter( PackageRepository.ROLE, 
                               PREFIX + "packages.PhoenixPackageRepository" );
        return defaults;
    }

    /**
     * Allow subclasses to get access to kernel.
     *
     * @return the Kernel
     */
    protected final Kernel getKernel()
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
