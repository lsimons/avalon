/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.xcommander;

import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.cornerstone.demos.xcommander.xcommands.Chat;
import org.apache.avalon.cornerstone.demos.xcommander.xcommands.Echo;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.phoenix.Block;

/**
 * A socket server which listens for XCommander requests.
 *
 * Based on SimpleServer &amp; helloworldserver. this Server is the entry-point
 * for XCommander. It listens on a port specified in the block's configuration,
 * and delegates incoming requests to an {@link XCommanderHandler XCommanderHandler}.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class XCommanderServer
    extends AbstractLoggable
    implements Block, Composable, Configurable, Initializable,
               ConnectionHandlerFactory, XCommanderService, CommandHandler
{
    // block stuff
    protected Configuration           m_configuration;
    protected SocketManager           m_socketManager;
    protected ConnectionManager       m_connectionManager;

    protected ArrayList               m_clients;
    protected HashMap                 m_xcommands;

    // BLOCK METHODS
    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_socketManager = (SocketManager)componentManager.lookup( SocketManager.ROLE );
        m_connectionManager = (ConnectionManager)componentManager.lookup( ConnectionManager.ROLE );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        m_clients = new ArrayList();
        m_xcommands = new HashMap();

        try
        {
            this.addCommand("xcommander.Echo", Echo.class );
            this.addCommand("org.apache.avalon.cornerstone.demos.xcommander.roles.Echo", Echo.class );
            this.addCommand("xcommander.Chat", Chat.class );
            this.addCommand("org.apache.avalon.cornerstone.demos.xcommander.roles.Chat", Chat.class );
        }
        catch( final Exception e )
        {
            // never happens...
        }

        getLogger().info( "init XCommanderServer ..." );

        final int port = m_configuration.getChild( "port" ).getValueAsInteger();
        getLogger().info( "Want to open port on:" + port );

        final ServerSocketFactory factory =
            m_socketManager.getServerSocketFactory( "plain" );
        final ServerSocket serverSocket = factory.createServerSocket( port );

        m_connectionManager.connect( "XCommanderListener", serverSocket, this );

        getLogger().info( "Got socket" );

        getLogger().info( "...XCommanderServer init" );
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
        final XCommanderHandler handler = new XCommanderHandler( this );
        setupLogger( handler );
        handler.initialize();
        return handler;
    }

    void addClient( final XCommanderHandler handler )
    {
        m_clients.add( handler );
    }

    void removeClient( final XCommanderHandler handler )
    {
        m_clients.remove( handler );
    }

    public void handleCommand( final String type,
                               final String identifier,
                               final Object result )
    {
        if( result instanceof GlobalResult )
        {
            Iterator it = m_clients.iterator();
            while(it.hasNext())
            {
                ((XCommanderHandler)it.next()).
                    handleCommand( type, identifier, result.toString());
            }
        }
    }

    public Class getCommand( final String commandName )
    {
        return (Class)m_xcommands.get( commandName );
    }

    /**
     * Use this method to add an XCommand. Make sure you watch security
     * concerns: all methods of an added class can be called.
     *
     * @throws IllegalArgumentException if the specified command is not
     * an instance of XCommand.
     */
    public Object addCommand ( final String commandName,
                               final Class command )
        throws IllegalArgumentException
    {
        if( XCommand.class.isAssignableFrom( command ) )
        {
            return m_xcommands.put( commandName, command );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
