/* 
 * Copyright 1999-2004 The Apache Software Foundation
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

package org.apache.avalon.cornerstone.blocks.connection;

import java.net.ServerSocket;
import java.util.HashMap;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;
import org.apache.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * This is the service through which ConnectionManagement occurs.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @avalon.component name="connection-manager" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.cornerstone.services.connection.ConnectionManager"
 */
public class DefaultConnectionManager
    extends AbstractLogEnabled
    implements ConnectionManager, Serviceable, Disposable
{
    private final HashMap m_connections = new HashMap();
    private ThreadManager m_threadManager;

   /**
    * @avalon.dependency type="org.apache.avalon.cornerstone.services.threads.ThreadManager"
    */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_threadManager = (ThreadManager)serviceManager.lookup( ThreadManager.ROLE );
    }

    public void dispose()
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "disposal" );
        }
        final String[] names = (String[])m_connections.keySet().toArray( new String[ 0 ] );
        for( int i = 0; i < names.length; i++ )
        {
            try
            {
                disconnect( names[ i ] );
            }
            catch( final Exception e )
            {
                final String message = "Error disconnecting " + names[ i ];
                getLogger().warn( message, e );
            }
        }
    }

    /**
     * Start managing a connection.
     * Management involves accepting connections and farming them out to threads
     * from pool to be handled.
     *
     * @param name the name of connection
     * @param socket the ServerSocket from which to
     * @param handlerFactory the factory from which to aquire handlers
     * @param threadPool the thread pool to use
     * @exception Exception if an error occurs
     */
    public synchronized void connect( String name,
                                      ServerSocket socket,
                                      ConnectionHandlerFactory handlerFactory,
                                      ThreadPool threadPool )
        throws Exception
    {
        if( null != m_connections.get( name ) )
        {
            final String message = "Connection already exists with name " + name;
            throw new IllegalArgumentException( message );
        }

        //Make sure timeout is specified for socket.
        if( 0 == socket.getSoTimeout() )
        {
            socket.setSoTimeout( 500 );
        }

        final Connection runner =
            new Connection( socket, handlerFactory, threadPool );
        setupLogger( runner );
        m_connections.put( name, runner );
        threadPool.execute( runner );
    }

    /**
     * Start managing a connection.
     * This is similar to other connect method except that it uses default thread pool.
     *
     * @param name the name of connection
     * @param socket the ServerSocket from which to
     * @param handlerFactory the factory from which to aquire handlers
     * @exception Exception if an error occurs
     */
    public void connect( String name,
                         ServerSocket socket,
                         ConnectionHandlerFactory handlerFactory )
        throws Exception
    {
        connect( name, socket, handlerFactory, m_threadManager.getDefaultThreadPool() );
    }

    /**
     * This shuts down all handlers and socket, waiting for each to gracefully shutdown.
     *
     * @param name the name of connection
     * @exception Exception if an error occurs
     */
    public void disconnect( final String name )
        throws Exception
    {
        disconnect( name, false );
    }

    /**
     * This shuts down all handlers and socket.
     * If tearDown is true then it will forcefully shutdown all connections and try
     * to return as soon as possible. Otherwise it will behave the same as
     * void disconnect( String name );
     *
     * @param name the name of connection
     * @param tearDown if true will forcefully tear down all handlers
     * @exception Exception if an error occurs
     */
    public synchronized void disconnect( final String name, final boolean tearDown )
        throws Exception
    {
        final Connection connection = (Connection)m_connections.remove( name );

        if( connection != null )
        {
            //TODO: Stop ignoring tearDown
            connection.dispose();
        }
        else
        {
            final String error =
                "Invalid request for the disconnection of an unrecognized connection name: "
                + name;
            throw new IllegalArgumentException( error );
        }
    }
}
