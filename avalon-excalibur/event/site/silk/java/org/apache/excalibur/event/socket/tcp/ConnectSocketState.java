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

import java.io.IOException;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.nbio.AsyncSelectable;
import org.apache.excalibur.nbio.AsyncSelection;

/**
 * Internal class used to represent state of a socket while an 
 * outgoing connection is pending. Keeps track of the number
 * of connection attempts performed and the socket channel state.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
final class ConnectSocketState
{
    /** An open socket channel */
    private final AsyncSelectable m_socketChannel;
    
    /** A selection key for connect operation events */
    private final AsyncSelection m_selectionKey;
    
    /** The tcp client socket representation */
    private final AsyncSocket m_clientSocket;

    /** The completion queue for completion events */
    private final Sink m_completionQueue;

    private final int m_connectClogTries;
    private final int m_writeClogThreshold;

    /** A counter for connection attempts */
    private int m_connectNumTries;
    
    /** Set when the connection operation has succeeded */
    private boolean m_completed = false;

    //------------------------- ConnectSocketState constructors
    /**
     * Constructs a connect state based on the 
     * connection request and the select source to 
     * write to.
     * @since May 22, 2002
     * 
     * @param connectionRequest
     *  The connection request that this state is 
     *  impersonating
     * @param writeSelectionSource
     *  The source to write IO Events to.
     * @throws IOException
     *  If an IOException ocurrs
     */
    public ConnectSocketState(
        ConnectRequest connectionRequest,
        AsyncSelection selectionKey,
        AsyncSelectable socketChannel)
        throws IOException
    {
        super();

        m_clientSocket = connectionRequest.getClientSocket();
        m_completionQueue = connectionRequest.getCompletionQueue();
        m_writeClogThreshold = connectionRequest.getWriteClogThreshold();
        m_connectClogTries = connectionRequest.getConnectClogTries();
        
        m_selectionKey = selectionKey;
        m_socketChannel = socketChannel;
        
        m_connectNumTries = 0;
        m_completed = false;
    }

    //------------------------- ConnectSocketState specific implementation
    /**
     * Returns a boolean indicating if the state 
     * represents a connected socket or not.
     * @since Aug 26, 2002
     * 
     * @return boolean 
     *  A boolean indicating if the state represents 
     *  a connected socket or not.
     */
    public boolean hasCompleted()
    {
        return m_completed;
    }

    /**
     * Allows to set the connection operation to be 
     * completed.
     * @since Aug 26, 2002
     */
    public void setCompleted()
    {
        m_completed = true;
    }

    /**
     * Returns the client socket representation.
     * @since Aug 26, 2002
     * 
     * @return ClientSocket
     *  The client socket representation.
     */
    public AsyncSocket getClientSocket()
    {
        return m_clientSocket;
    }

    /**
     * Returns the nonblocking socket channel.
     * @since Aug 26, 2002 
     * 
     * @return AsyncSelectable
     *  The nonblocking socket channel.
     */
    public AsyncSelectable getSocketChannel()
    {
        return m_socketChannel;
    }
    
    /**
     * Returns the sink in which to enqueue completion
     * events for this operation.
     * @since Aug 26, 2002
     * 
     * @return Sink
     *  The sink in which to enqueue completion events 
     *  for this operation.
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }
    
    public void incrementAttempts()
    {
        m_connectNumTries++;
    }
    
    public void resetAttempts()
    {
        m_connectNumTries = 0;
    }
    
    public boolean isAttemptsExceeded()
    {
        return m_connectNumTries > m_connectClogTries;
    }

    /**
     * Returns the writeClogThreshold.
     * @return int
     */
    public int getWriteClogThreshold()
    {
        return m_writeClogThreshold;
    }

    /**
     * Returns the selectionKey.
     * @return SelectionKey
     */
    public AsyncSelection getSelectionKey()
    {
        return m_selectionKey;
    }

}