/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import java.io.File;
import java.lang.UnsupportedOperationException;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.CascadingException;
import org.apache.avalon.Composer;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.DefaultComponentManager;
import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.atlantis.Kernel;
import org.apache.avalon.atlantis.Embeddor;
import org.apache.avalon.camelot.CamelotUtil;
import org.apache.avalon.camelot.Container;
import org.apache.avalon.camelot.Deployer;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.configuration.Parameters;
import org.apache.avalon.util.log.AvalonLogFormatter;
import org.apache.log.LogKit;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.FileOutputLogTarget;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * Note that this will eventually be moved to atlantis package.
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
     * org.apache.phoenix.engine.ServerKernel to be used.</li>
     * <li><b>deployer-class</b>, the classname of the
     * org.apache.avalon.camelot.Deployer to be used.</li>
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
    public void setParameters( final Parameters parameters )
        throws ConfigurationException
    {
        m_parameters = parameters;
    }

    /**
     * Creates the core handlers - logger, deployer, Manager and
     * Kernel. Note that these are not set up properly until you have
     * called the <code>run()</code> method.
     */
    public void init()
        throws Exception
    {
        createComponents();
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
        try
        {
            // setup core handler components
            setupComponents();
            deployDefaultApplications();

            m_kernel.start();

            // loop until <code>Shutdown</code> is created.
            while( !m_shutdown )
            {
                // loop

                // wait() for shutdown() to take action...
                try { synchronized( this ) { wait(); } }
                catch( final InterruptedException e ) {}
            }
        }
        catch( final Exception e )
        {
            // whoops!
            getLogger().fatalError( "There was a fatal error while running phoenix.", e );
            throw e;
        }
    }

    /**
     * Release all the resources associated with kernel.
     */
    public void dispose()
        throws Exception
    {
        if( null != m_kernel )
        {
            m_kernel.stop();
            m_kernel.dispose();
        }

        m_kernel = null;
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

            LogKit.addLogTarget( logDestination, logTarget );
            final Logger logger =
                LogKit.createLogger( LogKit.createCategory( "Phoenix", Priority.DEBUG ),
                                     new LogTarget[] { logTarget } );
            logger.info( "Logger started" );
            return logger;
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Logger", e );
        }
    }

    /**
     * Creates a new deployer from the Parameters's deployer-class.
     * TODO: fill the Parameters for Deployer properly.
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
     * passed a Context. If it is a Composer it is given a
     * ComponentManager which references the Kernel, cast to a
     * Container.
     * The deployer is now used to load the applications from the
     * default-facilities-location specified in Context.
     * TODO: load facilities from .fars as well.
     */
    private void setupDeployer()
        throws Exception
    {
        setupLogger( m_deployer );

        if( m_deployer instanceof Composer )
        {
            final DefaultComponentManager componentManager = new DefaultComponentManager();
            componentManager.put( "org.apache.avalon.camelot.Container", (Container)m_kernel );
            ((Composer)m_deployer).compose( componentManager );
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
     * Creates a new deployer from the Parameters's kernel-class.
     * TODO: fill the Parameters for kernel properly.
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
     */
    private void setupKernel()
        throws Exception
    {
        setupLogger( m_kernel );

        if( m_kernel instanceof Configurable )
        {
            final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            final String kernelConfigLocation =
                m_parameters.getParameter( "kernel-configuration-source", null );
            final Configuration configuration = builder.build( kernelConfigLocation );

            ((Configurable)m_kernel).configure( configuration );
        }

        try
        {
            m_kernel.init();
        }
        catch( final Exception e )
        {
            getLogger().fatalError( "There was a fatal error; phoenix could not be started", e );
            throw e;
        }
    }
}
