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
 * Demonstrates example of CLI containing incompatible arguments.
 * In this demo the quiet and verbose options are incompatible with
 * each other.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/cli/
 */
public class IncompatOptions
{
    // Define our short one-letter option identifiers.
    private static final int VERBOSE_OPT = 'v';
    private static final int QUIET_OPT = 'q';

    /**
     *  Define the understood options. Each CLOptionDescriptor contains:
     * - The "long" version of the option. Eg, "quiet" means that "--quiet" will
     * be recognised.
     * - The option flags, governing the option's argument(s).
     * - The "short" version of the option. Eg, 'q' means that "-q" will be
     * recognised.
     * - A description of the option.
     */
    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
        new CLOptionDescriptor( "verbose",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERBOSE_OPT,
                "Run command in verbose mode",
                new int[]{QUIET_OPT} ),
        new CLOptionDescriptor( "quiet",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                QUIET_OPT,
                "Run command in quiet mode",
                new int[]{VERBOSE_OPT} )
    };

    public static void main( final String[] args )
    {
        System.out.println( "Starting IncompatOptions..." );
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

                case VERBOSE_OPT:
                    System.out.println( "Verbose mode!" );
                    break;

                case QUIET_OPT:
                    System.out.println( "Quiet mode!" );
                    break;
            }
        }
    }
}
