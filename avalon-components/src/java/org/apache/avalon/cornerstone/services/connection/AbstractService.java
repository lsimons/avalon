/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.connection;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.IOException;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;

/**
 * Helper class to create protocol services.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class AbstractService
    extends AbstractLogEnabled
    implements Contextualizable, Composable, Configurable, Initializable, Disposable
{
    protected ConnectionManager m_connectionManager;
    protected SocketManager m_socketManager;
    protected ConnectionHandlerFactory m_factory;
    protected ThreadPool m_threadPool;
    protected String m_serverSocketType;
    protected int m_port;
    protected InetAddress m_bindTo; //network interface to bind to
    protected ServerSocket m_serverSocket;
    protected String m_connectionName;

    public AbstractService()
    {
        m_factory = createFactory();
        m_serverSocketType = "plain";
    }

    protected String getThreadPoolName()
    {
        return null;
    }

    protected abstract ConnectionHandlerFactory createFactory();

    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_factory );
    }

    public void contextualize( final Context context )
        throws ContextException
    {
        final String name = getThreadPoolName();

        if( null != name )
        {
            final BlockContext blockContext = (BlockContext)context;
            m_threadPool = blockContext.getThreadPool( name );
        }

        if( m_factory instanceof Contextualizable )
        {
            ( (Contextualizable)m_factory ).contextualize( context );
        }
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_connectionManager = (ConnectionManager)componentManager.lookup( ConnectionManager.ROLE );
        m_socketManager = (SocketManager)componentManager.lookup( SocketManager.ROLE );

        if( m_factory instanceof Composable )
        {
            ( (Composable)m_factory ).compose( componentManager );
        }
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        if( m_factory instanceof Configurable )
        {
            ( (Configurable)m_factory ).configure( configuration );
        }
    }

    public void initialize()
        throws Exception
    {
        if( m_factory instanceof Initializable )
        {
            ( (Initializable)m_factory ).initialize();
        }

        if( null == m_connectionName )
        {
            final StringBuffer sb = new StringBuffer();
            sb.append( m_serverSocketType );
            sb.append( ':' );
            sb.append( m_port );

            if( null != m_bindTo )
            {
                sb.append( '/' );
                sb.append( m_bindTo );
            }

            m_connectionName = sb.toString();
        }

        final ServerSocketFactory factory =
            m_socketManager.getServerSocketFactory( m_serverSocketType );

        if( null == m_bindTo )
        {
            m_serverSocket = factory.createServerSocket( m_port );
        }
        else
        {
            m_serverSocket = factory.createServerSocket( m_port, 5, m_bindTo );
        }

        if( null == m_threadPool )
        {
            m_connectionManager.connect( m_connectionName, m_serverSocket,
                                         m_factory );
        }
        else
        {
            m_connectionManager.connect( m_connectionName, m_serverSocket,
                                         m_factory, m_threadPool );
        }
    }

    public void dispose()
    {
        try
        {
            m_connectionManager.disconnect( m_connectionName );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error disconnecting", e );
        }

        try
        {
            m_serverSocket.close();
        }
        catch( final IOException ioe )
        {
            getLogger().warn( "Error closing server socket", ioe );
        }
    }
}
