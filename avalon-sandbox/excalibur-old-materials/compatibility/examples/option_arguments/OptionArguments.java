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
 * This simple example shows how to have options, requiring
 * an argument, optionally supporting an argument or requiring
 * 2 arguments.
 *
 * @author Peter Donald
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
