/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import javax.management.MBeanServer;
import java.io.File;

import org.apache.framework.configuration.Configurable;
import org.apache.framework.context.Contextualizable;
import org.apache.framework.container.Container;
import org.apache.framework.component.Composer;
import org.apache.framework.logger.Loggable;
import org.apache.framework.container.Deployer;
import org.apache.framework.lifecycle.Suspendable;
import org.apache.framework.lifecycle.Resumable;

import org.apache.framework.context.Context;
import org.apache.framework.container.Entry;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.ConfigurationException;

import org.apache.avalon.camelot.CamelotUtil;
import org.apache.avalon.atlantis.Kernel;
import org.apache.avalon.context.DefaultContext;
import org.apache.avalon.component.DefaultComponentManager;
import org.apache.avalon.configuration.DefaultConfigurationBuilder;

import org.apache.phoenix.engine.facilities.Manager;

import org.apache.log.format.AvalonLogFormatter;
import org.apache.log.output.FileOutputLogTarget;
import org.apache.log.Logger;
import org.apache.log.LogKit;
import org.apache.log.Priority;
import org.apache.log.LogTarget;

/**
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public class DefaultEmbeddor
    implements Embeddor
{
    private Context         context;

    private Logger          logger;
    private Deployer        deployer;
        private DefaultContext      deployerContext;
    private Manager         manager;
        private MBeanServer         mBeanServer;
        private DefaultContext      managerContext;
    private Kernel          kernel;
        private Thread              kernelThread;
        private DefaultContext      kernelContext;
        private Configuration       kernelConfiguration;
        private boolean             kernelSupportsSuspend = false;

    // monitor these for status changes
    private boolean shutdown = false;
    private boolean restart = false;
    private boolean suspend = false;
    private boolean recontextualized = false;
    private boolean reconfigured = false;

    /////////////////////////
    /// LIFECYCLE METHODS ///
    /////////////////////////
    /**
     * Make sure to provide all the neccessary information through
     * this context. All information it needs consists of strings.
     * These are also indexed using strings. Neccessary are:
     * <ul>
     * <li><b>kernel-class</b>, the classname of the
     * org.apache.phoenix.core.Kernel to be used.</li>
     * <li><b>deployer-class</b>, the classname of the
     * org.apache.avalon.camelot.Deployer to be used.</li>
     * <li><b>mBeanServer-class</b>, the classname of the
     * javax.management.MBeanServer to be used.</li>
     * <li><b>kernel-configuration-source</b>, the location
     * of the configuration file to be used for configuring the
     * kernel.</li>
     * <li><b>log-destination</b>, the file to save log
     * messages in. If omitted, no logs are written.</li>
     * <li>TODO: <b>facilities-directory</b>, the directory in
     * which the  facilities you wish to load into the kernel
     * are stored (in .far format).<br />
     * When ommited, the default facilities are used.</li>
     * <li><b>applications-directory</b>, the directory in which
     * the applications to be loaded by the kernel are stored
     * (in .sar format).<br />
     * When ommited, no applications are loaded.</li>
     * </ul>
     */
    public void contextualize( Context context )
    {
        this.context = context;
    }
    /**
     * Currently unused, but Embeddor extends it so call anyway
     * using a dummy configuration.
     */
    void configure( Configuration configuration )
        throws ConfigurationException;
    {
        // TODO
    }
    /**
     * Creates the core handlers - logger, deployer, Manager and
     * Kernel. Note that these are not set up properly until you have
     * called the <code>run()</code> method.
     */
    public void init()
        throws Exception
    {
        this.createLogger();
        try { this.createDeployer(); }
        catch( Exception e ) { logger.fatalError("Unable to create deployer!", e);
                               throw new Exception(e); }
        try { this.createManager(); }
        catch( Exception e ) { logger.fatalError("Unable to create manager!", e);
                               throw new Exception(e); }
        try { this.createKernel(); }
        catch( Exception e ) { logger.fatalError("Unable to create kernel!", e);
                               throw new Exception(e); }
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
    public void run()
    {
        this.setupLogger();
        try { this.setupDeployer(); }
        catch( Exception e ) {  logger.fatalError("Unable to setup deployer!", e);
                                System.exit( 1 ); }
        try { this.setupManager(); }
        catch( Exception e ) {  logger.fatalError("Unable to setup manager!", e);
                                System.exit( 1 ); }
        try { this.setupKernel(); }
        catch( Exception e ) {  logger.fatalError("Unable to setup kernel!", e);
                                System.exit( 1 ); }

        try
        {
            kernel.start();

            while( !this.shutdown )
            {
                while( !this.restart && !this.suspend )
                {
                    kernel.run();
                    try { synchronized( this ) { wait(); } }
                    catch (InterruptedException e) {}
                }
                if( this.restart )
                {
                    handleRestart();
                }
                else if( this.suspend )
                {
                    handleSuspend();
                }
            }
            handleDispose();
        }
        catch ( Exception e )
        {
            // whoops!
            this.logger.fatalError("There was a fatal error while running phoenix.", e );
            System.exit( 1 );
        }
    }

    /**
     * This stops and then restarts the Embeddor,
     * re-creating the logger, deployer, Manager
     * and Kernel in the process.
     */
    public void restart()
    {
        this.restart = true;
    }
    /**
     * Shut down the Embeddor together with the
     * Logger, Deployer, Manager and Kernel.
     */
    public void dispose()
    {
        this.shutdown = true;
    }
    /**
     * Suspend both the Embeddor and its Manager.
     * If the Kernel does not support suspend,
     * this shuts down the Kernel and all
     * Applications running in it. This is not
     * recommended!
     */
    public void suspend()
    {
        this.suspend = true;
    }
    /**
     * Resume both the Embeddor and its Manager.
     * If the Kernel does not support suspend, it
     * has to be re-created and re-setup. This
     * happens now.
     */
    public void resume()
    {
        try
        {
            if( this.kernelSupportsSuspend )
            {
                if( kernel instanceof Resumable )
                {
                    manager.resume();
                    ((Resumable)kernel).resume();

                    this.suspend = false;
                    synchronized( this ) { notifyAll(); }
                } else
                {
                    // it's stupid if this happens
                    // but let's handle it anyway...
                    kernel.stop();
                    kernel.dispose();
                    kernel = null;
                    this.createKernel();
                    this.setupKernel();

                    manager.resume();
                    kernel.start();

                    this.suspend = false;
                    synchronized( this ) { notifyAll(); }
                }
            } else
            {
                this.createKernel();
                this.setupKernel();

                manager.resume();
                kernel.start();

                this.suspend = false;
                synchronized( this ) { notifyAll(); }
            }
        }
        catch( Exception e )
        {
            // this is bad...
            logger.fatalError( "A fatal error occured while resuming. Phoenix will exit.", e );
            System.exit( 1 );
        }
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    private void createLogger() throws ConfigurationException
    {
        try
        {
            final FileOutputLogTarget logTarget = new FileOutputLogTarget( (String)this.context.get( "log-destination" ) );
            final AvalonLogFormatter formatter = new AvalonLogFormatter();
            formatter.setFormat( "%{time} [%7.7{priority}] <<%{category}>> " +
                                 "(%{context}): %{message}\\n%{throwable}" );
            logTarget.setFormatter( formatter );

            LogKit.addLogTarget( (String)this.context.get( "log-destination" ), logTarget );
            this.logger = LogKit.createLogger( LogKit.createCategory( "Phoenix", Priority.DEBUG ),
                                            new LogTarget[] { logTarget } );
            this.logger.info( "Loader started" );
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Logger", e );
        }
    }
    private void setupLogger() {}

    private void createDeployer() throws ConfigurationException
    {
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            this.deployer = (Deployer)Class.forName( (String)this.context.get( "deployer-class" ) ).newInstance();
            this.deployerContext = new DefaultContext();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Deployer of class " +
                                              (String)this.context.get( "deployer-class" ), e );
        }
    }
    private void setupDeployer() throws Exception
    {
        if( this.deployer instanceof Loggable )
        {
            ((Loggable)this.deployer).setLogger( this.logger );
        }
        if( this.deployer instanceof Contextualizable )
        {
            ((Contextualizable)this.deployer).contextualize( (Context)this.deployerContext );
        }
        if( this.deployer instanceof Composer )
        {
            final DefaultComponentManager componentManager = new DefaultComponentManager();
            componentManager.put( "org.apache.avalon.camelot.Container", (Container)this.kernel );
            ((Composer)this.deployer).compose( componentManager );
        }
        final File directory = new File( (String)this.context.get( "default-apps-location" ) );
        CamelotUtil.deployFromDirectory( deployer, directory, ".sar" );

        // TODO: load facilities from .fars
        // final File directory2 = new File( (String)this.context.get( "default-facilities-location" ) );
        // CamelotUtil.deployFromDirectory( deployer, directory2, ".far" );
    }
    private void createManager() throws ConfigurationException
    {
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            this.mBeanServer = (MBeanServer)Class.forName( (String)this.context.get( "mBeanServer-class" ) ).newInstance();
            this.managerContext = new DefaultContext();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create MBean Server of class " +
                                              (String)this.context.get( "mBeanServer-class" ), e );
        }
    }
    private void setupManager()
    {
        this.manager = new Manager( this.mBeanServer );
        if( this.manager instanceof Loggable )
        {
            ((Loggable)this.manager).setLogger( this.logger );
        }
        if( this.manager instanceof Contextualizable )
        {
            this.managerContext.put( "org.apache.framework.container.Deployer", this.deployer );
            /* add references to default facilities so they will be loaded.
               TODO: put the facilities in .sars, make the deployer load
               those and remove this.  */
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.ComponentBuilder",
                "org.apache.phoenix.engine.facilities.DefaultComponentBuilder" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.ComponentManager",
                "org.apache.phoenix.engine.facilities.DefaultComponentManager" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.ConfigurationRepository",
                "org.apache.phoenix.engine.facilities.DefaultConfigurationRepository" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.ContextBuilder",
                "org.apache.phoenix.engine.facilities.DefaultContextBuilder" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.LoggerBuilder",
                "org.apache.phoenix.engine.facilities.DefaultLoggerBuilder" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.LogManager",
                "org.apache.phoenix.engine.facilities.DefaultLogManager" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.DefaultPolicy",
                "org.apache.phoenix.engine.facilities.DefaultPolicy" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.ThreadManager",
                "org.apache.phoenix.engine.facilities.DefaultThreadManager" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.SarBlockFactory",
                "org.apache.phoenix.engine.facilities.SarBlockFactory" );
            this.managerContext.put( "org.apache.avalon.atlantis.Facility.SarClassLoader",
                "org.apache.phoenix.engine.facilities.SarClassLoader" );
            ((Contextualizable)this.manager).contextualize( (Context)this.managerContext );
        }

        /* TODO
        add DynamicMBeans for the default kernel services here
        */
    }
    private void createKernel() throws ConfigurationException
    {
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            this.kernel = (Kernel)Class.forName( (String)this.context.get( "kernel-class" ) ).newInstance();
            this.kernelContext = new DefaultContext();
        } catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Kernel of class " +
                                              (String)this.context.get( "kernel-class" ), e );
        }
    }
    private void setupKernel()
    {
        if( this.kernel instanceof Loggable )
        {
            ((Loggable)this.kernel).setLogger( this.logger );
        }
        if( this.kernel instanceof Contextualizable )
        {
            this.kernelContext.put( "org.apache.phoenix.facilities.Manager", this.manager );
            ((Contextualizable)this.kernel).contextualize( (Context)this.kernelContext );
        }
        if( this.kernel instanceof Configurable )
        {
            try {
                this.kernelConfiguration = (new DefaultConfigurationBuilder()).build(
                   (String)this.context.get( "kernel-configuration-source" ) );
                ((Configurable)this.kernel).configure( this.kernelConfiguration );
            }
            catch ( Exception se )
            {
                // it's okay; we don't use this yet anyway...
            }
        }
        if( this.kernel instanceof Suspendable )
        {
            this.kernelSupportsSuspend = true;
        }

        try {
            this.kernel.init();
        } catch ( Exception e )
        {
            this.logger.log( Priority.FATAL_ERROR,
                "There was a fatal error; phoenix could not be started", e );
            System.exit( 1 );
        }
    }

    private void handleRestart()
    {
        this.restart = false;

        kernel.stop();
        kernel.dispose();
        manager.stop();
        manager.dispose();

        logger = null;
        kernel = null;
        manager = null;
        mBeanServer = null;
        deployer = null;
        System.gc(); // make sure resources are released

        try
        {
            this.init();
            this.setupLogger();
            this.setupDeployer();
            this.setupManager();
            this.setupKernel();
        }
        catch( Exception e )
        {
            // this is bad...
            logger.fatalError( "A fatal error occured while restarting. Phoenix will exit.", e );
            System.exit( 1 );
        }
        kernel.start();
    }
    private void handleSuspend()
    {
        //this.suspend = false;

        if( this.kernelSupportsSuspend )
        {
            ((Suspendable)kernel).suspend();
        } else
        {
            // the kernel does not support suspend,
            // thus, we must destroy and then
            // re-create it.
            kernel.stop();
            kernel.dispose();
            kernel = null;
            System.gc(); // make sure resources are released
        }

        // wait for resume
        try { synchronized( this ) { wait(); } }
        catch (InterruptedException e) {}
    }
    private void handleDispose()
    {
        kernel.stop();
        kernel.dispose();
        manager.stop();
        manager.dispose();

        kernel = null;
        manager = null;
        mBeanServer = null;
        deployer = null;
        System.gc(); // make sure resources are released
    }

}
