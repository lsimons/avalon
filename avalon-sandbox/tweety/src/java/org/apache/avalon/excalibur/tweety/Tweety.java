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
 * This is the tweety engine. It is an Avalon Container, which can be used
 * to run and manage other Avalon Components. Because it is an Avalon
 * Component itself as well, it can easily be used in any Avalon Application.
 *
 * <p>The only reason tweety implements {@link org.apache.avalon.framework.configuration.Configurable Configurable}
 * and {@link org.apache.avalon.framework.context.Contextualizable Contextualizable}
 * is that it doesn't feel much like managing
 * contexts or configurations for the components it manages. Instead, it
 * simply forwards the configuration and context provided to its components.</p>
 *
 * @author   <a href="mailto:nicolaken@krysalis.org">Nicola Ken Barozzi</a>
 * @author   <a href="mailto:leosimons@apache.org">Leo Simons</a>
 * @created  June 20, 2002
 * @version  1.2.1
 * @since    1.0-alpha
 * @see      <a href="http://jakarta.apache.org/avalon/excalibur/tweety">Online Tweety documentation</a>
 * @see      <a href="http://jakarta.apache.org/avalon/framework">Online Avalon Framework documentation</a>
 */
public class Tweety implements LogEnabled, Contextualizable, Configurable, Initializable,
		Parameterizable, Startable
{
	//// PROPERTIES ////
	// all our properties are protected. Since we don't provide get()/set()
	// methods for them, this is the only way to make them accessible when
	// subclassing.

	/** This logger is used by tweety and its managed components everywhere; it sends
	messages to the console. */
	protected Logger m_logger;

	/** all components share the same, global, Context; this is not what should
	really happen in an actual avalon container, but it does make Tweety a bit easier. */
	protected Context m_context;

	/** all components share the same, global, Configuration; this is not what should
	really happen in an actual avalon container, but it does make Tweety a bit easier. */
	protected Configuration m_configuration;

	/** all components share the same, global, ComponentManager;
	this would not happen in a real-life setup, but it makes the
	code simpler. */
	protected DefaultComponentManager m_componentManager;

	/** all components share the same, global, ServiceManager;
	this would not happen in a real-life setup, but it makes the
	code simpler. */
	protected DefaultServiceManager m_serviceManager;

	/**
	 * this field is filled during {@link #parameterize(Parameters parameters) parameterize()} with a parameters
	 * object that describes everything <code>Tweety</code> needs to run.
	 * Usually, it contains information loaded from a <code>tweety.properties</code> file. A
	 * sample <code>tweety.properties</code> file might look like this:
	 <pre>
########################################################################
# tweety.properties file
########################################################################
#
# Use this section to specify which components you want tweety to manage,
# and under what role they should be put in the ComponentManager.
# FORMAT: <role> = <javaclassname>

chirp-world = org.apache.avalon.excalibur.tweety.demos.ChirpWorld
chirp-mondo = org.apache.avalon.excalibur.tweety.demos.ChirpWorld
	 </pre>
	 * <b>Note:</b> this object is *not* shared with all the components tweety
	 * contains, like some of the other properties of this class.
	 */
	protected Parameters m_parameters;

	/**
	 * This map will be used to store a reference to all the components
	 * that tweety will manage; We fill it up during {@link #initialize() initialize()}.
	 */
	protected Map m_components;

	//// CONSTRUCTOR ////
	/**
	 * Tweety has a single, public, no arguments constructor; We supply arguments
	 * at a later point in the form of an instance of <code>Parameters</code>
	 * during {@link #parameterize(Parameters parameters) parameterize()}.
	 *
	 * <p>The constructor initializes our properties with default values.</p>
	 */
	public Tweety()
	{
	}

	//// AVALON FRAMEWORK LIFECYCLE METHODS ////
	/**
	 * Provide tweety with a logger; This logger will also be used for all the
	 * components that tweety manages.
	 *
	 * @param logger the logger
	 */
	public void enableLogging( Logger logger )
	{
		m_logger = logger;
		logger.debug( "Tweety: Tweety got a logger" );
	}

	/**
	 * Provide tweety with a context; This context will also be used for all the
	 * components that tweety manages.
	 *
	 * @param context the context
	 */
	public void contextualize( Context context )
	{
		m_context = context;
		m_logger.debug( "Tweety: Tweety got a context" );
	}

	/**
	 * Provide tweety with a configuration; This configuration will also be used for all the
	 * components that tweety manages.
	 *
	 * @param configuration the class configurations.
	 */
	public void configure( Configuration configuration )
	{
		m_configuration = configuration;
		m_logger.debug( "Tweety: Tweety got a configuration" );
	}

	/**
	 * Provide tweety with the parameters it needs to run; Each pair of name and
	 * value defines a <code>Role</code> and a component that implements that role.
	 *
	 * @param parameters the parameters
	 * @throws ParameterException if parameters are invalid
	 */
	public void parameterize( Parameters parameters ) throws ParameterException
	{
		// check whether the supplied parameters object is valid
		if( null == parameters || parameters.getNames().length <= 0 )
		{
			/* this is bad! Since there's no point in
			running tweety without components, and there
			is no way to specify components other than
			during parameterize(), we throw an exception. */
			throw new ParameterException(
				    "We need some components to take through their lifecycle! Tjilp!" );

		}

		// check whether all the specified classes can be found
		String[] roles = parameters.getNames();
		String role = "";
		String classname = "";

		try
		{
			for( int i = 0; i < roles.length; i++ )
			{
				role = roles[i];
	    		classname = parameters.getParameter( role );

				// load class
				Class.forName( classname );
			}
		}
		catch( ClassNotFoundException cnfe )
		{
			/* this is also pretty bad. The parameters file
			is corrupt! Throw an exception. */
			throw new ParameterException( "The class " + classname +
					" specified in the properties file cannot be found" );
		}

		// we'll simply store these for later use
		m_parameters = parameters;
		m_logger.debug( "Tweety: Tweety got its parameters" );
	}

	/**
	 * Initialize tweety; We create an instance of all the components tweety contains.
	 */
	public void initialize()
	{
		// create the list that will hold all the components
		m_components = new HashMap();

		// all the roles
		String[] roles = m_parameters.getNames();

		// loop through each of them
		for ( int i=0; i < roles.length; i++ )
		{
			// Get the role of the component being setup
			String role = roles[i];

			Object component;
			String clazz = "";

			try
			{
				// get the class that implements that role
				clazz = m_parameters.getParameter( role );

				// create the component instance
				component = Class.forName( clazz ).newInstance();

				// add it to the list of components
				m_components.put( role, component );
			}
			catch( ParameterException pe ) { /* will never happen */ }
			catch( ClassNotFoundException cnfe ) { /* will never happen */ }
			catch( IllegalAccessException iae ) { /* will never happen */ }
			catch( InstantiationException ie )
			{
				// the component doesn't have a public no arguments constructor!

				// log error
				m_logger.error( "Tweety: The class " + clazz +
					    " specified properties file is not a valid avalon tweety component:" +
						" it doesn't have a public no-arguments constructor!" );
			}
		}

		// create the global ComponentManager
		m_componentManager = new DefaultComponentManager();

		// create the global ServiceManager
		m_serviceManager = new DefaultServiceManager();

		m_logger.debug( "Tweety: Tweety has been initialized" );
	}

	/**
	 * Start up tweety; We set up all the components tweety contains, add them to
	 * the global componentmanager and servicemanager, and finally we call start()
	 * on each of them.
	 */
	public void start()
	{
		m_logger.info( "Tweety: Tweety is starting up..." );

		// get all the roles
		Iterator it = m_components.keySet().iterator();

		// loop through them
	    while( it.hasNext() )
		{
			// current role
			String role = (String)it.next();
			// component that implements the role
			Object component = m_components.get( role );

			m_logger.info( "Tweety: Tweety is setting up the component implementing role '" + role + "'");

			try
			{
				// setup the component by running the appropriate lifecycle
				// methods in order. We use a very convenient utility to do
				// this.
				ContainerUtil.enableLogging(component, m_logger);
				ContainerUtil.contextualize(component, m_context);
				// note that this has been deprecated because compose()
				// itself has been deprecated. This is okay.
				ContainerUtil.compose      (component, m_componentManager);
				ContainerUtil.service      (component, m_serviceManager);
				ContainerUtil.configure    (component, m_configuration);
				ContainerUtil.parameterize (component, m_parameters);

				// put the new component in the servicemanager
				m_serviceManager.put( role, component );

				// put the new component in the componentmanager
				if( component instanceof org.apache.avalon.framework.component.Component )
				{
					m_componentManager.put( role, (Component) component );
				}

				// initialize component
				ContainerUtil.initialize   (component);
			}
			catch( Exception e )
			{
				// we failed to set up this component properly!

				// log the error
				m_logger.error(
					    "Tweety: We were unable to set up the component with the role: " + role +
						".\n The exception thrown was: " + e.getClass() +
						", with the message: " + e.getMessage() );

				// remove the component from our interal list so we don't try
				// and start() or stop() it later
				m_components.remove( role );
			}
		}

		// at this point, we know for sure that all the components still in the
		// internal list have been setup properly.

		// get all the roles
		it = m_components.keySet().iterator();

		// loop through these
		while( it.hasNext() )
		{
			// current role
			String role = (String) it.next();
			// current component
			Object component = m_components.get( role );

			m_logger.info( "Tweety: Tweety is starting the component implementing role " + role );

			try
			{
				// try to start it
				ContainerUtil.start( component );
			}
			catch( Exception e )
			{
				// we failed to start this component properly!

				// log the error
				m_logger.error(
					    "Tweety: We were unable to start the component with the role: " + role +
						".\n The exception thrown was: " + e.getClass().getName() +
						",\n with the message" + e.getMessage() );

				// remove the component from our interal list so we don't try
				// and stop() it later
				m_components.remove( role );
			}
		}

		m_logger.info( "Tweety: Tweety has started." );
	}

	/**
	 * Stop tweety; For each component tweety contains, we run their lifecycle from
	 * their stop() method right up to dispose().
	 */
	public void stop()
	{
		// get all the roles
		Iterator it = m_components.keySet().iterator();

		// loop through those
		while( it.hasNext() )
		{
			// current role
			String role = (String) it.next();
			// current component
			Object component = m_components.get( role );

			m_logger.info( "Tweety: Tweety is stopping the component implementing role " + role );

			try
			{
				// shut down the component by running the appropriate lifecycle
				// methods in order. We use a very convenient utility to do
				// this.
				ContainerUtil.stop( component );
				ContainerUtil.dispose( component );
			}
			catch( Exception e )
			{
				// we failed to stop this component properly!
				// log the problem, but don't do anything else
				m_logger.error(
					    "Tweety: We were unable to start the component with the role: " + role +
						".\n The exception thrown was: " + e.getClass().getName() +
						",\n with the message" + e.getMessage() );
			}
		}

		m_logger.info( "Tweety: Tweety has stopped." );
	}
}






