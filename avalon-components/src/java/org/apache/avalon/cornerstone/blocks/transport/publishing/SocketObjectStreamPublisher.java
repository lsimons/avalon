
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;



import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.commons.altrmi.server.impl.socket.PartialSocketObjectStreamServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.ServerSocket;


/**
 * Class SocketObjectStreamPublisher
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.4 $
 */
public class SocketObjectStreamPublisher extends AbstractPublisher
        implements ConnectionHandlerFactory
{

    private SocketManager m_socketManager;
    private ConnectionManager m_connectionManager;
    private int m_port;
    private InetAddress m_bindTo;

    /**
     * Pass the <code>Configuration</code> to the <code>Configurable</code>
     * class. This method must always be called after the constructor
     * and before any other method.
     *
     * @param configuration the class configurations.
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {

        super.configure(configuration);

        m_port = configuration.getChild("port").getValueAsInteger();

        try
        {
            final String bindAddress = configuration.getChild("bind").getValue();

            m_bindTo = InetAddress.getByName(bindAddress);
        }
        catch (final UnknownHostException unhe)
        {
            throw new ConfigurationException("Malformed bind parameter", unhe);
        }
    }

    /**
     * Method compose
     *
     *
     * @param manager
     *
     * @throws ComponentException
     *
     */
    public void compose(ComponentManager manager) throws ComponentException
    {

        super.compose(manager);

        m_socketManager = (SocketManager) manager.lookup(SocketManager.ROLE);
        m_connectionManager = (ConnectionManager) manager.lookup(ConnectionManager.ROLE);
    }

    /**
     * Construct an appropriate ConnectionHandler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    public ConnectionHandler createConnectionHandler() throws Exception
    {

        final SocketObjectStreamConnectionHandler handler =
            new SocketObjectStreamConnectionHandler(
                (PartialSocketObjectStreamServer) m_AltrmiServer);

        setupLogger(handler);

        return handler;
    }

    /**
     * Release a previously created ConnectionHandler.
     * e.g. for spooling.
     */
    public void releaseConnectionHandler(ConnectionHandler connectionHandler)
    {
    }

    /**
     * Initialialize the component. Initialization includes
     * allocating any resources required throughout the
     * components lifecycle.
     *
     * @exception Exception if an error occurs
     */
    public void initialize() throws Exception
    {

        m_AltrmiServer = new PartialSocketObjectStreamServer();

        super.initialize();

        final ServerSocketFactory factory = m_socketManager.getServerSocketFactory("plain");
        final ServerSocket serverSocket = factory.createServerSocket(m_port, 5, m_bindTo);

        m_connectionManager.connect("SocketObjectStreamListener", serverSocket, this);
    }
}
