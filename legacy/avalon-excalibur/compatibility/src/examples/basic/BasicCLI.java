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
 * Demonstrates Basic example of command line utilities.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/cli/
 */
public class BasicCLI
{
    // Define our short one-letter option identifiers.
    private static final int HELP_OPT = 'h';
    private static final int VERSION_OPT = 'v';

    /**
     *  Define the understood options. Each CLOptionDescriptor contains:
     * - The "long" version of the option. Eg, "help" means that "--help" will
     * be recognised.
     * - The option flags, governing the option's argument(s).
     * - The "short" version of the option. Eg, 'h' means that "-h" will be
     * recognised.
     * - A description of the option.
     */
    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
        new CLOptionDescriptor( "help",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELP_OPT,
                "print this message and exit" ),
        new CLOptionDescriptor( "version",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERSION_OPT,
                "print the version information and exit" )
    };

    public static void main( final String[] args )
    {
        System.out.println( "Starting BasicCLI..." );
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

                case HELP_OPT:
                    printUsage();
                    break;

                case VERSION_OPT:
                    printVersion();
                    break;
            }
        }
    }

    /**
     * Print out a dummy version
     */
    private static void printVersion()
    {
        System.out.println( "1.0" );
        System.exit( 0 );
    }

    /**
     * Print out a usage statement
     */
    private static void printUsage()
    {
        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuffer msg = new StringBuffer();

        msg.append( lineSeparator );
        msg.append( "Excalibur command-line arg parser demo" );
        msg.append( lineSeparator );
        msg.append( "Usage: java " + IncompatOptions.class.getName() + " [options]" );
        msg.append( lineSeparator );
        msg.append( lineSeparator );
        msg.append( "Options: " );
        msg.append( lineSeparator );

        /*
         * Notice that the next line uses CLUtil.describeOptions to generate the
         * list of descriptions for each option
         */
        msg.append( CLUtil.describeOptions( BasicCLI.OPTIONS ).toString() );

        System.out.println( msg.toString() );

        System.exit( 0 );
    }
}
