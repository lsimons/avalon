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
import org.apache.framework.component.Composer;
import org.apache.framework.logger.Loggable;
import org.apache.framework.lifecycle.Suspendable;
import org.apache.framework.lifecycle.Resumable;

import org.apache.framework.context.Context;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.ConfigurationException;
import org.apache.framework.context.DefaultContext;
import org.apache.framework.component.DefaultComponentManager;
import org.apache.framework.configuration.DefaultConfigurationBuilder;
import org.apache.framework.CascadingException;

import org.apache.avalon.camelot.Container;
import org.apache.avalon.camelot.CamelotUtil;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.Deployer;

import org.apache.phoenix.facilities.Manager;
import org.apache.phoenix.core.Kernel;
import org.apache.phoenix.core.Embeddor;
import org.apache.phoenix.engine.facilities.ManagerImpl;

import org.apache.log.format.AvalonLogFormatter;
import org.apache.log.output.FileOutputLogTarget;
import org.apache.log.Logger;
import org.apache.log.LogKit;
import org.apache.log.Priority;
import org.apache.log.LogTarget;

/**
 *
 * TODO:
 * - improve exception handling, implement Reconfigurable and Recontextualizable.
 * - figure out if run() can be better than it is now; I'm not entirely at home
 * with threading stuff.
 * - fix imports.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public class PhoenixEmbeddor
    implements Embeddor, Suspendable, Resumable
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

    public PhoenixEmbeddor()
    {
    }
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
    public void configure( Configuration configuration )
        throws ConfigurationException
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
        catch( Exception e ) { logger.fatalError( "Unable to create deployer!", e );
                               throw new CascadingException( "Unable to create deployer!", e ); }
        try { this.createManager(); }
        catch( Exception e ) { logger.fatalError( "Unable to create manager!", e );
                               throw new CascadingException( "Unable to create manager!", e ); }
        try { this.createKernel(); }
        catch( Exception e ) { logger.fatalError( "Unable to create kernel!", e );
                               throw new CascadingException( "Unable to create kernel!", e ); }
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
        // setup core handler components
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

            // loop until <code>Shutdown</code> is created.
            while( !this.shutdown )
            {
                // loop
                while( !this.restart && !this.suspend )
                {
                    // the run() method in the kernel should
                    // call wait(), or this doesn't work very
                    // well...
                    kernel.run();
                    // wait() for shutdown(), restart() or
                    // suspend() to take action...
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
            // we can stop everything now...
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
        synchronized( this ) { notifyAll(); }
    }
    /**
     * Shut down the Embeddor together with the
     * Logger, Deployer, Manager and Kernel.
     */
    public void dispose()
    {
        this.shutdown = true;
        synchronized( this ) { notifyAll(); }
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
        synchronized( this ) { notifyAll(); }
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
                manager.resume();
                ((Resumable)kernel).resume();

                this.suspend = false;
                synchronized( this ) { notifyAll(); }
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
    /**
     * Uses <code>org.apache.log.LogKit</code> to create a new
     * logger using "Phoenix" as its category, DEBUG as its
     * priority and the log-destination from Context as its
     * destination.
     * TODO: allow configurable priorities and multiple
     * logtargets.
     */
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
    /**
     * Currently does nothing but is provided and called anyway for
     * consistency with the creation/setup of other handlers.
     */
    private void setupLogger() {}

    /**
     * Creates a new deployer from the Context's deployer-class.
     * TODO: fill the Context for Deployer properly.
     */
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
    /**
     * Creates a new Manager. The mBeanServer it uses is determined from
     * the Context's mBeanServer-class variable.
     * TODO: allow plugging of Managers.
     */
    private void createManager() throws ConfigurationException
    {
        final String className = this.parameters.getParameter( "mBeanServer-class", null );
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            this.mBeanServer = (MBeanServer)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create MBean Server of class " +
                                              className, e );
        }
    }
    /**
     * Sets up the Manager.
     * TODO: create MBeans for the facilities and applications
     * Deployer just loaded. Have Deployer put those in
     * managerContext.
     */
    private void setupManager()
    {
        this.manager = new ManagerImpl();
        setupLogger( this.manager );
        this.managerContext = new DefaultContext();
        this.managerContext.put("javax.management.MBeanServer", this.mBeanServer );
        this.managerContext.put("org.apache.framework.atlantis.core.Embeddor", this );
        this.managerContext.put("org.apache.framework.atlantis.core.Kernel", this.kernel );
        this.managerContext.put("org.apache.avalon.camelot.Deployer", this.deployer );
        this.manager.contextualize( this.managerContext );
    }
    /**
     * Creates the Kernel. The class used is the kernel-class
     * specified in the Context.
     */
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
    /**
     * Sets up the Kernel. We determine whether it supports Loggable,
     * Contextualizable and Configurable and supply information based
     * on that.
     * TODO: add checking for Recontextualizable and Reconfigurable.
     */
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
        if( ( this.kernel instanceof Suspendable ) && ( this.kernel instanceof Resumable ) )
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

    /**
     * Shuts down all components, dereferences them, does garbage-collect
     * and then re-initializes and runs the core components.
     */
    private void handleRestart() throws Exception
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
            // re-initialise
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
    /**
     * Suspends the manager, and if possible, the Kernel.
     * If the Kernel is not Suspendable, it is disposed
     * of and recreated when resume() is called.
     */
    private void handleSuspend() throws Exception
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
    /**
     * Stop()s and disposes() the Kernel and Manager, dereferences
     * the other components and calls garbage-collect.
     */
    private void handleDispose() throws Exception
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
