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
 * ATcpListenFailedEvent objects will be passed to the 
 * Sink associated with an {@link AsyncServerSocket} when 
 * an attempt to create that server socket fails.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class ListenFailedEvent extends AbstractAsyncSocketErrorEvent
{
    /** The server socket on which the listening process failed. */
    private final AsyncServerSocket m_serverSocket;

    //---------------------- ListenFailedEvent constructors
    /**
     * Creates a listen failed event for the passed in server
     * socket object.
     * @since May 21, 2002
     * 
     * @param sock
     *  The server socket on which the listening failed
     * @param message
     *  The error message associated with the failure.
     */
    public ListenFailedEvent(AsyncServerSocket sock, String message)
    {
        super(message);
        m_serverSocket = sock;
    }

    //---------------------- ListenFailedEvent specific implementation
    /**
     * Returns the server socket for which the listen failed.
     * @since May 21, 2002
     * 
     * @return {@link AsyncSocketBase}
     *  the server socket for which the listen failed.
     */
    public AsyncSocketBase getSocket()
    {
        return m_serverSocket;
    }

}