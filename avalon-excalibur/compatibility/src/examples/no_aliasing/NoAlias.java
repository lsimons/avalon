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
 * This simple example shows how to set it up so that only the
 * long form or only short form of an option is capable of
 * being used.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/cli/
 */
public class NoAlias
{
    // Define our short one-letter option identifiers.
    private static final int SHORT_OPT = 's';
    private static final int LONG_OPT = 1;

    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
        new CLOptionDescriptor( null,
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                SHORT_OPT,
                "option with only short form",
                new int[0] ),
        new CLOptionDescriptor( "long",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                LONG_OPT,
                "option with long form" )
    };

    public static void main( final String[] args )
    {
        System.out.println( "Starting NoAlias..." );
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

                case SHORT_OPT:
                    System.out.println( "Received short option" );
                    break;

                case LONG_OPT:
                    System.out.println( "Received long option" );
                    break;
            }
        }
    }
}
