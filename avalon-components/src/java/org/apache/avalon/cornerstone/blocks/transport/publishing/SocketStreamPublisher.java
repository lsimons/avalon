/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.altrmi.server.impl.socket.AbstractPartialSocketStreamServer;

/**
 * @phoenix:block
 * @phoenix:service name="org.apache.excalibur.altrmi.server.AltrmiPublisher"
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @author Mike Miller.
 * @author Peter Royal.
 * @version $Revision: 1.8 $
 */
public class SocketStreamPublisher
    extends AbstractPublisher
    implements ConnectionHandlerFactory
{
    private SocketManager m_socketManager;
    private ConnectionManager m_connectionManager;
    private int m_port;
    private InetAddress m_bindTo;
    private String m_socketStreamServerClass;
    private boolean m_allAddresses = false;

    /**
     * @phoenix:configuration-schema type="relax-ng"
     */
    public final void configure( Configuration configuration ) throws ConfigurationException
    {

        super.configure( configuration );

        m_port = configuration.getChild( "port" ).getValueAsInteger();

        try
        {
            final String bindAddress = configuration.getChild( "bind" ).getValue();

            if( "*".equals( bindAddress ) )
            {
                m_allAddresses = true;
                m_bindTo = null;
            }
            else
            {
                m_allAddresses = false;
                m_bindTo = InetAddress.getByName( bindAddress );
            }
        }
        catch( final UnknownHostException unhe )
        {
            throw new ConfigurationException( "Malformed bind parameter", unhe );
        }

        m_socketStreamServerClass = configuration.getChild( "socketStreamServerClass" ).getValue();
    }

    /**
     * Service as per Serviceable interface
     * @param manager a service manager
     * @throws ServiceException if a problem during servicing
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.sockets.SocketManager"
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.connection.ConnectionManager"
     */
    public final void service( final ServiceManager manager )
        throws ServiceException
    {
        super.service( manager );

        m_socketManager = (SocketManager)manager.lookup( SocketManager.ROLE );
        m_connectionManager = (ConnectionManager)manager.lookup( ConnectionManager.ROLE );
    }

    protected ServerSocket makeServerSocket()
        throws IOException, Exception
    {

        final ServerSocketFactory factory = m_socketManager.getServerSocketFactory( "plain" );

        if( m_allAddresses )
        {
            return factory.createServerSocket( m_port, 5 );
        }
        else
        {
            return factory.createServerSocket( m_port, 5, m_bindTo );
        }
    }

    public ConnectionHandler createConnectionHandler() throws Exception
    {
        final PartialSocketStreamConnectionHandler handler =
            new PartialSocketStreamConnectionHandler(
                (AbstractPartialSocketStreamServer)getAbstractServer() );

        setupLogger( handler );

        return handler;
    }

    /**
     * Release a previously created ConnectionHandler.
     * e.g. for spooling.
     */
    public void releaseConnectionHandler( ConnectionHandler connectionHandler )
    {
    }

    public void initialize() throws Exception
    {
        setAbstractServer( (AbstractPartialSocketStreamServer)Class.forName( m_socketStreamServerClass ).newInstance() );

        setupLogger( getAbstractServer() );
        super.initialize();
        m_connectionManager.connect( "SocketStreamListener", makeServerSocket(), this );
    }
}
