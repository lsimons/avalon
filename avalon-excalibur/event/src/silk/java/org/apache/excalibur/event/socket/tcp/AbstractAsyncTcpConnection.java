/* 
 * Copyright (c) 2000 by Matt Welsh and The Regents of the University of 
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

package org.apache.excalibur.event.socket.tcp;

import java.net.InetAddress;

import org.apache.excalibur.event.socket.ReadWriteSocketState;

/**
 * This is an abstract base class implementing the 
 * {@link AsyncConnection} interface. AbstractAsyncTcpConnection 
 * represents an established connection on an asynchronous 
 * socket. It is used to send outgoing packets over 
 * the connection, and to initiate packet reads from the 
 * connection.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class AbstractAsyncTcpConnection implements AsyncTcpConnection
{
    /** The host address that this connection is bound to. */
    private final InetAddress m_remoteAddress;

    /** The port that this connection is bound to. */
    private final int m_port;

    /** The client socket that this connection is based on. */
    private final AsyncSocket m_socket;
    
    /** The server socket that this connection is based on. */
    private final AsyncServerSocket m_serverSocket;

    /** 
     * The application may use this field to associate some 
     * application-specific state with this connection. The aSocket
     * layer will not read or modify this field in any way.
     */
    public Object m_userTag;

    /** The internal state of the connection */
    private ReadWriteSocketState m_sockState;

    //---------------------- AsyncTcpConnection constructors
    /**
     * Creates a connection represented by the ip address
     * and the port number and based on the server socket.
     * @since May 21, 2002
     * 
     * @param socket
     *  The server socket for the connection.
     * @param remoteAddress
     *  The remote address to connect to.
     * @param port
     *  The port that this connection is bound to.
     */
    protected AbstractAsyncTcpConnection(
        AsyncServerSocket socket,
        InetAddress remoteAddress,
        int port)
    {
        m_remoteAddress = remoteAddress;
        m_port = port;
        m_serverSocket = socket;
        m_socket = null;
    }
    
    /**
     * Creates a connection represented by the ip address
     * and the port number and based on the client socket.
     * @since May 21, 2002
     * 
     * @param socket
     *  The client socket for the connection.
     * @param remoteAddress
     *  The remote address to connect to.
     * @param port
     *  The port that this connection is bound to.
     */
    protected AbstractAsyncTcpConnection(
        AsyncSocket socket,
        InetAddress remoteAddress,
        int port)
    {
        m_remoteAddress = remoteAddress;
        m_port = port;
        m_socket = socket;
        m_serverSocket = null;
    }

    //----------------------- AsyncConnection implementation
    /**
     * @see AsyncConnection#getAddress()
     */
    public InetAddress getAddress()
    {
        return m_remoteAddress;
    }

    /**
     * @see AsyncConnection#getPort()
     */
    public int getPort()
    {
        return m_port;
    }

    /**
     * @see AsyncConnection#getSocket()
     */
    public AsyncSocket getSocket()
    {
        return m_socket;
    }
    
    /**
     * @see AsyncConnection#getSocket()
     */
    public AsyncServerSocket getServerSocket()
    {
        return m_serverSocket;
    }

    //-------------------------- AbstractAsyncTcpConnection specific implementation
    /**
     * Returns the next sequence number for packets arriving 
     * on this connection. Returns <m_code>0</m_code> if this 
     * connection is not active. Note that this method may 
     * return an <b>inaccurate</b> sequence number since the 
     * call is not synchronized with new message arrivals that 
     * may increment the sequence number.
     * @since May 21, 2002
     * 
     * @return long
     *  The next sequence number for packets arriving on this 
     *  connection.
     */
    public long getSequenceNumber()
    {
        if (getSockState() == null)
        {
            return 0;
        }
        return getSockState().getCurrentReadPacketIndex();
    }

    /**
     * Allows to set the internal state of the connection.
     * @since May 23, 2002
     * 
     * @param sockState
     *  the internal state of the connection.
     */
    public void setSockState(ReadWriteSocketState sockState)
    {
        m_sockState = sockState;
    }

    /**
     * Returns the internal state of the connection.
     * @since May 23, 2002
     * 
     * @return {@link ReadWriteSocketState}
     *  the internal state of the connection.
     */
    public ReadWriteSocketState getSockState()
    {
        return m_sockState;
    }
}