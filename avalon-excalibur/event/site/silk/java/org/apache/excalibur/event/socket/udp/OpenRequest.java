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

/**
 * Request to create a UDP socket session. Upon completion
 * a Udp connection pipe will be pushed to the completion 
 * sink which can be connected and disconnected from a remote
 * host.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class OpenRequest
{
    /** The originating datgram token */
    private final AsyncDatagramSocket m_udpSocket;
    
    /** The address to bind the socket to */
    private final InetAddress m_inetAddress;
    
    /** The port to bind the socket to */
    private final int m_port;
    
    /** The completion queue for the connection */
    private final Sink m_completionQueue;
    
    /** The write clog threshold for the socket */
    private final int m_writeClogThreshold;
    
    /** The maximum packet size for packets from the socket */
    private final int m_maxPacketSize;

    //------------------------- OpenRequest constructors
    /**
     * Constructor for a creation request that takes
     * the socket, destination ip address and port number.
     * @since May 21, 2002
     * 
     * @param udpSocket
     *  The socket for connection establishment
     * @param inetAddress
     *  The destination host address
     * @param port
     *  The port number of the destination host.
     */
    public OpenRequest(
        AsyncDatagramSocket udpSocket,
        InetAddress inetAddress,
        int port,
        Sink completionQueue,
        int writeClogThreshold,
        int maxPacketSize)
    {
        m_udpSocket = udpSocket;
        m_inetAddress = inetAddress;
        m_port = port;
        m_completionQueue = completionQueue;
        m_writeClogThreshold = writeClogThreshold;
        m_maxPacketSize = maxPacketSize;
    }

    //------------------------- OpenRequest specific implementation
    /**
     * Returns the socket for connection establishment
     * @since May 21, 2002
     * 
     * @return {@link AsyncDatagramSocket}
     *  the socket for connection establishment
     */
    public AsyncDatagramSocket getUdpSocket()
    {
        return m_udpSocket;
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
     * Returns the completion queue for the socket
     * connection that should be created.
     * @since May 23, 2002
     * 
     * @return {@link Sink}
     *  the completion queue for the socket connection 
     *  that should be created.
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

    /**
     * Returns the maximum packet size for the
     * connection that should be created
     * @since May 23, 2002
     * 
     * @return int
     *  the maximum packet size for the connection that 
     *  should be created
     */
    public int getMaxPacketSize()
    {
        return m_maxPacketSize;
    }

    /**
     * Returns the threshold for writes to a clogged
     * sink with this socket.
     * @since May 23, 2002
     * 
     * @return int
     *  the threshold for writes to a clogged sink with 
     *  this socket.
     */
    public int getWriteClogThreshold()
    {
        return m_writeClogThreshold;
    }

}