/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import org.apache.avalon.parameters.Parameters;
import org.apache.excalibur.cli.CLArgsParser;
import org.apache.excalibur.cli.CLOption;
import org.apache.excalibur.cli.CLOptionDescriptor;
import org.apache.excalibur.cli.CLUtil;
import org.apache.log.LogKit;
import org.apache.log.Priority;

/**
 * The class to load the kernel and start it running.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Main
{
    private static final String    PHOENIX_HOME         =
        System.getProperty( "phoenix.home", ".." );

    private static final String    DEFAULT_LOG_FILE     = PHOENIX_HOME + "/logs/phoenix.log";
    private static final String    DEFAULT_APPS_PATH    = PHOENIX_HOME + "/apps";

    private static final String    DEFAULT_KERNEL_CLASS =
        System.getProperty( "phoenix.kernel", "org.apache.phoenix.engine.PhoenixKernel" );

    private static final int       DEBUG_LOG_OPT        = 'd';
    private static final int       HELP_OPT             = 'h';
    private static final int       LOG_FILE_OPT         = 'l';
    private static final int       APPS_PATH_OPT        = 'a';

    protected String               m_appsPath           = DEFAULT_APPS_PATH;
    protected String               m_logFile            = DEFAULT_LOG_FILE;

    protected CLOptionDescriptor[] m_options;

    /**
     * Main entry point.
     *
     * @param args[] the command line arguments
     */
    public void main( final String args[] )
    {
        final Main main = new Main();

        try { main.execute( args ); }
        catch( final Throwable throwable )
        {
            System.out.println( "There was an uncaught exception:" );
            System.out.println( "---------------------------------------------------------" );
            throwable.printStackTrace( System.out );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( "The log file may contain further details of error." );
            System.out.println( "Please check the configuration files and restart phoenix." );
            System.out.println( "If the problem persists, contact the Avalon project.  See" );
            System.out.println( "http://jakarta.apache.org/avalon for more information." );
            System.exit( 1 );
        }

        System.exit( 0 );
    }

    /**
     * Display usage report.
     *
     */
    protected void usage()
    {
        System.out.println( "java " + getClass().getName() + " [options]" );
        System.out.println( "\tAvailable options:");
        System.out.println( CLUtil.describeOptions( m_options ) );
    }

    /**
     * Initialise the options for command line parser.
     *
     */
    protected CLOptionDescriptor[] createCLOptions()
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

    /**
     * Setup properties, classloader, policy, logger etc.
     *
     * @param clOptions the command line options
     * @exception Exception if an error occurs
     */
    protected void execute( final String[] args )
        throws Exception
    {
        m_options = createCLOptions();
        final CLArgsParser parser = new CLArgsParser( args, m_options );

        if( null != parser.getErrorString() )
        {
            System.err.println( "Error: " + parser.getErrorString() );
            return;
        }

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        boolean debugLog = false;

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

        try
        {
            final PrivilegedExceptionAction action = new PrivilegedExceptionAction()
                {
                    public Object run() throws Exception
                    {
                        execute();
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

    /**
     * Actually create and execute the main component of kernel.
     *
     * @exception Exception if an error occurs
     */
    protected void execute()
        throws Exception
    {
        final Parameters parameters = new Parameters();
        parameters.setParameter( "kernel-class", "org.apache.phoenix.engine.PhoenixKernel" );
        parameters.setParameter( "deployer-class", "org.apache.phoenix.engine.DefaultSarDeployer" );
        parameters.setParameter( "kernel-configuration-source", null );
        parameters.setParameter( "log-destination", m_logFile );
        parameters.setParameter( "applications-directory", m_appsPath );

        final PhoenixEmbeddor embeddor = new PhoenixEmbeddor();
        embeddor.parametize( parameters );
        embeddor.init();

        try
        {
            embeddor.execute();
        }
        finally
        {
            embeddor.dispose();
        }
    }
}
