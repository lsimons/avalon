/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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

import java.util.List;

import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;

/**
 * Demonstrates Basic example of command line utilities.
 *
 * @author <a href="jeff@socialchange.net.au">Jeff Turner</a>
 * @author <a href="peter@apache.org">Peter Donald</a>
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
