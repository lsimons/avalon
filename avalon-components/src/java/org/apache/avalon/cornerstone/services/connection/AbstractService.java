/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.component.ComponentException;

/**
 * Helper class to create protocol services.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class AbstractService
    extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Configurable, Initializable, Disposable
{
    protected ConnectionManager m_connectionManager;
    protected SocketManager m_socketManager;
    protected ConnectionHandlerFactory m_factory;
    protected ThreadManager m_threadManager;
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
        ContainerUtil.enableLogging( m_factory, logger );
    }

    public void contextualize( final Context context )
        throws ContextException
    {
        ContainerUtil.contextualize( m_factory, context );
    }

    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_connectionManager = (ConnectionManager)serviceManager.lookup( ConnectionManager.ROLE );
        m_socketManager = (SocketManager)serviceManager.lookup( SocketManager.ROLE );
        if( null != getThreadPoolName() )
        {
            m_threadManager =
                (ThreadManager)serviceManager.lookup( ThreadManager.ROLE );
            m_threadPool = m_threadManager.getThreadPool( getThreadPoolName() );
        }
        ContainerUtil.service( m_factory, serviceManager );
        try
        {
            ContainerUtil.compose( m_factory, new AdaptingComponentManager( serviceManager ) );
        }
        catch( final ComponentException ce )
        {
            throw new ServiceException( ce.getMessage(), ce );
        }
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        ContainerUtil.configure( m_factory, configuration );
    }

    public void initialize()
        throws Exception
    {
        ContainerUtil.initialize( m_factory );

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
            final String message = "Error disconnecting";
            getLogger().warn( message, e );
        }

        try
        {
            m_serverSocket.close();
        }
        catch( final IOException ioe )
        {
            final String message = "Error closing server socket";
            getLogger().warn( message, ioe );
        }
    }
}
