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
 * Request to initiate read events on a connection.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class ReadRequest
{
    /** The socket state of the connection */
    private final ReadWriteSocketState m_socketState;

    /** The connection that should be read from. */
    private final AsyncConnection m_connection;

    /** The completion queue for read packets. */
    private final Sink m_completionQueue;

    /** The number of attempts to read from a clogged read source. */
    private final int m_cloggedReadAttempts;

    //------------------------- ReadRequest constructors
    /**
     * Constructor that creates the request based on the 
     * connection, the completion queue and the number of 
     * attempts to read.
     * @since May 21, 2002
     * 
     * @param connection
     *  The connection to read from
     * @param socketState
     *  The internal socket state of the connection
     * @param completionQueue
     *  The completion queue to push completion events to.
     * @param readClogTries
     *  The number of attempts to read from a clogged source.
     */
    public ReadRequest(
        AsyncConnection connection,
        ReadWriteSocketState socketState,
        Sink completionQueue,
        int cloggedReadAttempts)
    {
        m_connection = connection;
        m_completionQueue = completionQueue;
        m_cloggedReadAttempts = cloggedReadAttempts;
        m_socketState = socketState;
    }

    //------------------------- ReadRequest specific implementation
    /**
     * Returns the completion queue associated with this 
     * particular request.
     * @since May 21, 2002
     * 
     * @return {@link Sink}
     *  The completion queue to push completion events to.
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

    /**
     * Returns the number of attempts to read from a 
     * clogged source.
     * @since May 21, 2002
     * 
     * @return int
     *  the number of attempts to read from a clogged source.
     */
    public int getCloggedReadAttempts()
    {
        return m_cloggedReadAttempts;
    }

    /**
     * Returns the connection to read from.
     * @since May 21, 2002
     * 
     * @return {@link AsyncConnection}
     *  The connection to read from
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
     *  used to read.
     */
    public ReadWriteSocketState getSocketState()
    {
        return m_socketState;
    }
}