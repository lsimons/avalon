/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.avalon.component.Component;

/**
 * The interface used to create client sockets.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface SocketFactory
    extends Component
{
    /**
     * Create a socket and connect to remote address specified.
     *
     * @param address the remote address
     * @param port the remote port
     * @return the socket
     * @exception IOException if an error occurs
     */
    Socket createSocket( InetAddress address, int port )
        throws IOException;

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
    Socket createSocket( InetAddress address, int port,
                         InetAddress localAddress, int localPort )
        throws IOException;
}
