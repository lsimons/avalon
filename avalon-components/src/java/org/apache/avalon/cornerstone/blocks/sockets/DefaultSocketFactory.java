/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.avalon.cornerstone.services.sockets.SocketFactory;
import org.apache.avalon.framework.component.Component;

/**
 * The vanilla implementation of SocketFactory.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class DefaultSocketFactory
    implements SocketFactory, Component
{
    /**
     * Create a socket and connect to remote address specified.
     *
     * @param address the remote address
     * @param port the remote port
     * @return the socket
     * @exception IOException if an error occurs
     */
    public Socket createSocket( final InetAddress address, final int port )
        throws IOException
    {
        return new Socket( address, port );
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
    public Socket createSocket( final InetAddress address,
                                final int port,
                                final InetAddress localAddress,
                                final int localPort )
        throws IOException
    {
        return new Socket( address, port, localAddress, localPort );
    }
}
