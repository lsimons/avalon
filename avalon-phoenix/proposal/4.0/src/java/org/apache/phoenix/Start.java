/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.management.MBeanServer;

import org.apache.framework.context.Context;
import org.apache.framework.context.DefaultContext;
import org.apache.framework.container.Entry;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.DefaultConfiguration;
import org.apache.framework.configuration.ConfigurationException;
import org.apache.framework.lifecycle.Disposable;

import org.apache.avalon.component.DefaultComponentManager;

import org.apache.phoenix.core.Embeddor;
import org.apache.phoenix.engine.PhoenixEmbeddor;

/**
 * Entry point to phoenix. Call to start the server.
// TODO: list available arguments
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public class Start implements Disposable
{
    // embeddor
    private static Embeddor embeddor;
        // embeddor configuration settings
        private static String embeddorClass;
        private static final String DEFAULT_EMBEDDOR_CLASS =
                                                    "org.apache.phoenix.engine.PhoenixEmbeddor";
        private static String deployerClass;
        private static final String DEFAULT_DEPLOYER_CLASS =
                                                    "org.apache.phoenix.engine.DefaultSarDeployer";
        private static String mBeanServerClass;
        private static final String DEFAULT_MBEANSERVER_CLASS =
                                                    "org.apache.jmx.MBeanServerImpl";
        private static String kernelClass;
        private static final String DEFAULT_KERNEL_CLASS =
                                                    "org.apache.phoenix.engine.DefaultKernel";
        private static String configurationSource;
        private static final String DEFAULT_CONFIGURATION_SOURCE = "../conf/server.xml";

    // monitor these for status changes
    private static boolean singleton = false;
    private static boolean shutdown = false;
    private static boolean restart = false;

    // command line options
    private static final int       DEBUG_LOG_OPT        = 'd';
    private static final int       HELP_OPT             = 'h';
    private static final int       LOG_FILE_OPT         = 'l';
    private static final int       APPS_PATH_OPT        = 'a';


    /**
     * Entry-point into Phoenix. See the class description for a list of the possible
     * arguments.
     */
    public void main( final String[] args )
    {
        if( singleton )
        {
            System.out.println( "Sorry, an instance of phoenix is already running. Phoenix cannot be run multiple times in the same VM." );
            System.exit( 1 );
        }
        singleton = true;

        final Start start = new Start();

        try
        {
            start.execute( args );
        }
        catch( final Throwable throwable )
        {
            System.out.println( "There was an uncaught exception:" );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( throwable.toString() );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( "Please check the configuration files and restart phoenix." );
            System.out.println( "If the problem persists, contact the Avalon project.  See" );
            System.out.println( "http://jakarta.apache.org/avalon for more information." );
            System.exit( 1 );
        }
    }

    /////////////////////////
    /// LIFECYCLE METHODS ///
    /////////////////////////
    /**
     * Shuts down and immediately restarts the server using the
     * same settings.
     */
    public void restart()
    {
        this.restart = true;
    }
    /**
     * Shuts down the server.
     */
    public void dispose()
    {
        this.shutdown = true;
    }

    /////////////////////////
    /// EXECUTION METHODS ///
    /////////////////////////
    private void execute( final String[] args ) throws Exception
    {
        try
        {
            final PrivilegedExceptionAction action = new PrivilegedExceptionAction()
            {
                public Object run() throws Exception
                {
                    exec( args );
                    return null;
                }
            };

            AccessController.doPrivileged( action );
        }
        catch( final PrivilegedActionException pae )
        {
            // only "checked" exceptions will be "wrapped" in a PrivilegedActionException.
            throw pae.getException();
        }
    }
    private void exec( final String[] args ) throws Exception
    {
        this.parseCommandLineOptions( args );
        this.createEmbeddor();

        final DefaultContext ctx = new DefaultContext();
            ctx.put( "kernel-class", this.kernelClass );
            ctx.put( "deployer-class", this.deployerClass );
            ctx.put( "mBeanServer-class", this.mBeanServerClass );
            ctx.put( "kernel-configuration-source", this.configurationSource );
            ctx.put( "log-destination", "../log/phoenix.log" );
        final Configuration conf = new DefaultConfiguration("phoenix","localhost");

        // run Embeddor lifecycle
        this.embeddor.contextualize(ctx);
        this.embeddor.configure(conf);
        this.embeddor.init();

        while( !this.shutdown )
        {
            while( !this.restart )
            {
                this.embeddor.run();
            }
            this.embeddor.restart();
            this.restart = false;
        }
        embeddor.dispose();
        System.exit( 0 );
    }
    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    private void parseCommandLineOptions( final String[] args )
    {
        // start with the defaults
        this.mBeanServerClass = this.DEFAULT_MBEANSERVER_CLASS;
        this.kernelClass = this.DEFAULT_KERNEL_CLASS;
        this.embeddorClass = this.DEFAULT_EMBEDDOR_CLASS;
        this.deployerClass = this.DEFAULT_DEPLOYER_CLASS;
        this.configurationSource = this.DEFAULT_CONFIGURATION_SOURCE;

        // TODO: update code below to set the code above

        // setup parser and get arguments
        final CLOptionDescriptor[] clOptionsDescriptor = createCommandLineOptions();
        final CLArgsParser parser = new CLArgsParser( args, clOptionsDescriptor );
        if( null != parser.getErrorString() )
        {
            System.err.println( "Error: " + parser.getErrorString() );
            return;
        }
        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        boolean debugLog = false;

        // parse options
        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)clOptions.get( i );

            switch( option.getId() )
            {
            case 0:
                System.err.println( "Error: Unknown argument" + option.getArgument() );
                //fall threw
            case HELP_OPT:
                usage();
                return;

            case DEBUG_LOG_OPT: debugLog = true; break;
            case LOG_FILE_OPT: m_logFile = option.getArgument(); break;
            case APPS_PATH_OPT: m_appsPath = option.getArgument(); break;
            }
        }

        if( !debugLog ) LogKit.setGlobalPriority( Priority.DEBUG );
    }
    protected CLOptionDescriptor[] createCommandLineOptions()
    {
        //TODO: localise
        final CLOptionDescriptor options[] = new CLOptionDescriptor[ 4 ];
        options[0] =
            new CLOptionDescriptor( "help",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    HELP_OPT,
                                    "display this help" );
        options[1] =
            new CLOptionDescriptor( "log-file",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    LOG_FILE_OPT,
                                    "the name of log file." );

        options[2] =
            new CLOptionDescriptor( "apps-path",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    APPS_PATH_OPT,
                                    "the path to apps/ directory that contains .sars" );

        options[3] =
            new CLOptionDescriptor( "debug-init",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    DEBUG_LOG_OPT,
                                    "use this option to specify enable debug " +
                                    "initialisation logs." );
        return options;
    }
    protected void usage(List commandLineOptions)
    {
        System.out.println( "java " + getClass().getName() + " [options]" );
        System.out.println( "\tAvailable options:");
        System.out.println( CLUtil.describeOptions( commandLineOptions ) );
    }
    private void createEmbeddor() throws ConfigurationException
    {
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            this.embeddor = (Embeddor)Class.forName( this.embeddorClass ).newInstance();
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create Embeddor of class " +
                                              this.embeddorClass, e );
        }
    }
}
