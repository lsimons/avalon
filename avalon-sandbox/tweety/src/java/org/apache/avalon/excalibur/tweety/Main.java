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


package org.apache.avalon.excalibur.tweety;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.avalon.framework.activity.*;
import org.apache.avalon.framework.component.*;
import org.apache.avalon.framework.configuration.*;
import org.apache.avalon.framework.context.*;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.framework.parameters.*;
import org.apache.avalon.framework.service.*;
import org.apache.avalon.framework.container.*;

/**
 * This is the tweety 'bootstrapper'. It is used to run Tweety from the
 * commandline. The single main() method calls all of Tweety's lifecycle
 * methods.
 *
 *
 *@author   <a href="mailto:nicolaken@krysalis.org">Nicola Ken Barozzi</a>
 *@author   <a href="mailto:leosimons@apache.org">Leo Simons</a>
 *@created  July 4, 2002
 *@version  1.0
 */
public class Main {

    /**
	 * We've marked the constructor as protected to prevent instantiation.
	 * Main is not a real 'object' or 'component', but rather a container
	 * for some procedural code (inside the main() method). There is no
	 * need to create an instance of it.
	 */
	protected Main() {
    }

	//// COMMANDLINE ENTRY POINT ////
	/**
	 * This method is called to invoke Tweety from the command line; It instantiates
	 * a new <code>Tweety</code> instance, provides the parameters to configure
	 * tweety by loading them from a file, and then runs tweety itself through its
	 * lifecycle.
	 *
	 * <p><b>Note:</b> we should improve this method to create a sensible context and
	 * configuration for hosted components.</p>
	 *
	 * @param args the command line arguments. We don't use them.
	 */
	public static void main( String[] args )
	{
		// create logger
		ConsoleLogger logger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );

		try
		{
			/** @todo: this is stupid. Figure out what to do about contexts */
			// create dummy context
			Context context = new DefaultContext();

			/** @todo: this is stupid. Figure out what to do about configurations */
			// create dummy configuration
			Configuration config = new DefaultConfiguration( "empty config", "nowhere" );

			// load properties
			Properties properties = new Properties();
			properties.load(new FileInputStream("tweety.properties"));

			// create parameters from properties
			Parameters params = Parameters.fromProperties( properties );

			// debug: show the stuff we feed into tweety

			// create tweety instance
			Tweety tweety = new Tweety();

			logger.debug("tweety.Main: Providing tweety with a console logger.");
			tweety.enableLogging( logger );

			logger.debug("tweety.Main: Providing tweety with an empty context.");
			tweety.contextualize( context );

			logger.debug("tweety.Main: Providing tweety with an empty configuration");
			tweety.configure( config );

			String[] paramNames = params.getNames();
			logger.debug("tweety.Main: Configuring tweety with the following parameters:");
			for( int i = 0; i < paramNames.length; i++ )
			{
					logger.debug("   parameter: " + paramNames[i] +
						    " = " + params.getParameter(paramNames[i]) );
			}
			tweety.parameterize( params );

			logger.debug("tweety.Main: Initializing tweety.");
			tweety.initialize();

			logger.debug("tweety.Main: Starting tweety");
			tweety.start();

			//
			// Here hosted components that create threads continue operation as
			// long as they wish.
			//

			tweety.stop();
		}
		catch( java.io.IOException ioe )
		{
			logger.error( "tweety.Main: Error reading configuration file.\nProgram Terminated", ioe );
			System.exit(-4);
		}
		catch( Exception e )
		{
			logger.error( "tweety.Main: Error starting up.\nProgram Terminated", e );
			System.exit(-2);
		}
	}

}
