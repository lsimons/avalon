/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.frontends;

import java.util.List;
import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * The class prepare parameters based on input options.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
class CLISetup
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( CLISetup.class );

    private static final int DEBUG_LOG_OPT       = 'd';
    private static final int HELP_OPT            = 'h';
    private static final int LOG_FILE_OPT        = 'l';
    private static final int APPS_PATH_OPT       = 'a';
    private static final int REMOTE_MANAGER_OPT  = 1;

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
        System.err.println( "\t" + REZ.getString( "cli.desc.available.header" ) );
        System.err.println( CLUtil.describeOptions( options ) );
    }

    /**
     * Initialise the options for command line parser.
     */
    private CLOptionDescriptor[] createCLOptions()
    {
        final CLOptionDescriptor options[] = new CLOptionDescriptor[ 5 ];
        options[0] =
            new CLOptionDescriptor( "help",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    HELP_OPT,
                                    REZ.getString( "cli.opt.help.desc" ) );
        options[1] =
            new CLOptionDescriptor( "log-file",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    LOG_FILE_OPT,
                                    REZ.getString( "cli.opt.log-file.desc" ) );

        options[2] =
            new CLOptionDescriptor( "apps-path",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    APPS_PATH_OPT,
                                    REZ.getString( "cli.opt.apps-path.desc" ) );

        options[3] =
            new CLOptionDescriptor( "debug-init",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    DEBUG_LOG_OPT,
                                    REZ.getString( "cli.opt.debug-init.desc" ) );

        options[4] =
            new CLOptionDescriptor( "remote-manager",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    REMOTE_MANAGER_OPT,
                                    REZ.getString( "cli.opt.remote-manager.desc" ) );

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
            final String message = REZ.format( "cli.error.parser", parser.getErrorString() );
            System.err.println( message );
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
                {
                    final String message =
                        REZ.format( "cli.error.unknown.arg", option.getArgument() );
                    System.err.println( message );
                }
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

            case REMOTE_MANAGER_OPT:
                m_parameters.setParameter( "manager-class",
                                           "org.apache.avalon.phoenix.engine.PhoenixManager" );
                break;
            }
        }

        return true;
    }
}
