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
import java.util.List;

import javax.management.MBeanServer;

import org.apache.framework.lifecycle.Disposable;
import org.apache.framework.parameters.Parameters;
import org.apache.framework.parameters.ParameterException;
import org.apache.framework.parameters.Parametizable;
import org.apache.framework.context.Context;
import org.apache.framework.context.DefaultContext;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.DefaultConfiguration;
import org.apache.framework.configuration.ConfigurationException;
import org.apache.framework.component.DefaultComponentManager;

import org.apache.avalon.camelot.Entry;
import org.apache.avalon.cli.CLOptionDescriptor;
import org.apache.avalon.cli.CLArgsParser;
import org.apache.avalon.cli.CLOption;
import org.apache.avalon.cli.CLUtil;

import org.apache.avalon.atlantis.core.Embeddor;
import org.apache.phoenix.engine.PhoenixEmbeddor;

import org.apache.log.LogKit;
import org.apache.log.Priority;

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
    // classloader
    private static ClassLoader cl;
    // embeddor
    private static Embeddor embeddor;
        // embeddor configuration settings
        private static String embeddorClass;
        private static final String DEFAULT_EMBEDDOR_CLASS =
                                                    "org.apache.phoenix.engine.PhoenixEmbeddor";
        private static String deployerClass;
        private static final String DEFAULT_DEPLOYER_CLASS =
                                                    "org.apache.phoenix.engine.deployer.DefaultSarDeployer";
        private static String mBeanServerClass;
        private static final String DEFAULT_MBEANSERVER_CLASS =
                                                    "org.apache.jmx.MBeanServerImpl";
        private static String kernelClass;
        private static final String DEFAULT_KERNEL_CLASS =
                                                    "org.apache.phoenix.engine.PhoenixKernel";
        private static String configurationSource;
        private static final String DEFAULT_CONFIGURATION_SOURCE =
                                                    "../conf/server.xml";
        private static String logDestination;
        private static final String DEFAULT_LOG_DESTINATION =
                                                    "../log/phoenix.log";
        private static String applicationSource;
        private static final String DEFAULT_APPLICATON_SOURCE =
                                                    "../apps";
        private static int registryPort;
        private static final int DEFAULT_REGISTRY_PORT =
                                                    1111;
        private static String computerName;
        private static final String DEFAULT_COMPUTER_NAME =
                                                    "localhost";
        private static String adaptorName;
        private static final String DEFAULT_ADAPTOR_NAME =
                                                    "phoenix.manager.JMXAdaptor";

    // monitor these for status changes
    private static boolean singleton = false;
    private static boolean shutdown = false;

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
            System.out.println( "" );
            System.out.println( "" );
            System.out.println( "There was an uncaught exception:" );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( throwable.toString() );
            //throwable.printStackTrace( System.out  );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( "Please check the configuration files and restart phoenix." );
            System.out.println( "If the problem persists, contact the Avalon project.  See" );
            System.out.println( "http://jakarta.apache.org/avalon for more information." );
            System.out.println( "" );
            System.out.println( "" );
            System.exit( 1 );
        }
    }

    /**
     * Sets the classloader to be used for loading all phoenix and application
     * classes.
     */
    public void setClassLoader( ClassLoader cl )
    {
        this.cl = cl;
    }

    /////////////////////////
    /// LIFECYCLE METHODS ///
    /////////////////////////
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
        System.out.println( " " );
        System.out.println( "-----------------" );
        System.out.print(   "Phoenix 3.2a4-dev" );
        System.out.println( " " );
        System.out.println( "-----------------" );
        System.out.print( "   Starting up" );

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

        System.out.print( "." );
        final Parameters parameters = new Parameters();
            parameters.setParameter( "kernel-class", this.kernelClass );
            parameters.setParameter( "deployer-class", this.deployerClass );
            parameters.setParameter( "mBeanServer-class", this.mBeanServerClass );
            parameters.setParameter( "kernel-configuration-source", this.configurationSource );
            parameters.setParameter( "log-destination", this.logDestination );
            parameters.setParameter( "application-source", this.applicationSource );
            parameters.setParameter( "registry-port", new String( ""+this.registryPort ) );
            parameters.setParameter( "computer-name", this.computerName );
            parameters.setParameter( "adaptor-name", this.adaptorName );

        // run Embeddor lifecycle
        this.embeddor.parametize( parameters );
        System.out.print( "." );
        this.embeddor.init();

        this.embeddor.setRunner( (Disposable)this );
        System.out.print( "." );

        System.out.print( "done." );
        System.out.println( "" );
        System.out.println( "" );

        this.embeddor.start();

        while( !this.shutdown )
        {
            // loop

            // wait() for shutdown() to take action...
            try { synchronized( this ) { wait(); } }
            catch( final InterruptedException e ) {}
        }
        System.out.print( "done." ); // shutting down, that is.
        this.embeddor = null;
        System.out.println( "" );
        System.out.println( "Bye-bye!" );
        System.out.println( "" );
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
        this.logDestination = this.DEFAULT_LOG_DESTINATION;
        this.applicationSource = this.DEFAULT_APPLICATON_SOURCE;
        this.registryPort = this.DEFAULT_REGISTRY_PORT;
        this.adaptorName = this.DEFAULT_ADAPTOR_NAME;

        // try to get the computer name from system property
        if( (this.computerName = System.getProperty( "computer.name" )) == null )
            this.computerName = this.DEFAULT_COMPUTER_NAME;


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
                this.usage( clOptionsDescriptor );
                return;

            case DEBUG_LOG_OPT: debugLog = true; break;
            case LOG_FILE_OPT: this.logDestination = option.getArgument(); break;
            case APPS_PATH_OPT: this.applicationSource = option.getArgument(); break;
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
    protected void usage(CLOptionDescriptor[] commandLineOptions)
    {
        System.out.println( "java " + getClass().getName() + " [options]" );
        System.out.println( "\tAvailable options:");
        System.out.println( CLUtil.describeOptions( commandLineOptions ) );
    }
    private void createEmbeddor() throws Exception
    {
        //Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
        //this.embeddor = (Embeddor)Class.forName( this.embeddorClass ).newInstance();
        //this.embeddor = new PhoenixEmbeddor();
        Thread.currentThread().setContextClassLoader( this.cl );
        final Class clazz = Thread.currentThread().getContextClassLoader().loadClass( this.embeddorClass );
        this.embeddor = (Embeddor)clazz.newInstance();
    }
}
