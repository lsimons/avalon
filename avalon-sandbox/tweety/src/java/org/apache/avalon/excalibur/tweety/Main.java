/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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