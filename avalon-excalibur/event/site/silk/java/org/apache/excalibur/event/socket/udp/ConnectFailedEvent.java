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

package org.apache.excalibur.event.socket.udp;

import org.apache.excalibur.event.socket.AbstractAsyncSocketErrorEvent;
import org.apache.excalibur.event.socket.AsyncSocketBase;

/**
 * ConnectFailedEvent objects will be passed to 
 * the Sink associated with an {@link AsyncDatagramSocket}
 * when a connection attempt fails.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class ConnectFailedEvent extends AbstractAsyncSocketErrorEvent
{
    /** The client socket whose connection attempt failed. */
    private final AsyncDatagramSocket m_socket;

    //------------------------ ConnectFailedEvent constructors
    /**
     * Constructs a new connection failed event basedon on
     * the passed in socket and error message.
     * @since May 21, 2002
     * 
     * @param socket
     *  The client socket causing the failure
     * @param message
     *  The error message for the failure.
     */
    public ConnectFailedEvent(AsyncDatagramSocket socket, String message)
    {
        super(message);
        m_socket = socket;
    }

    //------------------------ ConnectFailedEvent specific implementation
    /**
     * Returns the socket that was responsible for the 
     * connection failure.
     * @since May 21, 2002
     * 
     * @return {@link AsyncSocketBase}
     *  the socket that was responsible for the failure.
     */
    public AsyncSocketBase getSocket()
    {
        return m_socket;
    }
}