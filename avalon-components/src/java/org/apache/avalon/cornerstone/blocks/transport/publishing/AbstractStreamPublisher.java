
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

import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * Class AbstractStreamPublisher
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @author Mike Miller.
 * @version $Revision: 1.4 $
 */
public abstract class AbstractStreamPublisher extends AbstractPublisher
        implements ConnectionHandlerFactory
{

    protected SocketManager m_socketManager;
    protected ConnectionManager m_connectionManager;
    private int m_port;
    private InetAddress m_bindTo;
    boolean m_allAddresses = false;

    /**
     * Pass the <code>Configuration</code> to the <code>Configurable</code>
     * class. This method must always be called after the constructor
     * and before any other method.
     *
     * @param configuration the class configurations.
     */
    public final void configure(Configuration configuration) throws ConfigurationException
    {

        super.configure(configuration);

        m_port = configuration.getChild("port").getValueAsInteger();

        try
        {
            final String bindAddress = configuration.getChild("bind").getValue();

            if ("*".equals(bindAddress))
            {
                m_allAddresses = true;
                m_bindTo = null;
            }
            else
            {
                m_allAddresses = false;
                m_bindTo = InetAddress.getByName(bindAddress);
            }
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
    public final void compose(ComponentManager manager) throws ComponentException
    {

        super.compose(manager);

        m_socketManager = (SocketManager) manager.lookup(SocketManager.ROLE);
        m_connectionManager = (ConnectionManager) manager.lookup(ConnectionManager.ROLE);
    }

    protected ServerSocket makeServerSocket() throws ComponentException, IOException
    {

        final ServerSocketFactory factory = m_socketManager.getServerSocketFactory("plain");

        if (m_allAddresses)
        {
            return factory.createServerSocket(m_port, 5);
        }
        else
        {
            return factory.createServerSocket(m_port, 5, m_bindTo);
        }
    }
}
