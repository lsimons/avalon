/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.excalibur.xfc;

import java.util.List;
import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;

/**
 * Command line based XFC entry point.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * (parts also taken from the Excalibur CLI example)
 * @version CVS $Id: Main.java,v 1.4 2002/11/12 19:55:27 donaldp Exp $
 */
public final class Main
{
    // Define our short one-letter option identifiers.
    private static final int HELP_OPT = 'h';
    private static final int VERSION_OPT = 'v';
    private static final int INPUT_OPT = 'i';
    private static final int OUTPUT_OPT = 'o';
    private static final int DEBUG_OPT = 'd';

    // Array of understood options, for setting the input and output
    // conversion modules
    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
        new CLOptionDescriptor( "help",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                HELP_OPT,
                                "print this message and exit" ),
        new CLOptionDescriptor( "version",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                VERSION_OPT,
                                "print this version and exit" ),
        new CLOptionDescriptor( "input",
                                CLOptionDescriptor.ARGUMENT_REQUIRED,
                                INPUT_OPT,
                                "set the input module name and context" ),
        new CLOptionDescriptor( "output",
                                CLOptionDescriptor.ARGUMENT_REQUIRED,
                                OUTPUT_OPT,
                                "set the output module name and context" ),
        new CLOptionDescriptor( "debug",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                DEBUG_OPT,
                                "enable debug logging" ),
    };

    // Logger for output.
    private static Logger m_logger = new NullLogger();

    // Input module name
    private static String m_inputModule;

    // Input module context
    private static String m_inputCtx;

    // Output module name
    private static String m_outputModule;

    // Output module context
    private static String m_outputCtx;

    /**
     * Main method, entry point to program
     *
     * @param args a <code>String[]</code> array of command line arguments
     * @exception Exception if an error occurs
     */
    public static void main( final String[] args )
        throws Exception
    {
        // parse command line args
        parseArgs( args );

        if( getLogger().isInfoEnabled() )
        {
            getLogger().info( "Input Module = " + m_inputModule );
            getLogger().info( "Input Module Context = " + m_inputCtx );
            getLogger().info( "Output Module = " + m_outputModule );
            getLogger().info( "Output Module Context = " + m_outputCtx );
        }

        // create converter
        final Converter cv = new Converter( getLogger() );

        // set up input and output modules
        cv.setInputModule( getClass( m_inputModule ) );
        cv.setInputModuleContext( m_inputCtx );

        cv.setOutputModule( getClass( m_outputModule ) );
        cv.setOutputModuleContext( m_outputCtx );

        // convert
        cv.convert();

        // all done, good show
    }

    /**
     * Helper method for parsing the command line arguments
     *
     * @param args a <code>String[]</code> value
     */
    private static void parseArgs( final String[] args )
    {
        // Parse the arguments
        final CLArgsParser parser = new CLArgsParser( args, OPTIONS );

        // Make sure that there was no errors parsing arguments
        if( null != parser.getErrorString() )
        {
            System.err.println( "Error: " + parser.getErrorString() );
            System.exit( 1 );
        }

        // Get a list of parsed options
        final List options = parser.getArguments();
        final int size = options.size();

        // Check that there are enough arguments (should be no more than 3)
        if( size > 3 )
        {
            printUsage();
        }

        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)options.get( i );

            switch( option.getId() )
            {
                case CLOption.TEXT_ARGUMENT:
                    // This occurs when a user supplies an unknown argument
                    System.err.println( "Unknown argument: " + option.getArgument() );
                    break;

                case DEBUG_OPT:
                    // Modify the logger to print debug output to console
                    m_logger = new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG );
                    break;

                case HELP_OPT:
                    // Print some help information
                    printUsage();
                    break;

                case VERSION_OPT:
                    // Print some version information
                    printVersion();

                case INPUT_OPT:
                    // Set the input module and context
                    m_inputModule = getModule( option.getArgument() );
                    m_inputCtx = getContext( option.getArgument() );
                    break;

                case OUTPUT_OPT:
                    // Set the output module and context
                    m_outputModule = getModule( option.getArgument() );
                    m_outputCtx = getContext( option.getArgument() );
                    break;
            }
        }

        // check that modules/contexts are set
        if( m_inputModule == null ||
            m_inputCtx == null ||
            m_outputModule == null ||
            m_outputCtx == null )
        {
            printUsage();
        }
    }

    /**
     * Helper method for obtaining the {@link Module} name
     * from the given {@link String} parameter
     *
     * @param str a <code>String</code> value
     * @return a <code>String</code> value
     */
    private static String getModule( final String str )
    {
        final int i = str.indexOf( ':' );
        return str.substring( 0, i );
    }

    /**
     * Helper method for obtaining the {@link Module} Context
     * value from the given {@link String} parameter
     *
     * @param str a <code>String</code> value
     * @return a <code>String</code> value
     */
    private static String getContext( final String str )
    {
        final int i = str.indexOf( ':' );
        return str.substring( i + 1 );
    }

    /**
     * Obtain the {@link Class} object for the plugin module
     * specified. This method makes a simple check with some pre-defined
     * plugin's and returns their {@link Class} objects if specified.
     *
     * <p>
     *  If the specified plugin is not known, it's assumed to be a
     *  fully qualified class name of a custom plugin, and is loaded manually.
     * </p>
     *
     * @param clazz class name as a <code>String</code> object
     * @return a <code>Class</code> instance
     * @exception ClassNotFoundException if an error occurs
     */
    private static Class getClass( final String clazz )
        throws ClassNotFoundException
    {
        if( "ecm".equalsIgnoreCase( clazz ) )
        {
            return Class.forName( "org.apache.excalibur.xfc.modules.ecm.ECM" );
        }

        if( "fortress".equalsIgnoreCase( clazz ) )
        {
            return Class.forName( "org.apache.excalibur.xfc.modules.fortress.Fortress" );
        }

        if( "merlin".equalsIgnoreCase( clazz ) )
        {
            return Class.forName( "org.apache.excalibur.xfc.modules.merlin.Merlin" );
        }

        // assume custom module
        return Class.forName( clazz );
    }

    /**
     * Print out a usage statement
     */
    private static void printUsage()
    {
        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuffer msg = new StringBuffer();

        msg.append( lineSeparator );
        msg.append( "XFC - The Avalon Excalibur (X)Conf (F)ile (C)onverter" );
        msg.append( lineSeparator );
        msg.append( "Usage: java " + Main.class.getName() + " [options]" );
        msg.append( lineSeparator );
        msg.append( lineSeparator );
        msg.append( "Options: " );
        msg.append( lineSeparator );

        //
        // Uses CLUtil.describeOptions to generate the
        // list of descriptions for each option
        //
        msg.append( CLUtil.describeOptions( OPTIONS ).toString() );

        // Usage examples
        msg.append( "\nExample: \n" );
        msg.append( "\tjava " + Main.class.getName() + "\\\n" );
        msg.append( "\t\t--input ecm:conf/ecm.roles:conf/ecm.xconf \\\n" );
        msg.append( "\t\t--output fortress:conf/fortress.roles:conf/fortress.xconf" );

        System.out.println( msg.toString() );

        System.exit( 0 );
    }

    /**
     * Simple method to print a version number
     */
    private static void printVersion()
    {
        System.out.println( "XFC Version 0.1" );
        System.exit( 0 );
    }

    /**
     * Helper method to return the <code>Logger</code> object
     *
     * @return a <code>Logger</code> value
     */
    private static Logger getLogger()
    {
        return m_logger;
    }
}
