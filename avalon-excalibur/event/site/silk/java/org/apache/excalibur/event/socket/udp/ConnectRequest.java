/* 
 * Copyright (c) 2001 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.udp;

import java.net.InetAddress;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.socket.ReadWriteSocketState;
import org.apache.excalibur.nbio.AsyncSelectableDatagramSocket;

/**
 * Request to create a UDP socket session.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class ConnectRequest
{
    /** The socket state of the connection */
    private final ReadWriteSocketState m_socketState;

    /** The udp connection which must be connected */
    private final AsyncUdpConnection m_connection;

    /** The address to connect to. */
    private final InetAddress m_inetAddress;

    /** The port number to connect to. */
    private final int m_port;

    /** The socket's completion queue. */
    private final Sink m_completionQueue;

    //------------------------- ConnectRequest constructors
    /**
     * Constructor for a creation request that takes
     * the socket, destination ip address and port number.
     * @since May 21, 2002
     * 
     * @param connection
     *  The connection for connection establishment
     * @param socketState
     *  The internal socket state of the connection
     * @param completionQueue
     *  The socket's completion queue.
     * @param inetAddress
     *  The destination host address
     * @param port
     *  The port number of the destination host.
     */
    public ConnectRequest(
        AsyncUdpConnection connection,
        ReadWriteSocketState socketState,
        Sink completionQueue,
        InetAddress inetAddress,
        int port)
    {
        m_connection = connection;
        m_inetAddress = inetAddress;
        m_port = port;
        m_socketState = socketState;
        m_completionQueue = completionQueue;
    }

    //------------------------- ConnectRequest specific implementation
    /**
     * Returns the connection for connection establishment
     * @since May 21, 2002
     * 
     * @return {@link AsyncUdpConnection}
     *  the connection for connection establishment
     */
    public AsyncUdpConnection getConnection()
    {
        return m_connection;
    }

    /**
     * Returns the port number of the destination host.
     * @since May 21, 2002
     * 
     * @return int
     *  the port number of the destination host.
     */
    public int getPort()
    {
        return m_port;
    }

    /**
     * Returns the destination host address.
     * @since May 21, 2002
     * 
     * @return {@link InetAddress}
     *  the destination host address.
     */
    public InetAddress getInetAddress()
    {
        return m_inetAddress;
    }

    /**
     * Returns the internal state of the socket 
     * which is used to read.
     * @since May 21, 2002
     * 
     * @return {@link ReadWriteSocketState}
     *  the internal state of the socket which is 
     *  used to flush.
     */
    public ReadWriteSocketState getSocketState()
    {
        return m_socketState;
    }

    /**
     * Returns the socket's completion queue.
     * @since May 23, 2002
     * 
     * @return {@link Sink}
     *  The socket's completion queue to enqueue
     *  success or failure events to.
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

}