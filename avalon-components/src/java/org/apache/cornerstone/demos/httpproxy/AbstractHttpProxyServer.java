/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Initializable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.cornerstone.services.connection.ConnectionHandler;
import org.apache.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.cornerstone.services.connection.ConnectionManager;
import org.apache.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.cornerstone.services.sockets.SocketManager;
import org.apache.phoenix.Block;
import org.apache.phoenix.BlockContext;
import org.apache.phoenix.Service;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public abstract class AbstractHttpProxyServer
    extends AbstractLoggable
    implements Block, Contextualizable, Composer, Configurable, Service, 
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
        m_port = configuration.getChild( "listen-port" ).getValueAsInt( 8000 );

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
        throws ComponentManagerException
    {
        getLogger().info( "HttpProxyServer-" + m_name + ".compose()" );

        m_socketManager = (SocketManager)componentManager.
            lookup( "org.apache.cornerstone.services.sockets.SocketManager" );

        m_connectionManager = (ConnectionManager)componentManager.
            lookup( "org.apache.cornerstone.services.connection.ConnectionManager" );
    }
    
    public void init()
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

