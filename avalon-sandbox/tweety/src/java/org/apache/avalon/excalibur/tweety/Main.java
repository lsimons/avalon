/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (c) 2002 The Apache Software Foundation. All rights reserved.

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
 * This is the tweety 'bootstrapper'. It is used to run
 * {@link org.apache.avalon.excalibur.tweety.Tweety Tweety} from the
 * commandline. The single {@link #main(String[] args) main()} method calls all of
 * Tweety's lifecycle methods.
 *
 *
 * @author   <a href="mailto:nicolaken@krysalis.org">Nicola Ken Barozzi</a>
 * @author   <a href="mailto:leosimons@apache.org">Leo Simons</a>
 * @created  July 4, 2002
 * @version  1.0.1
 * @since    1.0-alpha
 * @see      <a href="http://jakarta.apache.org/avalon/excalibur/tweety">Online Tweety documentation</a>
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
	 * @param args the command line arguments. There is one option: if you provide
	 * the option "-d" you will get extra debugging information.
	 */
	public static void main( String[] args )
	{
		// creating logger. We do some very simple commandline parsing here to figure
		// out whether we should enable debugging.
		ConsoleLogger logger = null;
		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equals("-d"))
			{
	    		logger = new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG );
				break;
			}
		}
		if(logger == null)
			logger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );

		try
		{
			// create dummy context
			DefaultContext context = new DefaultContext();

			/** @todo: this is stupid. It would be a good idea to use a configuration builder
			 *  as org.apache.avalon.framework.tools.infobuilder in the info/meta packages.
			 *  Until such a thing is standardised, keep things like they are. Don't want to
			 *  add a dependency on xerces or alpha code for this. */
			// create dummy configuration
			Configuration config = new DefaultConfiguration( "empty config", "nowhere" );

			// load properties
			Properties properties = new Properties();
			properties.load(new FileInputStream("tweety.properties"));

			// create parameters from properties
			Parameters params = Parameters.fromProperties( properties );

		    // add some stuff into the context. This loosely follows
			// the context spec part of the info revolution for lack
			// of any standard.
			// see: http://jakarta.apache.org/avalon/excalibur/info/context.html

			// directory for persistent data
			File storage = new File("./store");
			// directory for non-persistent data
			File work = new File("./work");

			// make sure those dirs exist
			if(!storage.exists())
				storage.mkdir();
			if(!storage.isDirectory())
				throw new Exception("No permanent storage directory available!");
			if(!work.exists())
				storage.mkdir();
			if(!work.isDirectory())
				throw new Exception("No working directory available!");

			context.put( "component.classloader", Thread.currentThread().getContextClassLoader() );
			context.put( "component.home", storage );
			context.put( "component.work", work );

			// create tweety instance
			Tweety tweety = new Tweety();

			// debug: show the stuff we feed into tweety

			logger.debug("tweety.Main: Providing tweety with a console logger.");
			tweety.enableLogging( logger );

			logger.debug("tweety.Main: Providing tweety with the context.");
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
		finally
		{
			/** @todo make sure the work directory is empty so that there aren't any file
			 *  conflicts during subsequent runs */
		}
	}
}
