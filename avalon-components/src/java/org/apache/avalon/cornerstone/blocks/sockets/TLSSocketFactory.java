/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.sockets;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;
import org.apache.avalon.cornerstone.services.sockets.SocketFactory;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.component.Component; // for backward compatibility
import org.apache.avalon.phoenix.BlockContext;

/**
 * Factory implementation for client TLS TCP sockets.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:charles@benett1.demon.co.uk">Charles Benett</a>
 * @author <a href="mailto:">Harish Prabandham</a>
 * @author <a href="mailto:">Costin Manolache</a>
 * @author <a href="mailto:">Craig McClanahan</a>
 * @author <a href="mailto:myfam@surfeu.fi">Andrei Ivanov</a>
 */
public class TLSSocketFactory
    extends AbstractLogEnabled
    implements SocketFactory, Contextualizable, Configurable, Initializable, Component
{
    private SSLSocketFactory m_factory;

    private File m_baseDirectory;

    private String m_keyStoreFile;
    private String m_keyStorePassword;
    private String m_keyPassword;
    private String m_keyStoreType;
    private String m_keyStoreProtocol;
    private String m_keyStoreAlgorithm;
    private boolean m_keyStoreAuthenticateClients;

    public void contextualize( final Context context )
    {
        final BlockContext blockContext = (BlockContext)context;
        m_baseDirectory = blockContext.getBaseDirectory();
    }

    /**
     * Configure factory. Sample config is
     *
     * <keystore>
     *  <file>conf/keystore</file> <!-- location of keystore relative to .sar base directory -->
     *  <password></password> <!-- Password for the Key Store file -->
     *  <key-password></key-password> <!-- Optional private Key Password -->
     *  <type>JKS</type> <!-- Type of the Key Store file -->
     *  <protocol>TLS</protocol> <!-- SSL protocol to use -->
     *  <algorithm>SunX509</algorithm> <!-- Certificate encoding algorithm -->
     *  <authenticate-client>false</authenticate-client> <!-- Require client authentication? -->
     * <keystore>
     *
     * @param configuration the Configuration
     * @exception ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration keyStore = configuration.getChild( "keystore" );
        m_keyStoreFile = keyStore.getChild( "file" ).getValue( "conf/keystore" );
        m_keyStorePassword = keyStore.getChild( "password" ).getValue();
        m_keyPassword = keyStore.getChild( "key-password" ).getValue( null );
        m_keyStoreType = keyStore.getChild( "type" ).getValue( "JKS" );
        m_keyStoreProtocol = keyStore.getChild( "protocol" ).getValue( "TLS" );
        m_keyStoreAlgorithm = keyStore.getChild( "algorithm" ).getValue( "SunX509" );
        m_keyStoreAuthenticateClients
            = keyStore.getChild( "authenticate-client" ).getValueAsBoolean( false );

    }

    public void initialize()
        throws Exception
    {
        final KeyStore keyStore = initKeyStore();
        initSSLFactory( keyStore );
    }

    private KeyStore initKeyStore()
        throws Exception
    {
        try
        {
            final KeyStore keyStore = KeyStore.getInstance( m_keyStoreType );
            File keyStoreFile = new File( m_baseDirectory, m_keyStoreFile );
            if( !keyStoreFile.exists() ) keyStoreFile = new File( m_baseDirectory + m_keyStoreFile );
            final FileInputStream input = new FileInputStream( keyStoreFile );

            keyStore.load( input, m_keyStorePassword.toCharArray() );
            getLogger().info( "Keystore loaded from: " + keyStoreFile );

            return keyStore;
        }
        catch( final Exception e )
        {
            getLogger().error( "Exception loading keystore from: " + m_keyStoreFile, e );
            throw e;
        }
    }

    private void initSSLFactory( final KeyStore keyStore )
        throws Exception
    {

        java.security.Security.addProvider( new sun.security.provider.Sun() );
        java.security.Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider() );

        // set up key manager to do server authentication
        final SSLContext sslContext = SSLContext.getInstance( m_keyStoreProtocol );
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance( m_keyStoreAlgorithm );

        if( null == m_keyPassword )
        {
            keyManagerFactory.init( keyStore, m_keyStorePassword.toCharArray() );
        }
        else
        {
            keyManagerFactory.init( keyStore, m_keyPassword.toCharArray() );
        }

        final TrustManagerFactory tmf = TrustManagerFactory.getInstance( m_keyStoreAlgorithm );
        tmf.init( keyStore );

        sslContext.init( keyManagerFactory.getKeyManagers(),
                         tmf.getTrustManagers(),
                         new java.security.SecureRandom() );

        // Create socket factory
        m_factory = sslContext.getSocketFactory();
    }

    private void initSocket( final Socket socket )
    {
        final SSLSocket sslSocket = (SSLSocket)socket;

        // Enable all available cipher suites when the socket is connected
        final String[] cipherSuites = sslSocket.getSupportedCipherSuites();
        sslSocket.setEnabledCipherSuites( cipherSuites );

        // Set client authentication if necessary
        sslSocket.setNeedClientAuth( m_keyStoreAuthenticateClients );
    }

    /**
     * Returns a socket layered over an existing socket connected to the named
     * host, at the given port. This constructor can be used when tunneling SSL
     * through a proxy or when negotiating the use of SSL over an existing socket.
     * The host and port refer to the logical peer destination. This socket is
     * configured using the socket options established for this factory.
     *
     * @param s - the existing socket
     * @param host - the server host
     * @param port - the server port
     * @param autoClose - close the underlying socket when this socket is closed
     *
     * @exception IOException - if the connection can't be established
     * @exception UnknownHostException - if the host is not known
     */
    public Socket createSocket( Socket s, String host, int port, boolean autoClose ) throws IOException
    {
        final Socket socket = m_factory.createSocket( s, host, port, autoClose );
        initSocket( socket );
        return socket;
    }

    /**
     * Returns a socket connected to a ServerSocket at the specified network
     * address and port. This socket is configured using the socket options
     * established for this factory.
     *
     * @param host - the server host
     * @param port - the server port
     *
     * @exception IOException - if the connection can't be established
     * @exception UnknownHostException - if the host is not known
     */
    public Socket createSocket( String host, int port ) throws IOException, UnknownHostException
    {
        InetAddress address = InetAddress.getByName( host );
        return this.createSocket( address, port );
    }

    /**
     * Returns a socket connected to a ServerSocket on the named host, at the
     * given port. The client address address is the specified host and port.
     * This socket is configured using the socket options established for this
     * factory.
     *
     * @param host - the server host
     * @param port - the server port
     * @param localAddress - the client host
     * @param localPort - the client port
     *
     * @exception IOException - if the connection can't be established
     * @exception UnknownHostException - if the host is not known
     */
    public Socket createSocket( String host, int port, InetAddress localAddress, int localPort )
        throws IOException, UnknownHostException
    {
        InetAddress address = InetAddress.getByName( host );
        return this.createSocket( address, port, localAddress, localPort );

    }

    /**
     * Create a socket and connect to remote address specified.
     *
     * @param address the remote address
     * @param port the remote port
     * @return the socket
     * @exception IOException if an error occurs
     */
    public Socket createSocket( InetAddress address, int port ) throws IOException
    {
        final Socket socket = m_factory.createSocket( address, port );
        initSocket( socket );
        return socket;
    }

    /**
     * Create a socket and connect to remote address specified
     * originating from specified local address.
     *
     * @param address the remote address
     * @param port the remote port
     * @param localAddress the local address
     * @param localPort the local port
     * @return the socket
     * @exception IOException if an error occurs
     */
    public Socket createSocket( InetAddress address, int port, InetAddress localAddress, int localPort ) throws IOException
    {
        final Socket socket = m_factory.createSocket( address, port, localAddress, localPort );
        initSocket( socket );
        return socket;
    }

}

