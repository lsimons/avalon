/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Support class for the DefaultConnectionManager.
 * This manages an individual ServerSocket.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
class Connection
    extends AbstractLogEnabled
    implements Component, Runnable
{
    private final ServerSocket m_serverSocket;
    private final ConnectionHandlerFactory m_handlerFactory;
    private final ThreadPool m_threadPool;
    private final Vector m_runners = new Vector();

    //Need to synchronize access to thread object
    private Thread m_thread;

    public Connection( final ServerSocket serverSocket,
                       final ConnectionHandlerFactory handlerFactory,
                       final ThreadPool threadPool )
    {
        m_serverSocket = serverSocket;
        m_handlerFactory = handlerFactory;
        m_threadPool = threadPool;
    }

    public void dispose()
        throws Exception
    {
        synchronized( this )
        {
            if( null != m_thread )
            {
                final Thread thread = m_thread;
                m_thread = null;
                thread.interrupt();

                //Can not join as threads are part of pool
                //and will never finish
                //m_thread.join();

                wait( /*1000*/ );
            }
        }

        final Iterator runners = m_runners.iterator();
        while( runners.hasNext() )
        {
            final ConnectionRunner runner = (ConnectionRunner)runners.next();
            runner.dispose();
        }

        m_runners.clear();
    }

    public void run()
    {
        m_thread = Thread.currentThread();

        while( null != m_thread && !Thread.interrupted() )
        {
            //synchronized( this )
            //{
            //if( null == m_thread ) break;
            //}

            try
            {
                final Socket socket = m_serverSocket.accept();
                final ConnectionRunner runner =
                    new ConnectionRunner( socket, m_runners, m_handlerFactory );
                setupLogger( runner );
                m_threadPool.execute( runner );
            }
            catch( final InterruptedIOException iioe )
            {
                //Consume exception
            }
            catch( final IOException ioe )
            {
                final String message = "Exception accepting connection";
                getLogger().error( message, ioe );
            }
            catch( final Exception e )
            {
                final String message = "Exception executing runner";
                getLogger().error( message, e );
            }
        }

        synchronized( this )
        {
            notifyAll();
            m_thread = null;
        }
    }
}

class ConnectionRunner
    extends AbstractLogEnabled
    implements Runnable, Component
{
    private Socket m_socket;
    private Thread m_thread;
    private List m_runners;
    private ConnectionHandlerFactory m_handlerFactory;

    ConnectionRunner( final Socket socket,
                      final List runners,
                      final ConnectionHandlerFactory handlerFactory )
    {
        m_socket = socket;
        m_runners = runners;
        m_handlerFactory = handlerFactory;
    }

    public void dispose()
        throws Exception
    {
        synchronized( this )
        {
            if( null != m_thread )
            {
                m_thread.interrupt();
                m_thread = null;
                //Can not join as threads are part of pool
                //and will never finish
                //m_thread.join();

                wait( /*1000*/ );
            }
        }
    }

    public void run()
    {
        ConnectionHandler handler = null;
        try
        {
            m_thread = Thread.currentThread();
            m_runners.add( this );

            debugBanner( true );

            handler = m_handlerFactory.createConnectionHandler();
            handler.handleConnection( m_socket );

            debugBanner( false );
        }
        catch( final Exception e )
        {
            final String message = "Error handling connection";
            getLogger().warn( message, e );
        }

        if( null != handler )
        {
            m_handlerFactory.releaseConnectionHandler( handler );
        }

        shutdownSocket();

        synchronized( this )
        {
            m_thread = null;
            m_runners.remove( this );

            notifyAll();
        }

    }

    /**
     * Print out debug banner indicating that handling of a connection
     * is starting or ending.
     *
     * @param starting true if starting, false othrewise
     */
    private void debugBanner( final boolean starting )
    {
        if( getLogger().isDebugEnabled() )
        {
            final String prefix = ( starting ) ? "Starting" : "Ending";
            final String message =
                prefix + " connection on " +
                m_socket.getInetAddress().getHostAddress();
            getLogger().debug( message );
        }
    }

    /**
     * Utility method for shutting down associated socket.
     */
    private void shutdownSocket()
    {
        try
        {
            m_socket.close();
        }
        catch( final IOException ioe )
        {
            final String message = "Error shutting down connection";
            getLogger().warn( message, ioe );
        }
    }
}
