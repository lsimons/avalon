/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.io.File;
import java.lang.UnsupportedOperationException;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.atlantis.Embeddor;
import org.apache.avalon.framework.atlantis.Kernel;
import org.apache.avalon.framework.atlantis.SystemManager;
import org.apache.avalon.framework.camelot.CamelotUtil;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.camelot.Deployer;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.LogKit;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.FileOutputLogTarget;
import org.apache.avalon.phoenix.engine.facilities.log.AvalonLogFormatter;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="donaldp@apache.org">Peter Donald</a>
 */
public class PhoenixEmbeddor
    extends AbstractLoggable
    implements Embeddor
{
    private Parameters     m_parameters;
    private Kernel         m_kernel;
    private Deployer       m_deployer;
    private SystemManager  m_systemManager;

    private boolean        m_shutdown;

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
     * @exception ConfigurationException if an error occurs
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

            //TODO: Deploying should go into execute!!!
            deployDefaultApplications();

            m_systemManager.start();

            m_kernel.start();

            //Uncomment next bit to try registering...

            //TODO: Logger and SystemManager itself aswell???
            //m_systemManager.register( "Phoenix.Kernel", m_kernel );
            //m_systemManager.register( "Phoenix.Embeddor", this );
        }
        catch( final Exception e )
        {
            // whoops!
            getLogger().fatalError( "There was a fatal error while starting phoenix.", e );
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
        //TODO: Insert default deployment here...

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
        throws Exception
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

        m_deployer = null;

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

        try
        {
            m_deployer = createDeployer();
        }
        catch( final Exception e )
        {
            final String message = "Unable to create deployer!";
            getLogger().fatalError( message, e );
            throw new CascadingException( message, e );
        }

        try
        {
            m_systemManager = createSystemManager();
        }
        catch( final Exception e )
        {
            final String message = "Unable to create SystemManager!";
            getLogger().fatalError( message, e );
            throw new CascadingException( message, e );
        }

        try
        {
            m_kernel = createKernel();
        }
        catch( final Exception e )
        {
            final String message = "Unable to create kernel!";
            getLogger().fatalError( message, e );
            throw new CascadingException( message, e );
        }
    }

    /**
     * Setup the deployer and kernel components.
     * Note that after this method these components are ready to be used
     */
    private void setupComponents()
        throws Exception
    {
        try
        {
            setupDeployer();
        }
        catch( final Exception e )
        {
            getLogger().fatalError( "Unable to setup deployer!", e );
            throw e;
        }

        try
        {
            setupSystemManager();
        }
        catch( final Exception e )
        {
            getLogger().fatalError( "Unable to setup SystemManager!", e );
            throw e;
        }

        try
        {
            setupKernel();
        }
        catch( final Exception e )
        {
            getLogger().fatalError( "Unable to setup kernel!", e );
            throw e;
        }
    }

    /**
     * Uses <code>org.apache.log.LogKit</code> to create a new
     * logger using "Phoenix" as its category, DEBUG as its
     * priority and the log-destination from Parameters as its
     * destination.
     * TODO: allow configurable priorities and multiple
     * logtargets.
     */
    private Logger createLogger()
        throws ConfigurationException
    {
        try
        {
            final String logDestination =
                m_parameters.getParameter( "log-destination", null );

            final FileOutputLogTarget logTarget = new FileOutputLogTarget( logDestination );
            final AvalonLogFormatter formatter = new AvalonLogFormatter();
            formatter.setFormat( "%{time} [%7.7{priority}] <<%{category}>> " +
                                 "(%{context}): %{message}\\n%{throwable}" );
            logTarget.setFormatter( formatter );

            final Logger logger = LogKit.getLoggerFor( "Phoenix" );
            logger.setLogTargets( new LogTarget[] { logTarget } );

            logger.info( "Logger started" );
            return logger;
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Logger", e );
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
        final String className = m_parameters.getParameter( "deployer-class", null );
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            return (Deployer)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Deployer of class " +
                                              className, e );
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
            componentManager.put( "org.apache.avalon.framework.camelot.Container", (Container)m_kernel );
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
    private void deployDefaultApplications()
        throws Exception
    {
        final String defaultAppsLocation =
            m_parameters.getParameter( "applications-directory", null );

        if( null != defaultAppsLocation )
        {
            final File directory = new File( defaultAppsLocation );
            CamelotUtil.deployFromDirectory( m_deployer, directory, ".sar" );
        }

        // TODO: load facilities from .fars
        // final File directory2 = new File( (String)this.context.get( "default-facilities-location" ) );
        // CamelotUtil.deployFromDirectory( deployer, directory2, ".far" );
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
        final String className = m_parameters.getParameter( "manager-class", null );
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            return (SystemManager)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create SystemManager of class " +
                                              className, e );
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

        try
        {
            m_systemManager.initialize();
        }
        catch( final Exception e )
        {
            getLogger().fatalError( "There was a fatal error; " +
                                    "phoenix's SystemManager could not be started", e );
            throw e;
        }
    }

    /**
     * Creates a new kernel from the Parameters's kernel-class parameter.
     *
     * @return the created Kernel
     * @exception ConfigurationException if an error occurs
     */
    private Kernel createKernel()
        throws ConfigurationException
    {
        final String className = m_parameters.getParameter( "kernel-class", null );
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            return (Kernel)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Kernel of class " +
                                              className, e );
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
            componentManager.put( "org.apache.avalon.framework.atlantis.SystemManager", m_systemManager );
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

        try
        {
            m_kernel.initialize();
        }
        catch( final Exception e )
        {
            getLogger().fatalError( "There was a fatal error; phoenix could not be started", e );
            throw e;
        }
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
