/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.apps.demos.helloworldserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;

/**
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.apps.demos.helloworldserver.HelloWorldServer"
 * @phoenix:mx name="org.apache.avalon.apps.demos.helloworldserver.HelloWorldServerMBean"
 *
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @author  Federico Barbieri <scoobie@pop.systemy.it>
 * @version 1.0
 */
public final class HelloWorldServerImpl
    extends AbstractLogEnabled
    implements HelloWorldServer, HelloWorldServerMBean, Contextualizable,
    Serviceable, Configurable, Initializable, Disposable,
    ConnectionHandlerFactory
{
    private SocketManager m_socketManager;
    private ConnectionManager m_connectionManager;
    private BlockContext m_context;
    private String m_greeting = "Hello World";
    private InetAddress m_bindTo;
    private int m_port;
    private String m_connectionName = "HelloWorldListener";
    private ServerSocket m_serverSocket;

    public void setGreeting( final String greeting )
    {
        m_greeting = greeting;
    }

    public String getGreeting()
    {
        return m_greeting;
    }

    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getChild( "port" ).getValueAsInteger( 8000 );

        try
        {
            final String bindAddress = configuration.getChild( "bind" ).getValue();
            m_bindTo = InetAddress.getByName( bindAddress );
        }
        catch( final UnknownHostException unhe )
        {
            throw new ConfigurationException( "Malformed bind parameter", unhe );
        }
        //String test = configuration.getChild("test").getValue();
    }

    /**
     *
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.sockets.SocketManager"
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.connection.ConnectionManager"
     *
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        getLogger().info( "HelloWorldServer.compose()" );

        m_socketManager = (SocketManager)serviceManager.lookup( SocketManager.ROLE );
        m_connectionManager = (ConnectionManager)serviceManager.lookup( ConnectionManager.ROLE );
    }

    public void initialize()
        throws Exception
    {
        final ServerSocketFactory factory =
            m_socketManager.getServerSocketFactory( "plain" );
        m_serverSocket = factory.createServerSocket( m_port, 5, m_bindTo );

        m_connectionManager.connect( m_connectionName, m_serverSocket, this );

        // This is only to help newbies.....
        System.out.println( "HelloWorld server running with a greeting of '" + m_greeting + "'.  Point your browser to http://localhost:" + m_port + " to see its page" );
    }

    public void dispose()
    {
        try
        {
            m_connectionManager.disconnect( m_connectionName );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error while disconnecting.", e );
        }

        try
        {
            m_serverSocket.close();
        }
        catch( final IOException ioe )
        {
            getLogger().warn( "Error while closing server socket.", ioe );
        }
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
        final HelloWorldHandler handler =
            new HelloWorldHandler( m_greeting, m_context );
        setupLogger( handler );
        return handler;
    }

    /**
     * Release a previously created ConnectionHandler e.g. for spooling.
     */
    public void releaseConnectionHandler( ConnectionHandler connectionHandler )
    {
    }
}
