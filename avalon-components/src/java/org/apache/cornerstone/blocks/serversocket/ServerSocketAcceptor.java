/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.serversocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Disposable;
import org.apache.cornerstone.services.SocketServer;
import org.apache.log.Logger;

/**
 * This is an implementation example of a socket acceptor. A socket acceptor
 * waits on a defined (in its confs) socket for request. On a request it calls
 * the method parseRequest(Socket s) in the SocketHandler interface. The
 * specific implementation of the SocketHandler is defined again in confs.
 * Definitivly this class listen on the specific port and then call the
 * specific handler to parse the request on that socket. You must start an
 * acceptor for each port or other "request generator" you want to listen to.
 *
 * @author  Federico Barbieri <fede@apache.org>
 */
public class ServerSocketAcceptor 
    extends AbstractLoggable
    implements SocketServer.Listener, Disposable
{
    protected ServerSocket                m_serverSocket;
    protected int                         m_port;
    protected SocketServer.SocketHandler  m_handler;
    
    public void listen( final int port, 
                        final SocketServer.SocketHandler handler, 
                        final InetAddress bind ) 
    {
        m_handler = handler;
        m_port = port;

        try
        {
            m_serverSocket = new ServerSocket( m_port, 50, bind );
        } 
        catch( final IOException ioe )
        {
            final String message = "Cannot open ServerSocketAcceptor on port " + m_port;
            getLogger().error( message , ioe );
            throw new RuntimeException( message );
        }
        
        getLogger().info( "ServerSocketAcceptor on port " + m_port + " ready" );
    }
    

    /**
     * This is the actual work of an acceptor.
     * In particular a ServerSocketAcceptor will accept() on the specified port.
     */
    public void run() 
    {
        getLogger().info( "ServerSocketAcceptor on port " + m_port + ": running" );

        try 
        {
            while( true )
            {
                final Socket socket = m_serverSocket.accept();
                getLogger().info( "ServerSocketAcceptor on port " + m_port + 
                                  ": request arrived" );
                m_handler.parseRequest( socket );
            }
        }
        catch( final Exception e ) 
        {
            getLogger().error( "Exception on port " + m_port + ": ", e );
        }
    }
    
    public void dispose()
        throws Exception
    {
        try
        {
            m_serverSocket.close();
        } 
        catch( final Exception e )
        {
            getLogger().error( "Exception while destroing ServerSocketAcceptor on port " + 
                               m_port + ": ", e );
        }

        getLogger().info( "ServerSocketAcceptor on port " + m_port + " disposed" );
    }
}
