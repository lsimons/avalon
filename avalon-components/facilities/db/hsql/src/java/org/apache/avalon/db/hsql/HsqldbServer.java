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

package org.apache.avalon.db.hsql;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;

import org.apache.avalon.db.DatabaseService;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.Logger;
import org.hsqldb.HsqlServerFactory;
import org.hsqldb.HsqlSocketRequestHandler;

import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * This is a database server facility for the HypersonicSQL database.
 *
 * @avalon.component name="server" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.db.DatabaseService"
 * @author  <a href="mailto:exterminatorx@comcast.net">Timothy Bennett</a>
 */
public class HsqldbServer
  implements Contextualizable, Configurable, Initializable,
  Startable, DatabaseService
{
    //---------------------------------------------------------
    // static
    //---------------------------------------------------------

    /**
     * Default HSQL database server TCP socket listening port.
     * The default value is the HSQL standard port 9001.
     */
    private static final int DEFAULT_PORT = 9001;
    /**
     * Default host name to bind the HSQL database server TCP socket
     * to.  The default value is the localhost, that is 127.0.0.1.
     */
    private static final String DEFAULT_HOST = "127.0.0.1";
    /**
     * TCP listening socket backlog.
     */
    private static final int BACKLOG = 50;
    /**
     * TCP listening socket connection timeout (msecs).
     */
    private static final int TIMEOUT = 500;

    //---------------------------------------------------------
    // immutable state
    //---------------------------------------------------------

    /**
     * The logging channel for this component.
     */
    private final Logger m_logger;

    //---------------------------------------------------------
    // state
    //---------------------------------------------------------

    /**
     * Main thread managing the server connection socket.
     */
    private Thread m_serverSocketThread;

    /**
     * The HSQLDB database server and socket request handler.
     */
    private HsqlSocketRequestHandler m_hsqlSocketRequestHandler;

    /**
     * The working base directory.
     */
    private File m_basedir;

    /**
     * Listening port for database connections. Default is 9001;
     */
    private int m_port;

    /**
     * Host to bind the database TCP/IP listener to. Default is localhost.
     */
    private InetAddress m_host;

    /**
     * Fully qualified filepath to the database file artifacts.
     */
    private File m_dbName;

    /**
     * Run the HSQLDB server in debug mode?
     */
    private boolean m_dbDebug;

    /**
     * Run the HSQLDB server silently?
     */
    private boolean m_dbSilent;

    //---------------------------------------------------------
    // constructor
    //---------------------------------------------------------

    /**
     * Creates a new instance of the HSQL database server with the
     * logging channel supplied by the container.
     *
     * @param logger the container-supplied logging channel
     */
    public HsqldbServer( final Logger logger )
    {
        m_logger = logger;
    }

    //---------------------------------------------------------
    // Contextualizable
    //---------------------------------------------------------

   /**
    * Contextualization of the server by the container.
    *
    * @param context the supplied server context
    * @exception ContextException if a contextualization error occurs
    * @avalon.entry key="urn:avalon:home" type="java.io.File" alias="app.home"
    */
    public void contextualize( final Context context )
      throws ContextException
    {
        // cache the working base directory
        m_basedir = getBaseDirectory( context );
    }

    //---------------------------------------------------------
    // Configurable
    //---------------------------------------------------------

    /**
     * Configuration of the server by the container.
     *
     * @param config the supplied server configuration
     * @exception ConfigurationException if a configuration error occurs
     */
    public void configure( final Configuration config )
      throws ConfigurationException
    {
        // get the database listening port/host
        m_port = config.getChild( "port" ).getValueAsInteger( DEFAULT_PORT );
        String host = config.getChild( "host" ).getValue( DEFAULT_HOST );
        try
        {
            m_host = InetAddress.getByName( host );
        }
        catch ( UnknownHostException uhe )
        {
            if ( getLogger().isErrorEnabled() )
            {
                getLogger().error(
                  "Error configuring HSQLDB component: "
                    + uhe.getMessage(),
                  uhe );
            }
            throw new ConfigurationException( uhe.getMessage(), uhe );
        }

        // get the HSQLDB server parameters...
        m_dbName = new File( m_basedir,
            config.getChild( "db-name" ).getValue( "avalon-hsqldb" ) );
        m_dbDebug = config.getChild( "debug" ).getValueAsBoolean( false );
        m_dbSilent = config.getChild( "silent" ).getValueAsBoolean( true );
        
        // how are we configured?
        this.toString();
    }

    //---------------------------------------------------------
    // Initializable
    //---------------------------------------------------------

    /**
     * Initialization of the component by the container.
     * 
     * @throws Exception if an error occurs while initializing
     * the component
     */
    public void initialize() throws Exception 
    {
        // nothing yet to initialize        
    }

    //---------------------------------------------------------
    // Startable
    //---------------------------------------------------------

    /**
     * Start the server.
     * 
     * @throws Exception if an error occurs while starting the 
     * component
     */
    public void start() throws Exception 
    {
        // create the HSQLDB server and socket request handler
        try 
        {
            m_hsqlSocketRequestHandler = 
              HsqlServerFactory.createHsqlServer(
                m_dbName.getAbsolutePath(), 
                m_dbDebug, 
                m_dbSilent );
        } 
        catch ( SQLException sqle ) 
        {
            if ( m_logger.isErrorEnabled() ) 
            {
                final String error = 
                  "Error starting HSQL database: " 
                  + sqle.getMessage();
                m_logger.error( error, sqle );
            }
            throw sqle;
        }
        
        // start up primary socket server 
        final ServerSocket serverSocket = new ServerSocket( m_port,
            BACKLOG, m_host );
        serverSocket.setSoTimeout( TIMEOUT );
        m_serverSocketThread = 
          new Thread( new Runnable() {
            public void run()
            {
                runServer( serverSocket );
            }
          } );
        m_serverSocketThread.start();
        
        if ( m_logger.isInfoEnabled() ) 
        {
            m_logger.info( "Hypersonic SQL listening on port " + m_port );
        }
    }

    /**
     * Stop the server.
     * 
     * @throws Exception if an error occurs while stopping the component
     */
    public void stop() throws Exception 
    {
        // tell HSQLDB that all connections are being shutdown...
        if ( m_hsqlSocketRequestHandler != null ) 
        {
            m_hsqlSocketRequestHandler.signalCloseAllServerConnections();
        }
        
        // shutdown server socket...
        if ( m_serverSocketThread != null ) 
        {
            m_serverSocketThread.interrupt();
        }
    }

    /**
     * Starts the socket server and hands off connections to the thread pool for
     * processing by a worker thread.
     * 
     * @param serverSocket the serverSocket to manage
     */
    private void runServer( ServerSocket serverSocket ) 
    {
        if ( m_logger.isInfoEnabled() ) 
        {
            m_logger.info( "Running database connection server..." );
        }
        
        ThreadedExecutor runner = new ThreadedExecutor();
        try 
        {
            // accept connections until this thread is interrupted...
            while ( !Thread.interrupted() ) 
            {
                try
                {
                    // when a connection is made, spin off a thread to
                    // process the connection, and immediately go back to
                    // listening for other connections
                    final Socket connection = serverSocket.accept();
                    runner.execute( 
                      new Runnable() 
                      {
                          public void run() 
                          {
                            // who connected to us?
                            final String remoteHost = 
                              connection.getInetAddress().getHostName();
                            final String remoteIP = 
                              connection.getInetAddress().getHostAddress();
                            
                            // hand-off the socket connection to HSQLDB...
                            m_hsqlSocketRequestHandler.handleConnection( connection );

                            if ( m_logger.isDebugEnabled() ) 
                            {
                                final String message = 
                                  "database connection from " 
                                  + remoteHost 
                                  + " (" 
                                  + remoteIP 
                                  + ")";
                                m_logger.debug( message );
                            }
                        }
                    } );
                } 
                catch ( InterruptedIOException iioe ) 
                {
                    // Ignore these - thrown when accept() times out
                }
                catch ( Exception e ) 
                {
                    // IOException - if an I/O error occurs when waiting 
                    // for a connection.
                    // SecurityException - if a security manager exists 
                    // and its checkListen method doesn't allow the operation.
                    if ( getLogger().isWarnEnabled() ) 
                    {
                        getLogger().warn( e.getMessage(), e );
                    }
                }
            }
        } 
        finally 
        {
            if ( runner != null ) 
            {
                runner = null;
            }
            if ( serverSocket != null ) 
            {
                try 
                {
                    serverSocket.close();
                } 
                catch ( IOException ignore ) 
                {
                    // ignore...
                }
                serverSocket = null;
            }
        }
    }
    
    /**
     * Return the working base directory.
     * 
     * @param context the supplied server context
     * @return a <code>File</code> object representing the working
     * base directory
     * @exception ContextException if a contextualization error occurs
     */
    private File getBaseDirectory( final Context context ) 
      throws ContextException 
    {
        File home = (File) context.get( "urn:avalon:home" );
        if ( !home.exists() )
        {
            boolean success = home.mkdirs();
            if ( !success )
            {
                throw new ContextException( "Unable to create component "
                    + "home directory: " + home.getAbsolutePath() );
            }
        }
        if ( getLogger().isInfoEnabled() )
        {
            getLogger().info( "Setting home directory to: " + home );
        }
        return home;
    }
    
   /**
    * Return the assigned logging channel.
    * @return the logging channel
    */
    private Logger getLogger()
    {
        return m_logger;
    }

    /**
     * Returns a <code>String</code> representation of this component.
     * 
     * @return a <code>String</code> representation of this component
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[HsqldbServer:" );
        buffer.append( " m_basedir: " );
        buffer.append( m_basedir );
        buffer.append( " m_port: " );
        buffer.append( m_port );
        buffer.append( " m_host: " );
        buffer.append( m_host );
        buffer.append( " m_dbName: " );
        buffer.append( m_dbName );
        buffer.append( " m_dbDebug: " );
        buffer.append( m_dbDebug );
        buffer.append( " m_dbSilent: " );
        buffer.append( m_dbSilent );
        buffer.append( "]" );
        return buffer.toString();
    }
}
