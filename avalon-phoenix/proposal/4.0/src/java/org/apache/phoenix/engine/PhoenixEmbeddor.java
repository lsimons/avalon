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

import org.apache.framework.logger.Loggable;

import org.apache.framework.parameters.Parameters;
import org.apache.framework.parameters.ParameterException;

import org.apache.framework.context.Context;
import org.apache.framework.context.DefaultContext;
import org.apache.framework.context.ContextualizationException;

import org.apache.framework.component.DefaultComponentManager;
import org.apache.framework.component.Composer;
import org.apache.framework.component.ComponentException;

import org.apache.framework.configuration.Configurable;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.ConfigurationException;
import org.apache.framework.configuration.DefaultConfigurationBuilder;
import org.apache.framework.lifecycle.StartException;
import org.apache.framework.lifecycle.StopException;
import org.apache.framework.lifecycle.InitializationException;

import org.apache.framework.CascadingException;

import org.apache.avalon.camelot.Container;
import org.apache.avalon.camelot.CamelotUtil;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.Deployer;

import org.apache.avalon.atlantis.facilities.Manager;
import org.apache.avalon.atlantis.core.Kernel;
import org.apache.avalon.atlantis.core.Embeddor;
import org.apache.phoenix.engine.facilities.ManagerImpl;

import org.apache.log.format.AvalonLogFormatter;
import org.apache.log.output.FileOutputLogTarget;
import org.apache.log.Logger;
import org.apache.log.LogKit;
import org.apache.log.Priority;
import org.apache.log.LogTarget;
import org.apache.framework.logger.AbstractLoggable;

