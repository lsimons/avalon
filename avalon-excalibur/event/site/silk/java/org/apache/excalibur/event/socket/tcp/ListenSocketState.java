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
import org.apache.excalibur.nbio.AsyncSelectableServerSocket;
import org.apache.excalibur.nbio.AsyncSelection;

/**
 * Internal class used to represent a server socket 
 * listening on a given port.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
final class ListenSocketState
{   
    /** just for statistics */
    protected static int m_numConnections = 0;
    
    /** The server socket */
    private final AsyncServerSocket m_serverSocket;
    
    /** The port the server is listening on */
    private final int m_port;
    
    /** The completion queue for the listening operation */
    private final Sink m_completionQueue;
    
    /** The server socket channel attached with this state */
    private AsyncSelectableServerSocket m_serverSocketChannel;
    
    /** The selection key for listening events */
    private final AsyncSelection m_selectionKey;
    
    /** marks the operation as being completed */    
    private boolean m_closed;

    private final int m_writeClogThreshold;

    //------------------------ ListenSocketState constructors
    /**
     * Constructs a new internal listening state for the 
     * socket based on the passed in listen request and 
     * a listening select source queue.
     * @since May 22, 2002
     * 
     * @param listenRequest
     *  The request to listen for incoming connections.
     * @param listenSelectSource
     *  The OS listening queue from which to pull the 
     *  selected IO events.
     * @throws IOException 
     *  in the case an IOException occurrs during construction.
     */
    public ListenSocketState(
        ListenRequest listenRequest,
        AsyncSelection selectionKey,
        AsyncSelectableServerSocket serverSocketChannel)
    {
        m_port = listenRequest.getPort();
        m_completionQueue = listenRequest.getCompletionQueue();
        m_writeClogThreshold = listenRequest.getWriteClogThreshold();
        m_serverSocket = listenRequest.getServerSocket();
        m_selectionKey = selectionKey;
        m_serverSocketChannel = serverSocketChannel;
        m_closed = false;
     }

    //----------------------- ListenSocketState specific implementation
    /**
     * Sets the socket state to being closed.
     * @see #isClosed()
     * @since Aug 26, 2002
     */
    public void setClosed()
    {
        m_closed = true;
    }
    
    /** 
     * Returns whether the socket's state is closed.
     * @see #setClosed()
     * @since Aug 26, 2002
     * 
     * @return boolean
     *  A boolean indicating whether the socket's 
     *  state is closed.
     */
    public boolean isClosed()
    {
        return m_closed;
    }

    /**
     * Returns the local port on which the socket is
     * listening
     * @since May 22, 2002
     * 
     * @return int
     *  the local port on which the socket is listening
     */
    public int getLocalPort()
    {
        return m_serverSocketChannel.getLocalPort();
    }


    /**
     * Returns the current server socket representation.
     * @since May 22, 2002
     * 
     * @return {@link AsyncServerSocket}
     *  the current server socket representation.
     */
    public AsyncServerSocket getServerSocket()
    {
        return m_serverSocket;
    }

    /**
     * Returns the socket's threshold for writing to
     * clogged sinks
     * @since May 22, 2002
     * 
     * @return int
     *  the socket's threshold for writing to clogged sinks
     */
    public int getWriteClogThreshold()
    {
        return m_writeClogThreshold;
    }

    /**
     * Returns the {@link AsyncSelectableServerSocket} attached 
     * to this listening operation's state.
     * @since Aug 26, 2002
     * 
     * @return AsyncSelectableServerSocket
     *  The {@link AsyncSelectableServerSocket} attached to this 
     *  listening operation's state.
     */
    public AsyncSelectableServerSocket getServerSocketChannel()
    {
        return m_serverSocketChannel;
    }

    /**
     * Returns the {@link AsyncSelection} for listening 
     * accept operations and the attached channel.
     * @since Aug 26, 2002
     * 
     * @return AsyncSelection
     *  The {@link AsyncSelection} for listening accept
     *  operations and the attached channel.
     */
    public AsyncSelection getSelectionKey()
    {
        return m_selectionKey;
    }

    /**
     * Returns the completion Queue for this operation's
     * stage.
     * @since Aug 26, 2002
     * 
     * @return Sink
     *  the completion Queue for this operation's stage.
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }
}