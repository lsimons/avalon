/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.serversocket;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.security.cert.X509Certificate;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.cornerstone.services.SocketServer;
import org.apache.avalon.AbstractLoggable;

/**
 * This is an implementation example of a secure socket acceptor. A socket acceptor
 * waits on a defined (in its confs) socket for request. On a request it calls
 * the method parseRequest(Socket s) in the SocketHandler interface. The 
 * specific implementation of the SocketHandler is defined again in confs.
 * Definitivly this class listen on the specific port and then call the 
 * specific handler to parse the request on that socket. You must start an 
 * acceptor for each port or other "request generator" you want to listen to.
 *
 * This socket acceptor implements Transport Layer Security (TLS) using  the JSSE extension.
 * It currently supports the Sun SSL provider shipped with JSSE. Support for other
 * crypto providers may be added in due course.
 *
 * @author  Federico Barbieri <fede@apache.org>
 * @author  Charles Benett <charles@benett1.demon.co.uk>
 */
public class TLSServerSocketAcceptor 
    extends AbstractLoggable
    implements SocketServer.Listener, Configurable, Initializable, Disposable
{
    protected Configuration                  m_configuration;
    protected ServerSocket                   m_serverSocket;
    protected int                            m_port;
    protected SocketServer.SocketHandler     m_handler;
    protected KeyStore                       m_keyStore;
    protected String                         m_keyStoreFile;
    protected char[]                         m_passphrase;
    
    public void configure( final Configuration configuration ) 
        throws ConfigurationException
    {
        m_keyStoreFile = configuration.getChild("serverKeysFile").getValue();

        // Get passphrase for access to keys.
        // TBD: Replace with console interaction
        m_passphrase = "passphrase".toCharArray();
    }

    public void init() throws Exception 
    {
        try
        {
            m_keyStore = KeyStore.getInstance("JKS");
            m_keyStore.load( new FileInputStream( "../conf/" + m_keyStoreFile ), m_passphrase );
        } 
        catch( final Exception e ) 
        {
            getLogger().error("Exception loading keystore from: " + m_keyStoreFile, e);
        }
        
        getLogger().info("Keystore loaded from: " + m_keyStoreFile);
    }
    
    public void listen( final int port, 
                        final SocketServer.SocketHandler handler, 
                        final InetAddress bind ) 
    {
        m_handler = handler;
        m_port = port;

        try
        {
            // set up key manager to do server authentication
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            final KeyManagerFactory keyManagerFactory = 
                KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init( m_keyStore, m_passphrase );
            sslContext.init( keyManagerFactory.getKeyManagers(), null, null );
                
            // Create TLS socket
            final SSLServerSocketFactory sslServerSocketFactory = 
                sslContext.getServerSocketFactory();
            m_serverSocket = sslServerSocketFactory.createServerSocket( m_port, 50, bind );
                
        } 
        catch( final Exception e ) 
        {
            getLogger().error( "Cannot open TLSServerSocketAcceptor on port " + m_port, e );
            throw new 
                RuntimeException( "Cannot open TLSServerSocketAcceptor on port " + m_port );
        }
       
        getLogger().info( "TLSServerSocketAcceptor on port " + m_port + " ready" );
    }
    
    /**
     * This is the actual work of an acceptor.
     * In particular a ServerSocketAcceptor will accept() on the specified m_port.
     */
    public void run() 
    {
        getLogger().info( "TLSServerSocketAcceptor on port " + m_port + ": running" );
        try 
        {
            while( true ) 
            {
                final Socket socket = m_serverSocket.accept();
                getLogger().info( "TLSServerSocketAcceptor on port " + m_port + 
                             ": request arrived" );

                m_handler.parseRequest( socket );
            }
        } 
        catch( final Exception e ) 
        {
            getLogger().error( "Exception on port " + m_port, e );
        }
    }
    
    public void dispose() throws Exception 
    {
        try
        {
            m_serverSocket.close();
        } 
        catch( final Exception e ) 
        {
            getLogger().error( "Exception while destroing TLSServerSocketAcceptor on port " + 
                          m_port, e );
        }
                
        getLogger().info( "TLSServerSocketAcceptor on port " + m_port + " disposed" );
    }
}
