/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.avalon.util.thread.ThreadPool;
import org.apache.cornerstone.services.connection.ConnectionHandler;
import org.apache.cornerstone.services.connection.ConnectionHandlerFactory;

/**
 * Support class for the DefaultConnectionManager. 
 * This manages an individual ServerSocket.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class Connection
    extends AbstractLoggable
    implements Component, Runnable
{
    protected final ServerSocket               m_serverSocket;
    protected final ConnectionHandlerFactory   m_handlerFactory;
    protected final ThreadPool                 m_threadPool;
    protected final ArrayList                  m_runners          = new ArrayList();

    protected Thread                           m_thread;

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
        if( null != m_thread )
        {
            m_thread.interrupt();
            m_thread.join( /* 1000 ??? */ );
            m_thread = null;
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

        while( !Thread.interrupted() )
        {
            try
            {
                final Socket socket = m_serverSocket.accept();
                final ConnectionHandler handler = m_handlerFactory.createConnectionHandler();
                final ConnectionRunner runner = 
                    new ConnectionRunner( socket, m_runners, handler );
                setupLogger( runner );
                m_threadPool.execute( runner );
            }
            catch( final IOException ioe )
            {
                getLogger().error( "Exception accepting connection", ioe );
            }
            catch( final Exception e )
            {
                getLogger().error( "Exception executing runner", e );
            }
        }
    }
}
 
class ConnectionRunner
    extends AbstractLoggable
    implements Runnable, Component
{
    protected Socket             m_socket;
    protected Thread             m_thread;
    protected ArrayList          m_runners;
    protected ConnectionHandler  m_handler;

    ConnectionRunner( final Socket socket, 
                      final ArrayList runners, 
                      final ConnectionHandler handler )
    {
        m_socket = socket;
        m_runners = runners;
        m_handler = handler;
    }

    public void dispose()
        throws Exception
    {
        if( null != m_thread )
        {
            m_thread.interrupt();
            m_thread.join( /* 1000 ??? */ );
            m_thread = null;
        }
    }
        
    public void run()
    {
        try
        {
            m_thread = Thread.currentThread();
            m_runners.add( this );

            getLogger().debug( "Starting connection on " + m_socket );
            setupLogger( m_handler );
            m_handler.handleConnection( m_socket ); 
            getLogger().debug( "Ending connection on " + m_socket );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error handling connection", e );
        }
        finally
        {
            try { m_socket.close(); }
            catch( final IOException ioe ) 
            { 
                getLogger().warn( "Error shutting down connection", ioe );
            }
                
            m_runners.remove( this );
        }
    }
}
