/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

    private CLOptionDescriptor[] createCLOptions()
    {
        final CLOptionDescriptor options[] = new CLOptionDescriptor[ 9 ];
        options[ 0 ] =
            new CLOptionDescriptor( "help",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    HELP_OPT,
                                    REZ.getString( "cli.opt.help.desc" ) );
        options[ 1 ] =
            new CLOptionDescriptor( "log-file",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    LOG_FILE_OPT,
                                    REZ.getString( "cli.opt.log-file.desc" ) );

        options[ 2 ] =
            new CLOptionDescriptor( "apps-path",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    APPS_PATH_OPT,
                                    REZ.getString( "cli.opt.apps-path.desc" ) );

        options[ 3 ] =
            new CLOptionDescriptor( "debug-init",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    DEBUG_LOG_OPT,
                                    REZ.getString( "cli.opt.debug-init.desc" ) );

        options[ 4 ] =
            new CLOptionDescriptor( "remote-manager",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    REMOTE_MANAGER_OPT,
                                    REZ.getString( "cli.opt.remote-manager.desc" ) );

        options[ 5 ] =
            new CLOptionDescriptor( "disable-hook",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    DISABLE_HOOK_OPT,
                                    REZ.getString( "cli.opt.disable-hook.desc" ) );

        options[ 6 ] =
            new CLOptionDescriptor( "application",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    APPLICATION_OPT,
                                    REZ.getString( "cli.opt.application.desc" ) );

        options[ 7 ] =
            new CLOptionDescriptor( "persistent",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    PERSISTENT_OPT,
                                    REZ.getString( "cli.opt.persistent.desc" ) );

        options[ 8 ] =
            new CLOptionDescriptor( "configfile",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    CONFIGFILE_OPT,
                                    REZ.getString( "cli.opt.configfile.desc" ) );

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
            final String message = REZ.getString( "cli.error.parser", parser.getErrorString() );
            System.err.println( message );
            return false;
        }

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)clOptions.get( i );

            switch( option.getDescriptor().getId() )
            {
                case 0:
                    {
                        final String message =
                            REZ.getString( "cli.error.unknown.arg", option.getArgument() );
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
                    m_parameters.setParameter( "log-destination", option.getArgument() );
                    break;

                case APPS_PATH_OPT:
                    m_parameters.setParameter( "phoenix.apps.dir", option.getArgument() );
                    break;

                case REMOTE_MANAGER_OPT:
                    m_parameters.setParameter( SystemManager.ROLE, MANAGER_IMPL );
                    break;

                case APPLICATION_OPT:
                    m_parameters.setParameter( "application-location", option.getArgument() );
                    break;

                case PERSISTENT_OPT:
                    m_parameters.setParameter( "persistent", "true" );
                    break;

                case DISABLE_HOOK_OPT:
                    m_parameters.setParameter( "disable-hook", "true" );
                    break;

                case CONFIGFILE_OPT:
                    m_parameters.setParameter( "phoenix.configfile", option.getArgument() );
                    break;
            }
        }

        return true;
    }
}
