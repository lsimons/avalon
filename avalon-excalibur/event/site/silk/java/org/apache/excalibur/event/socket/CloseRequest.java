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

package org.apache.excalibur.event.socket;

import org.apache.excalibur.event.Sink;

/**
 * Request to close a connection.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class CloseRequest
{
    /** The socket state of the connection */
    private final ReadWriteSocketState m_socketState;

    /** The connection to be closed. */
    private final AsyncConnection m_connection;

    /** The queue for the completion events */
    private final Sink m_completionQueue;

    //------------------------ CloseRequest constructors
    /**
     * Creates a close request based on the passed in
     * connection and completion queue.
     * @since May 21, 2002
     * 
     * @param connection
     *  The connection to be closed.
     * @param socketState
     *  The internal socket state of the connection
     * @param completionQueue
     *  The queue for the completion events
     */
    public CloseRequest(
        AsyncConnection connection,
        ReadWriteSocketState socketState,
        Sink completionQueue)
    {
        m_connection = connection;
        m_completionQueue = completionQueue;
        m_socketState = socketState;
    }

    //------------------------ CloseRequest specific implementation
    /**
     * Returns the queue for the completion events.
     * @since May 21, 2002
     * 
     * @return {@link Sink}
     *  The queue for the completion events
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

    /**
     * Returns the connection to be closed.
     * @since May 21, 2002
     * 
     * @return {@link AsyncConnection}
     *  The connection to be closed.
     */
    public AsyncConnection getConnection()
    {
        return m_connection;
    }

    /**
     * Returns the internal state of the socket 
     * which is used to read.
     * @since May 21, 2002
     * 
     * @return {@link ReadWriteSocketState}
     *  the internal state of the socket which is 
     *  used to close.
     */
    public ReadWriteSocketState getSocketState()
    {
        return m_socketState;
    }
}