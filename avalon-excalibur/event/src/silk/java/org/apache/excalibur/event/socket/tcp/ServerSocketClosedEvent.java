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

import org.apache.excalibur.event.socket.AbstractAsyncSocketErrorEvent;
import org.apache.excalibur.event.socket.AsyncSocketBase;

/**
 * ATcpServerSocketClosedEvent objects will be passed up 
 * to the Sink associated with the AsyncServerSocket when a 
 * server socket dies. This can happen if an attempt to 
 * accept an incoming connection fails for some reason, 
 * generally due to running out of file descriptors.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class ServerSocketClosedEvent extends AbstractAsyncSocketErrorEvent
{

    /** The server socket that is closed. */
    private final AsyncServerSocket m_serverSocket;

    //--------------------- ServerSocketClosedEvent constructors
    /**
     * Constructs an empty event that does not
     * carry an associated server socket.
     * @since May 21, 2002)
     */
    public ServerSocketClosedEvent()
    {
        this(null);
    }

    /**
     * Constructs an event based on the passed in server 
     * socket.
     * @since May 21, 2002
     * 
     * @param sock
     *  The server socket that has been closed.
     */
    public ServerSocketClosedEvent(AsyncServerSocket serverSocket)
    {
        super("AsyncServerSocket is dead");
        m_serverSocket = serverSocket;
    }

    //--------------------- ServerSocketClosedEvent specific implementation
    /**
     * Returns the socket that has been closed.
     * @since May 21, 2002
     * 
     * @return {@link AsyncSocketBase}
     *  The socket socket that has been closed.
     */
    public AsyncSocketBase getSocket()
    {
        return m_serverSocket;
    }
}