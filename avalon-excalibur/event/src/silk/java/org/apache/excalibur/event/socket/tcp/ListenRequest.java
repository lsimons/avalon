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

import org.apache.excalibur.event.Sink;

/**
 * Request to listen on a TCP port.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class ListenRequest
{
    /** The server socket which should start listening */
    private final AsyncServerSocket m_serverSocket;

    /** The completion queue for the listening success */
    private final Sink m_completionQueue;

    /** The port on which to listen */
    private final int m_port;

    /** The threshold for writing to clogged sink. */
    private final int m_writeClogThreshold;

    //------------------------- ListenRequest constructors
    /**
     * Constructs a listen request based on the passed in
     * server socket, port number, completion queue and
     * write clog threshold.
     * @since May 21, 2002
     * 
     * @param serverSocket
     *  The server socket that must listen
     * @param port
     *  The port number on which to listen for connections
     * @param completionQueue
     *  The completion queue for listening success events
     * @param writeClogThreshold
     *  The write clog threshold for the clogged sink.
     */
    public ListenRequest(
        AsyncServerSocket serverSocket,
        int port,
        Sink completionQueue,
        int writeClogThreshold)
    {
        m_serverSocket = serverSocket;
        m_completionQueue = completionQueue;
        m_port = port;
        m_writeClogThreshold = writeClogThreshold;
    }

    //------------------------- ListenRequest specific implementation
    /**
     * Returns the write clog threshold for the socket.
     * @since May 21, 2002
     * 
     * @return int
     *  the write clog threshold for the socket.
     */
    public int getWriteClogThreshold()
    {
        return m_writeClogThreshold;
    }

    /**
     * Returns the server socket that should start listening
     * @since May 21, 2002
     * 
     * @return {@link AsyncServerSocket}
     *  the server socket that should start listening
     */
    public AsyncServerSocket getServerSocket()
    {
        return m_serverSocket;
    }

    /**
     * The port on which to listen for connections.
     * @since May 21, 2002
     * 
     * @return int
     *  The port number on which to listen for connections.
     */
    public int getPort()
    {
        return m_port;
    }

    /**
     * Returns the completion queue on which to enqueue 
     * completion events.
     * @since May 21, 2002
     * 
     * @return {@link Sink}
     *  The completion queue on which to enqueue completion
     *  events
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

}