/**
 *
 * TODO:
 * - improve exception handling, implement Reconfigurable and Recontextualizable.
 * - figure out if run() can be better than it is now; I'm not entirely at home
 * with threading stuff.
 * - fix imports.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public class PhoenixEmbeddor
    extends AbstractLoggable implements Embeddor
{
    private Parameters         parameters;

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
     * the parameters. Neccessary are:
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
    public void parametize( Parameters parameters )
        throws ParameterException
    {
        this.parameters = parameters;
    }
    /**
     * Creates the core handlers - logger, deployer, Manager and
     * Kernel. Note that these are not set up properly until you have
     * called the <code>run()</code> method.
     */
    public void init()
        throws InitializationException
    {
        this.createComponents();
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
    public void start() throws StartException
    {
        // setup core handler components
        this.setupComponents();
        // deploy facilities and applications
        this.runDeployer();

        kernel.start();
        // loop until <code>Shutdown</code> is created.
        while( this.shutdown )
        {
            // loop

            // wait() for shutdown() to take action...
            try { synchronized( this ) { wait(); } }
            catch( final InterruptedException e ) {}
        }
        // we can shut everything down now...
        handleShutdown();
    }

    /**
     * Shut down the Embeddor together with the
     * Logger, Deployer, Manager and Kernel.
     */
    public void stop()
    {
        this.shutdown = true;
        synchronized( this ) { notifyAll(); }
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    /**
     * Creates all required core components.
     */
    private void createComponents() throws InitializationException
    {
        try { this.createLogger(); }
        catch( Exception e ) {  logger.fatalError( "Unable to create logger!", e );
                                throw new InitializationException( "Unable to create logger!", e ); }
        try { this.createDeployer(); }
        catch( Exception e ) { logger.fatalError( "Unable to create deployer!", e );
                               throw new InitializationException( "Unable to create deployer!", e ); }
        try { this.createManager(); }
        catch( Exception e ) { logger.fatalError( "Unable to create manager!", e );
                               throw new InitializationException( "Unable to create manager!", e ); }
        try { this.createKernel(); }
        catch( Exception e ) { logger.fatalError( "Unable to create kernel!", e );
                               throw new InitializationException( "Unable to create kernel!", e ); }
    }
    /**
     * Sets up all the core components.
     */
    private void setupComponents() throws StartException
    {
        try { this.setupLogger(); }
        catch( Exception e ) {  logger.fatalError( "Unable to setup logger!", e );
                                throw new StartException( "Unable to setup logger!", e ); }
        try { this.setupDeployer(); }
        catch( Exception e ) {  logger.fatalError( "Unable to setup deployer!", e );
                                throw new StartException( "Unable to setup deployer!",e ); }
        try { this.setupManager(); }
        catch( Exception e ) {  logger.fatalError( "Unable to setup manager!", e );
                                throw new StartException( "Unable to setup manager!",e ); }
        try { this.setupKernel(); }
        catch( Exception e ) {  logger.fatalError( "Unable to setup kernel!", e );
                                throw new StartException( "Unable to setup kernel!",e ); }
    }
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
            final String logDest = this.parameters.getParameter( "log-destination", "" );
            final FileOutputLogTarget logTarget = new FileOutputLogTarget( logDest );
            final AvalonLogFormatter formatter = new AvalonLogFormatter();
            formatter.setFormat( "%{time} [%7.7{priority}] <<%{category}>> " +
                                 "(%{context}): %{message}\\n%{throwable}" );
            logTarget.setFormatter( formatter );

            LogKit.addLogTarget( logDest, logTarget );
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
        final String className = this.parameters.getParameter( "deployer-class", null );
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            this.deployer = (Deployer)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Deployer of class " +
                                              className, e );
        }
    }
    /**
     * Sets up the Deployer. If it is Loggable, it gets a reference
     * to the Embeddor's logger. If it is a Composer it is given a
     * ComponentManager which references the Kernel, cast to a
     * Container.
     */
    private void setupDeployer() throws Exception
    {
        setupLogger( this.deployer );

        if( this.deployer instanceof Composer )
        {
            final DefaultComponentManager componentManager = new DefaultComponentManager();
            componentManager.put( "org.apache.avalon.camelot.Container", (Container)this.kernel );
            ((Composer)this.deployer).compose( componentManager );
        }
    }
    /**
     * Runs the deployer. This expands and installs all the .sar and .far
     * files from their respective directories.
     * TODO: handle Facilities.
     */
    private void runDeployer() throws StartException
    {
        final String defaultAppsLocation =
            this.parameters.getParameter( "applications-directory", null );

        if( null != defaultAppsLocation )
        {
            final File directory = new File( defaultAppsLocation );
            try
            {
                CamelotUtil.deployFromDirectory( this.deployer, directory, ".sar" );
            }
            catch( Exception e )
            {
                throw new StartException( "Unable to deploy applications from "+defaultAppsLocation, e );
            }
        }

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
    private void setupManager() throws StartException
    {
        this.manager = new ManagerImpl();
        setupLogger( this.manager );
        this.managerContext = new DefaultContext();
        this.managerContext.put("javax.management.MBeanServer", this.mBeanServer );
        this.managerContext.put("org.apache.framework.atlantis.core.Embeddor", this );
        this.managerContext.put("org.apache.framework.atlantis.core.Kernel", this.kernel );
        this.managerContext.put("org.apache.avalon.camelot.Deployer", this.deployer );
        try
        {
            this.manager.contextualize( this.managerContext );
        }
        catch( Exception ce )
        {
            throw new StartException( "Unable to contextualize Manager.", ce );
        }
    }
    /**
     * Creates the Kernel. The class used is the kernel-class
     * specified in the Context.
     */
    private void createKernel() throws ConfigurationException
    {
        final String className = this.parameters.getParameter( "kernel-class", null );
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            this.kernel = (Kernel)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Kernel of class " +
                                              className, e );
        }
    }
    /**
     * Sets up the Kernel. We determine whether it supports Loggable,
     * Contextualizable and Configurable and supply information based
     * on that.
     * TODO: add checking for Recontextualizable and Reconfigurable.
     */
    private void setupKernel() throws StartException
    {
        setupLogger( this.kernel );

        if( this.kernel instanceof Configurable )
        {
            final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            final String kernelConfigLocation =
                this.parameters.getParameter( "kernel-configuration-source", null );
            try
            {
                final Configuration configuration = builder.build( kernelConfigLocation );
                ((Configurable)this.kernel).configure( configuration );
            }
            catch( Exception e ) { throw new StartException(
                        "Unable to configuration kernel from "+kernelConfigLocation, e ); }
        }

        try
        {
            this.kernel.init();
        }
        catch( final Exception e )
        {
            throw new StartException( "There was a fatal error; phoenix could not be started", e );
        }
    }

    /**
     * Stop()s and disposes() the Kernel and Manager, dereferences
     * the other components and calls garbage-collect.
     */
    private void handleShutdown()
    {
        try
        {
            kernel.stop();
            kernel.dispose();
            manager.stop();

            kernel = null;
            manager = null;
            mBeanServer = null;
            deployer = null;
        }
        catch( Exception e )
        {
            this.logger.error( "There was an error while attempting to shut down phoenix", e );
        }
        logger = null;
        System.gc(); // make sure resources are released
    }
}
