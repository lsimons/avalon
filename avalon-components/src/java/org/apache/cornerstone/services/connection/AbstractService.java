/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.connection;

import java.net.InetAddress;
import java.net.ServerSocket;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.Loggable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.util.thread.ThreadPool;
import org.apache.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.cornerstone.services.sockets.SocketManager;
import org.apache.log.Logger;
import org.apache.phoenix.Block;
import org.apache.phoenix.BlockContext;

/**
 * Helper class to create protocol services.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractService
    extends AbstractLoggable
    implements Block, Contextualizable, Composer, Configurable, Initializable, Disposable 
{
    protected ConnectionManager        m_connectionManager;
    protected SocketManager            m_socketManager;
    protected ConnectionHandlerFactory m_factory;
    protected ThreadPool               m_threadPool;
    protected String                   m_serverSocketType;
    protected int                      m_port;
    protected InetAddress              m_bindTo; //network interface to bind to
    protected String                   m_connectionName;

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

    public void setLogger( final Logger logger )
    {
        super.setLogger( logger );

        if( m_factory instanceof Loggable )
        {
            ((Loggable)m_factory).setLogger( logger );
        }
    }
    
    public void contextualize( final Context context )
    {
        final String name = getThreadPoolName();

        if( null != name )
        {
            final BlockContext blockContext = (BlockContext)context;
            m_threadPool = blockContext.getThreadPool( name );
        }

        if( m_factory instanceof Contextualizable )
        {
            ((Contextualizable)m_factory).contextualize( context );
        }
    }
    
    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_connectionManager = (ConnectionManager)componentManager.
            lookup( "org.apache.cornerstone.services.connection.ConnectionManager" );

        m_socketManager = (SocketManager)componentManager.
            lookup( "org.apache.cornerstone.services.sockets.SocketManager" );

        if( m_factory instanceof Composer )
        {
            ((Composer)m_factory).compose( componentManager );
        }
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        if( m_factory instanceof Configurable )
        {
            ((Configurable)m_factory).configure( configuration );
        }
    }

    public void init() 
        throws Exception
    {
        if( m_factory instanceof Initializable )
        {
            ((Initializable)m_factory).init();
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

        ServerSocket serverSocket = null;
        
        if( null == m_bindTo )
        {
            serverSocket = factory.createServerSocket( m_port );
        }
        else
        {
            serverSocket = factory.createServerSocket( m_port, 5, m_bindTo );
        }

        if( null == m_threadPool )
        {
            m_connectionManager.connect( m_connectionName, serverSocket, m_factory );
        }
        else
        {
            m_connectionManager.
                connect( m_connectionName, serverSocket, m_factory, m_threadPool );
        }
    }

    public void dispose()
        throws Exception
    {
        m_connectionManager.disconnect( m_connectionName );
    }
}
