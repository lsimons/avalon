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
 * Request to suspend accepting new connections on a 
 * server socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class SuspendAcceptRequest
{
    /** Server socket at which the accept should be suspended. */
    private final AsyncServerSocket m_serverSocket;

    /** Internal state of the server socket used to suspend accept. */
    private final ListenSocketState m_listenSocketState;

    //------------------------ SuspendAcceptRequest constructors
    /**
     * Constructs a request for accept suspension based
     * on the passed in server socket.
     * @since May 21, 2002
     * 
     * @param m_serverSocket
     *  The server socket which should supend accepting
     *  socket requests.
     * @param listenSocketState
     *  The internal state of the server socket.
     */
    public SuspendAcceptRequest(
        AsyncServerSocket servsock,
        ListenSocketState listenSocketState)
    {
        m_serverSocket = servsock;
        m_listenSocketState = listenSocketState;
    }

    //------------------------ SuspendAcceptRequest specific implementation
    /**
     * Returns the server socket that should suspend 
     * accepting socket connections.
     * @since May 21, 2002
     * 
     * @return {@link AsyncServerSocket}
     *  The server socket that should suspend accepting 
     *  socket connections.
     */
    public AsyncServerSocket getServerSocket()
    {
        return m_serverSocket;
    }

    /**
     * Returns the internal state of the server socket 
     * which is used to suspend the accept.
     * @since May 21, 2002
     * 
     * @return {@link ListenSocketState}
     *  The internal state of the server socket which is
     *  used to suspend the accept.
     */
    public ListenSocketState getListenSocketState()
    {
        return m_listenSocketState;
    }
}