/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.Block;

/**
 * This is the service through which ConnectionManagement occurs.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 *
 * @phoenix:version 1.0
 * @phoenix:service name="org.apache.avalon.cornerstone.services.connection.ConnectionManager"
 */
public class DefaultConnectionManager
    extends AbstractLogEnabled
    implements Block, ConnectionManager, Composable, Disposable
{
    private HashMap             m_connections        = new HashMap();
    private ThreadManager       m_threadManager;

    /**
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.threads.ThreadManager"
     */
    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_threadManager = (ThreadManager)componentManager.lookup( ThreadManager.ROLE );
    }

    public void dispose()
    {
        final String[] names = (String[])m_connections.keySet().toArray( new String[ 0 ] );
        for( int i = 0; i < names.length; i++ )
        {
            try { disconnect( names[ i ] ); }
            catch( final Exception e )
            {
                getLogger().warn( "Error disconnecting " + names[ i ], e );
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
    public void connect( String name,
                         ServerSocket socket,
                         ConnectionHandlerFactory handlerFactory,
                         ThreadPool threadPool )
        throws Exception
    {
        if( null != m_connections.get( name ) )
        {
            throw new IllegalArgumentException( "Connection already exists with name " +
                                                name );
        }

        //Make sure timeout is specified for socket.
        if( 0 == socket.getSoTimeout() )
        {
            socket.setSoTimeout( 500 );
        }

        final Connection runner = new Connection( socket, handlerFactory, threadPool );
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
    public void disconnect( final String name, final boolean tearDown )
        throws Exception
    {
        final Connection connection = (Connection)m_connections.remove( name );
        if( null == connection )
        {
            throw new IllegalArgumentException( "No such connection with name " +
                                                name );
        }

        //TODO: Stop ignoring tearDown
        connection.dispose();
    }
}
