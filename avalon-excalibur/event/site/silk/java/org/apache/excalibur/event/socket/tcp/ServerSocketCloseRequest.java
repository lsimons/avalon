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

/**
 * Request to close a server socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class ServerSocketCloseRequest
{
    /** The server socket to be closed. */
    private final AsyncServerSocket m_serverSocket;

    /** Internal server socket state used to close the socket */
    private final ListenSocketState m_listenSocketState;

    //----------------------- ServerSocketCloseRequest constructors
    /**
     * Constructs a closing request based on the passed
     * in server socket object.
     * @since May 21, 2002
     * 
     * @param serverSocket
     *  The socket to close.
     * @param listenSocketState
     *  The internal state of the server socket.
     */
    public ServerSocketCloseRequest(
        AsyncServerSocket serverSocket,
        ListenSocketState listenSocketState)
    {
        m_serverSocket = serverSocket;
        m_listenSocketState = listenSocketState;
    }

    //----------------------- ServerSocketCloseRequest specific implementation
    /**
     * Returns the socket to be closed.
     * @since May 21, 2002
     * 
     * @return {@link AsyncServerSocket}
     *  The server socket to be closed.
     */
    public AsyncServerSocket getServerSocket()
    {
        return m_serverSocket;
    }

    /**
     * Returns the internal state of the server socket 
     * which is used to close the server socket
     * @since May 21, 2002
     * 
     * @return {@link ListenSocketState}
     *  The internal state of the server socket which is
     *  used to close the server socket
     */
    public ListenSocketState getListenSocketState()
    {
        return m_listenSocketState;
    }
}