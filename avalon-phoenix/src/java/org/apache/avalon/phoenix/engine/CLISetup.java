/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.util.List;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;

/**
 * The class prepare parameters based on input options.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
class CLISetup
{
    private static final int       DEBUG_LOG_OPT        = 'd';
    private static final int       HELP_OPT             = 'h';
    private static final int       LOG_FILE_OPT         = 'l';
    private static final int       APPS_PATH_OPT        = 'a';

    ///Parameters created by parsing CLI options
    private Parameters    m_parameters   = new Parameters();

    ///Command used to execute program
    private String        m_command;

    public CLISetup( final String command )
    {
        m_command = command;
    }

    /**
     * Display usage report.
     */
    private void usage( final CLOptionDescriptor[] options )
    {
        System.err.println( m_command );
        System.err.println( "\tAvailable options:");
        System.err.println( CLUtil.describeOptions( options ) );
    }

    /**
     * Initialise the options for command line parser.
     */
    private CLOptionDescriptor[] createCLOptions()
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

    public Parameters getParameters()
    {
        return m_parameters;
    }

    public boolean parseCommandLineOptions( final String[] args )
    {
        final CLOptionDescriptor[] options = createCLOptions();
        final CLArgsParser parser = new CLArgsParser( args, options );

        if( null != parser.getErrorString() )
        {
            System.err.println( "Error: " + parser.getErrorString() );
            return false;
        }

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)clOptions.get( i );

            switch( option.getId() )
            {
            case 0:
                System.err.println( "Error: Unknown argument" + option.getArgument() );
                return false;

            case HELP_OPT:
                usage( options );
                return false;

            case DEBUG_LOG_OPT: 
                m_parameters.setParameter( "log-priority", "DEBUG" ); 
                break;

            case LOG_FILE_OPT: 
                m_parameters.setParameter( "log-destination",  option.getArgument() ); 
                break;

            case APPS_PATH_OPT: 
                m_parameters.setParameter( "applications-directory", option.getArgument() );
                break;
            }
        }

        return true;
    } 
}
