/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.httpproxy;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.Service;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public abstract class AbstractHttpProxyServer
    extends AbstractLoggable
    implements Block, Contextualizable, Composable, Configurable, Service,
               Initializable, ConnectionHandlerFactory
{
    protected SocketManager       m_socketManager;
    protected ConnectionManager   m_connectionManager;

    protected String              m_name;
    protected String              m_forwardToAnotherProxy;
    protected int                 m_port;
    protected InetAddress         m_bindTo;
    protected BlockContext        m_context;

    public AbstractHttpProxyServer( final String name )
    {
        m_name = name;
    }

    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    public void configure( final Configuration configuration  )
        throws ConfigurationException
    {
        m_port = configuration.getChild( "listen-port" ).getValueAsInteger( 8000 );

        try
        {
            final String bindAddress = configuration.getChild( "bind" ).getValue();
            m_bindTo = InetAddress.getByName( bindAddress );
        }
        catch( final UnknownHostException unhe )
        {
            throw new ConfigurationException( "Malformed bind parameter", unhe );
        }

        final Configuration forward = configuration.getChild( "forward-to-another-proxy" );
        m_forwardToAnotherProxy = forward.getValue("");
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        getLogger().info( "HttpProxyServer-" + m_name + ".compose()" );

        m_socketManager = (SocketManager)componentManager.
            lookup( "org.apache.avalon.cornerstone.services.sockets.SocketManager" );

        m_connectionManager = (ConnectionManager)componentManager.
            lookup( "org.apache.avalon.cornerstone.services.connection.ConnectionManager" );
    }

    public void initialize()
        throws Exception
    {
        final ServerSocketFactory factory =
            m_socketManager.getServerSocketFactory( "plain" );
        final ServerSocket serverSocket = factory.createServerSocket( m_port, 5, m_bindTo );

        m_connectionManager.connect( "HttpProxyListener-" + m_name, serverSocket, this );
    }

    /**
     * Construct an appropriate ConnectionHandler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    public ConnectionHandler createConnectionHandler()
        throws Exception
    {
        final HttpProxyHandler handler = newHttpProxyHandler();
        setupLogger( handler );
        return handler;
    }

    protected abstract HttpProxyHandler newHttpProxyHandler();
}

