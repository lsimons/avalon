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

package org.apache.avalon.http.impl;

import java.io.File;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;

import org.apache.avalon.util.configuration.ConfigurationUtil;

import org.apache.avalon.composition.model.ComponentModel;

import org.apache.avalon.http.HttpService;

import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpListener;
import org.mortbay.http.SunJsseListener;
import org.mortbay.http.ajp.AJP13Listener;
import org.mortbay.util.Log;

/**
 * This is a sample service implementation that will be included 
 * within the template-impl deliverable.
 *
 * @avalon.component name="server" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.http.HttpService"
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class DefaultServer 
    implements LogEnabled, Contextualizable, Configurable,
               Initializable, Startable, HttpService
{
   //---------------------------------------------------------
   // static
   //---------------------------------------------------------

   private static final String LISTENER_TYPE_ATTRIBUTE_NAME = "type";
   private static final String KEYSTORE_ELEMENT_NAME = "keystore";
   private static final String FILE_ELEMENT_NAME = "file";
   private static final String PASSWORD_ELEMENT_NAME = "password";
   private static final String KEYPASSWORD_ELEMENT_NAME = "key-password";

   private static final String SOCKET_TYPE = "socket";
   private static final String JSSE_TYPE = "jsse";
   private static final String AJP_TYPE = "ajp";

   //---------------------------------------------------------
   // state
   //---------------------------------------------------------

   /**
    * The logging channel assigned by the container.
    */
    private Logger m_logger;

   /**
    * The supplied server configuration.
    */
    private Configuration m_config;

   /**
    * The http server.
    */
    private HttpServer m_server = new HttpServer();

   /**
    * The working base directory.
    */
    private File m_basedir;
    
   /** 
    * Mapping table for URL space to Servlet
    */
    private HashMap m_ServletMap;
     

   //---------------------------------------------------------
   // LogEnabled
   //---------------------------------------------------------

   /**
    * Handle the assignment of a logging channel by the 
    * container (this could be simplified if this class extended
    * the AbstractLogEnabled class).
    * 
    * @param logger the logging channel
    */
    public void enableLogging( Logger logger )
    {
        m_logger = logger;
    }

    private Logger getLogger()
    {
        return m_logger;
    }

   //---------------------------------------------------------
   // Contextualizable
   //---------------------------------------------------------

  /**
   * Contextualization of the server by the container.
   * @param context the supplied server context
   * @exception ContextException if a contextualization error occurs
   * @avalon.entry key="urn:composition:dir" type="java.io.File"
   */
   public void contextualize( Context context ) throws ContextException
   {
       m_basedir = getBaseDirectory( context );
   }

   //---------------------------------------------------------
   // Configurable
   //---------------------------------------------------------

  /**
   * Configuration of the server by the container.
   * @param config the supplied server configuration
   * @exception ConfigurationException if a configuration error occurs
   */
   public void configure( Configuration config ) throws ConfigurationException
   {
       m_config = config;
   }

   //---------------------------------------------------------
   // Initializable
   //---------------------------------------------------------

   /**
    * Initialization of the component by the container.
    */
    public void initialize() throws Exception
    {
        if( m_logger.isInfoEnabled() )
        {
            m_logger.debug( "initialization" );
        }

        m_server.setTrace( true );

        //
        // map the jetty logging channel to the avalon logger
        //

        AvalonLogSink sink = new AvalonLogSink();
        sink.enableLogging( m_logger );
        Log.instance().add( sink );

        //
        // if no listeners are declared create a default
        // socket listener on port 8080
        //

        if( null == m_config.getChild( "listener", false ))
        {
            SocketListener listener = new SocketListener();
            listener.setPort( 8080 );
            m_server.addListener( listener );
        }

        //
        // handle the children declared in the configuration
        // (includes listeners, etc.)
        //

        Configuration[] children = m_config.getChildren();
        for( int i=0; i<children.length; i++ )
        {
            Configuration child = children[i];
            String name = child.getName();
            
            if( "listeners".equalsIgnoreCase( name ) )
            {
                configureListeners( child );
            }
            else if( "mappings".equalsIgnoreCase( name ) )
            {
                configureMappings( child );
            }
            else
            {
                final String error = 
                  "Http server configuration contains an unrecognized element: [" 
                  + name + "]\n"
                  + ConfigurationUtil.list( child );
                throw new ConfigurationException( error );
            }
        }
    }

    private void configureListeners( Configuration conf )
        throws ConfigurationException, UnknownHostException
    {
        Configuration[] children = conf.getChildren( "listener" );
        for( int i=0 ; i < children.length ; i++ )
        {
            HttpListener listener = createListener( children[i] );
            m_server.addListener( listener );
        }
    }

    private void configureMappings( Configuration conf )
    {
        Configuration[] children = conf.getChildren( "mapping" );
        for( int i=0 ; i < children.length ; i++ )
        {
            Configuration servletConf = children[i];
            String component = servletConf.getChild( "component" ).getValue( "" );
            String url = servletConf.getChild( "url" ).getValue( "" );
            if( ! "".equals( component ) && ! "".equals( url ) )
                m_ServletMap.put( component.trim(), url.trim() );
        }
    }
        
    //---------------------------------------------------------
    // Startable
    //---------------------------------------------------------

   /**
    * Start the server.
    */
    public void start() throws Exception
    {
        m_server.start();
    }

   /**
    * Stop the server.
    */
    public void stop() throws Exception
    {
       m_server.stop();
    }

   //---------------------------------------------------------
   // HttpServer
   //---------------------------------------------------------

   /**
    * Register a servlet under a context.
    * @param model the component model
    */
    public void register( ComponentModel model )
    {
        final String path = model.getPath();
        final String url = (String) m_ServletMap.get( path );
        
        getLogger().info( 
          "registering servlet: " 
          + path + " to url: " + url );
        
        HttpContext context = m_server.getContext( path + "*" );
        ContainmentModelHandler handler = 
          getContainmentHandler( context, path );
        handler.addComponentModel( model );
        m_server.addContext( context );
        if( m_server.isStarted() )
        {
            try
            {
                context.start();
            }
            catch( Throwable e )
            {
                getLogger().error( "context startup failure", e );
            }
        }
    }

    private ContainmentModelHandler getContainmentHandler( 
      HttpContext context, String partition )
    {
        ContainmentModelHandler handler = 
          (ContainmentModelHandler) context.getHandler( 
             ContainmentModelHandler.class );
        if( null != handler ) return handler;
        handler = new ContainmentModelHandler( partition );
        context.addHandler( handler );
        return handler;
    }

   //---------------------------------------------------------
   // implementation
   //---------------------------------------------------------

    /**
     * Add a listener defined in the component configuration.
     * @param conf the listener configuration
     */
    private HttpListener createListener( Configuration conf ) 
        throws UnknownHostException
    {
        HttpListener listener = null;

        String listenerType = 
          conf.getAttribute(
            LISTENER_TYPE_ATTRIBUTE_NAME, 
            SOCKET_TYPE );
        
        if( listenerType.equals( AJP_TYPE ) ) 
        {
            listener = createAJP13Listener( conf, 2345 );
        }
        else if( listenerType.equals( JSSE_TYPE ) )
        {
            listener = createSunJsseListener( conf, 8443 );
        } 
        else if( listenerType.equals( SOCKET_TYPE ) )
        {
            listener = createSocketListener( conf, 8080 );
        }
        else 
        {
            final String error = 
              "Unrecognized listener type [" + listenerType + "].";
            throw new IllegalArgumentException( error );
        }
        return listener;
    }

    /**
     * Creates a new AJP13 Listener
     * @param config an AJP13 listener configuration
     * @param port the default port
     */
    private HttpListener createAJP13Listener( Configuration config, int port ) 
      throws UnknownHostException
    {
        AJP13Listener listener = new AJP13Listener();
        int timeout = config.getChild( "timeout" ).getValueAsInteger( 60000 );
        listener.setMaxIdleTimeMs( timeout );
        return setUpListener( listener, config, port );
    }

    /**
     * Creates a new socket listener
     * @param config an socket listener configuration
     * @param port the default port
     */
    private HttpListener createSocketListener( Configuration config, int port ) 
      throws UnknownHostException
    {
        SocketListener listener = new SocketListener();
        int timeout = config.getChild( "timeout" ).getValueAsInteger( 60000 );
        listener.setMaxIdleTimeMs( timeout );
        return setUpListener( listener, config, port );
    }

    /**
     * Creates a default sun jsse listener.
     * @param configuration
     * @param port the default port
     * @return The newly created sun jsse listener
     * @throws Exception
     */
    private HttpListener createSunJsseListener( Configuration conf, int port ) 
      throws UnknownHostException
    {
        SunJsseListener listener = new SunJsseListener();
        int timeout = conf.getChild( "timeout" ).getValueAsInteger( 60000 );
        listener.setMaxIdleTimeMs( timeout );

        Configuration ksconfig = conf.getChild( KEYSTORE_ELEMENT_NAME );
        Configuration fileconfig = ksconfig.getChild( FILE_ELEMENT_NAME );
        String fileName = fileconfig.getValue( "conf/.keystore" );

        File configuredFile = new File( fileName );
        if( ! configuredFile.isAbsolute() )
        {
            File keystore = new File( m_basedir, fileName );
            listener.setKeystore( keystore.getAbsolutePath() );
        }
        else 
        {
            listener.setKeystore( configuredFile.getAbsolutePath() );
        }

        listener.setPassword(
          ksconfig.getChild( 
            PASSWORD_ELEMENT_NAME ).getValue( null ) );
        listener.setKeyPassword(
          ksconfig.getChild(
            KEYPASSWORD_ELEMENT_NAME ).getValue( null ) );

        return setUpListener( listener, conf, port );
    }

    private HttpListener setUpListener( 
        HttpListener listener, Configuration config, int port )
        throws UnknownHostException
    {
        int configPort = config.getChild( "port" ).getValueAsInteger( port );
        String host = config.getChild( "hostname" ).getValue( "0.0.0.0" );
        
        listener.setPort( configPort );
        listener.setHost( host );
        return listener;
    }

    private String getDerivedPath( final String path )
    {
        if( null == path )
        {
            return m_basedir.toString();
        }
        else
        {
            return new File( m_basedir, path ).toString();
        }
    }

    private File getBaseDirectory( Context context ) 
      throws ContextException
    {
        return (File) 
          context.get( "urn:composition:dir" );
    }
}
