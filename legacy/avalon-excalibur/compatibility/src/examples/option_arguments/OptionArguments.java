/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;

/**
 * This simple example shows how to have options, requiring
 * an argument, optionally supporting an argument or requiring
 * 2 arguments.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/cli/
 */
public class OptionArguments
{
    // Define our short one-letter option identifiers.
    private static final int FILE_OPT = 'f';
    private static final int DEFINE_OPT = 'D';
    private static final int SECURE_OPT = 'S';

    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
        //File requires an argument
        new CLOptionDescriptor( "file",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                FILE_OPT,
                "specify a file" ),

        //secure can take an argument if supplied
        new CLOptionDescriptor( "secure",
                CLOptionDescriptor.ARGUMENT_OPTIONAL,
                SECURE_OPT,
                "set security mode" ),

        //define requires 2 arguments
        new CLOptionDescriptor( "define",
                CLOptionDescriptor.ARGUMENTS_REQUIRED_2,
                DEFINE_OPT,
                "Require 2 arguments" )
    };

    public static void main( final String[] args )
    {
        System.out.println( "Starting OptionArguments..." );
        System.out.println( CLUtil.describeOptions( OPTIONS ) );
        System.out.println();

        // Parse the arguments
        final CLArgsParser parser = new CLArgsParser( args, OPTIONS );

        //Make sure that there was no errors parsing
        //arguments
        if( null != parser.getErrorString() )
        {
            System.err.println( "Error: " + parser.getErrorString() );
            return;
        }

        // Get a list of parsed options
        final List options = parser.getArguments();
        final int size = options.size();

        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)options.get( i );

            switch( option.getDescriptor().getId() )
            {
                case CLOption.TEXT_ARGUMENT:
                    //This occurs when a user supplies an argument that
                    //is not an option
                    System.out.println( "Unknown arg: " + option.getArgument() );
                    break;

                case FILE_OPT:
                    System.out.println( "File: " + option.getArgument() );
                    break;

                case SECURE_OPT:
                    if( null == option.getArgument() )
                    {
                        System.out.println( "Secure Mode with no args" );
                    }
                    else
                    {
                        System.out.println( "Secure Mode with arg: " + option.getArgument() );
                    }
                    break;

                case DEFINE_OPT:
                    System.out.println( "Defining: " +
                            option.getArgument( 0 ) + "=" +
                            option.getArgument( 1 ) );
                    break;
            }
        }
    }
}
