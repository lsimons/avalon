/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.helloworldserver;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.context.Context;
import org.apache.avalon.context.Contextualizable;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.cornerstone.services.connection.ConnectionHandler;
import org.apache.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.cornerstone.services.connection.ConnectionManager;
import org.apache.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.cornerstone.services.sockets.SocketManager;
import org.apache.phoenix.Block;
import org.apache.phoenix.BlockContext;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @author  Federico Barbieri <scoobie@pop.systemy.it>
 * @version 1.0
 */
public class HelloWorldServerImpl
    extends AbstractLoggable
    implements Block, HelloWorldServer,
               Contextualizable, Composable, Configurable, Initializable, ConnectionHandlerFactory
{
    protected SocketManager       m_socketManager;
    protected ConnectionManager   m_connectionManager;

    protected BlockContext        m_context;
    protected String              m_greeting          = "Hello World";
    protected InetAddress         m_bindTo;
    protected int                 m_port;

    public void setGreeting( final String greeting )
    {
        m_greeting = greeting;
    }

    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getChild("port").getValueAsInteger( 8000 );

        try
        {
            final String bindAddress = configuration.getChild( "bind" ).getValue();
            m_bindTo = InetAddress.getByName( bindAddress );
        }
        catch( final UnknownHostException unhe )
        {
            throw new ConfigurationException( "Malformed bind parameter", unhe );
        }
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        getLogger().info("HelloWorldServer.compose()");

        m_socketManager = (SocketManager)componentManager.
            lookup( "org.apache.cornerstone.services.sockets.SocketManager" );

        m_connectionManager = (ConnectionManager)componentManager.
            lookup( "org.apache.cornerstone.services.connection.ConnectionManager" );
    }

    public void initialize()
        throws Exception
    {
        final ServerSocketFactory factory =
            m_socketManager.getServerSocketFactory( "plain" );
        final ServerSocket serverSocket = factory.createServerSocket( m_port, 5, m_bindTo );

        m_connectionManager.connect( "HelloWorldListener", serverSocket, this );
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
        final HelloWorldHandler handler = new HelloWorldHandler( m_greeting );
        setupLogger( handler );
        return handler;
    }
}
