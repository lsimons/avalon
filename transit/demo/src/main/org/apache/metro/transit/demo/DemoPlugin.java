/* 
 * Copyright 2004 Apache Software Foundation
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

package org.apache.metro.transit.demo;

import org.apache.metro.i18n.ResourceManager;
import org.apache.metro.i18n.Resources;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import org.apache.metro.logging.Logger;

/**
 * A demonstration plugin.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class DemoPlugin implements Runnable
{
    // ------------------------------------------------------------------------
    // static
    // ------------------------------------------------------------------------

    private static Resources REZ =
        ResourceManager.getPackageResources( DemoPlugin.class );

    public static Options OPTIONS = buildCommandLineOptions();

    private static Options buildCommandLineOptions()
    {
        Options options = new Options();
        options.addOption( 
          OptionBuilder.hasArg().withArgName( "text" )
           .withDescription( REZ.getString( "cli-message-description" ) )
           .create( "message" ) );
        return options;
    }

    // ------------------------------------------------------------------------
    // immutable state
    // ------------------------------------------------------------------------

    private final Logger m_logger;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------
    
    public DemoPlugin( Logger logger, String[] args ) throws ParseException
    {
        m_logger = logger;

        CommandLineParser parser = new GnuParser();
        CommandLine commands = parser.parse( OPTIONS, args, false );
        if( commands.hasOption( "message" ) )
        {
            String message = commands.getOptionValue( "message" );
            getLogger().info( message );
        }
        else
        {
            getLogger().info( REZ.getString( "message" )  );
        }
    }

    public void run()
    {
        boolean flag = true;
        while( flag )
        {
            try
            {
                Thread.currentThread().sleep( 100 );
            }
            catch( InterruptedException ie )
            {
                flag = false;
                getLogger().debug( "shutdown" );
            }
        }
    }

    // ------------------------------------------------------------------------
    // internals
    // ------------------------------------------------------------------------

    private Logger getLogger() 
    {
        return m_logger;
    }
}

