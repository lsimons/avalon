/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.sockets;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.security.cert.X509Certificate;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.phoenix.BlockContext;

/**
 * Factory implementation for vanilla TCP sockets.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:charles@benett1.demon.co.uk">Charles Benett</a>
 * @author <a href="mailto:">Harish Prabandham</a>
 * @author <a href="mailto:">Costin Manolache</a>
 * @author <a href="mailto:">Craig McClanahan</a>
 */
public class TLSServerSocketFactory
    extends AbstractLoggable
    implements ServerSocketFactory, Component, Contextualizable, Configurable, Initializable
{
    protected SSLServerSocketFactory   m_factory;
    protected File                     m_baseDirectory;

    protected String                   m_keyStoreFile;
    protected String                   m_keyStorePassword;
    protected String                   m_keyStoreType;
    protected String                   m_keyStoreProtocol;
    protected String                   m_keyStoreAlgorithm;
    protected boolean                  m_keyStoreAuthenticateClients;

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
        m_keyStoreType = keyStore.getChild( "type" ).getValue( "JKS" );
        m_keyStoreProtocol = keyStore.getChild( "protocol" ).getValue( "TLS" );
        m_keyStoreAlgorithm = keyStore.getChild( "algorithm" ).getValue( "SunX509" );
        m_keyStoreAuthenticateClients =
            keyStore.getChild( "authenticate-client" ).getValueAsBoolean( false );
    }

    public void initialize()
        throws Exception
    {
        final KeyStore keyStore = initKeyStore();
        initSSLFactory( keyStore );
    }

    protected KeyStore initKeyStore()
        throws Exception
    {
        try
        {
            final KeyStore keyStore = KeyStore.getInstance( m_keyStoreType );
            final File keyStoreFile = new File( m_baseDirectory, m_keyStoreFile );
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

    protected void initSSLFactory( final KeyStore keyStore )
        throws Exception
    {
        /*
          java.security.Security.addProvider( new sun.security.provider.Sun() );
          java.security.Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider() );
          // */

        // set up key manager to do server authentication
        final SSLContext sslContext = SSLContext.getInstance( m_keyStoreProtocol );
        final KeyManagerFactory keyManagerFactory =
            KeyManagerFactory.getInstance( m_keyStoreAlgorithm );

        keyManagerFactory.init( keyStore, m_keyStorePassword.toCharArray() );

        sslContext.init( keyManagerFactory.getKeyManagers(),
                         null,
                         new java.security.SecureRandom() );

        // Create socket factory
        m_factory = sslContext.getServerSocketFactory();
    }

    /**
     * Creates a socket on specified port.
     *
     * @param port the port
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( final int port )
        throws IOException
    {
        final ServerSocket serverSocket = m_factory.createServerSocket( port );
        initServerSocket( serverSocket );
        return serverSocket;
    }

    /**
     * Creates a socket on specified port with a specified backLog.
     *
     * @param port the port
     * @param backLog the backLog
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( int port, int backLog )
        throws IOException
    {
        final ServerSocket serverSocket = m_factory.createServerSocket( port, backLog );
        initServerSocket( serverSocket );
        return serverSocket;
    }

    /**
     * Creates a socket on a particular network interface on specified port
     * with a specified backLog.
     *
     * @param port the port
     * @param backLog the backLog
     * @param bindAddress the network interface to bind to.
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( int port, int backLog, InetAddress bindAddress )
        throws IOException
    {
        final ServerSocket serverSocket =
            m_factory.createServerSocket( port, backLog, bindAddress );
        initServerSocket( serverSocket );
        return serverSocket;
    }

    protected void initServerSocket( final ServerSocket serverSocket )
    {
        final SSLServerSocket socket = (SSLServerSocket)serverSocket;

        // Enable all available cipher suites when the socket is connected
        final String[] cipherSuites = socket.getSupportedCipherSuites();
        socket.setEnabledCipherSuites( cipherSuites );

        // Set client authentication if necessary
        socket.setNeedClientAuth( m_keyStoreAuthenticateClients );
    }
}

