/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.udp;

import java.net.InetAddress;

import org.apache.excalibur.event.socket.AsyncConnection;

/**
 * Represents a udp specific connection between tow peers over 
 * a datagram socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncUdpConnection extends AsyncConnection
{
    /**
     * Returns the {@link AsyncDatagramSocket} from which this 
     * connection came. Returns <m_code>null</m_code> if this 
     * connection resulted from a ServerSocket.
     * @since May 21, 2002
     * 
     * @return {@link AsyncDatagramSocket}
     *  the socket from which this connection came
     */
    AsyncDatagramSocket getDatagramSocket();

    /**
     * Asynchronously connect this socket to the given port. 
     * All send requests enqueued after this given connect 
     * call will use the given address and port as the default 
     * address. A {@link ConnectEvent} will be pushed to 
     * the user when the connect has completed.
     * @since May 21, 2002
     * 
     * @param remoteAddress
     *  The ip address of the remote host to connect to.
     * @param port 
     *  The port on the remote machine to connect to.
     */
    void connect(InetAddress remoteAddress, int port);

    /**
     * Asynchronously disconnect this socket from the given 
     * port. A {@link DisconnectEvent} will be enqueued 
     * to the user when the disconnect has completed. If this 
     * socket is not connected then an {@link DisconnectEvent}
     * will be pushed to the user regardless.
     * @since May 21, 2002)
     */
    void disconnect();
}
