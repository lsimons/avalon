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

import java.util.List;
import java.util.Vector;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.nbio.AsyncSelectable;
import org.apache.excalibur.nbio.AsyncSelection;

/**
 * Internal class used to represent state of an active 
 * socket connection.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public final class ReadWriteSocketState
{
    /** The underlying non blocking selectable socket. */
    private AsyncSelectable m_selectable;
    
    /** The logical representation of a connection, mother of this state */
    private final AsyncConnection m_connection;
    
    /** Indicates whether this connections internal state is closed */
    private boolean m_closed = false;

    //------------------------- Reading --------------------------//
    /** The read operations selection key */
    private AsyncSelection m_readSelectionKey;

    /** The completion sink for completion read event. */
    private Sink m_completionQueue;

    /** An element not dequeued from a clogged queue during read */
    private Object m_cloggedReadElement = null;

    /** Number of attempts to read data from a clogged queue */
    private int m_readClogTries;

    /** The current incoming data packet's index */
    private long m_currentReadPacketIndex = 1;

    /** The byte buffer object used for the read operation */    
    private byte[] m_readByteBuffer;

    //------------------------- Writing --------------------------//
    /** The write operations selection key */
    private AsyncSelection m_writeSelectionKey;

    /** Boolean indicating whether the write process was started */
    private boolean m_writeStarted = false;

    /** Number of attempts to write data out */
    private int m_cloggedReadAttempts = 0;

    /** Limit of allowed outstanding write attempts */
    private int m_writeClogThreshold;
    
    /** The number of currently outstanding writes */
    private int m_outstandingWrites = 0;

    /** The amount of writes that were empty */
    private int m_emptyWritesCount = 0;

    /** A linked list of sequential write requests */
    private final List m_writeRequestList = new Vector();

    /** The current write requests to be processed */
    private Object m_currentWriteRequest;
    
    /** The offset for the current write operation */
    private int m_currentWriteOffset;

    /** The target amount of written bytes the current write operation */
    private int m_currentWriteLengthTarget;

    /** The byte buffer object used for the write operation */    
    private byte[] m_writeByteBuffer;

    //-------------------------- ReadWriteSocketState constructors
    /**
     * Creates a new internal state object based on the 
     * connection and selectable socket.
     * @since May 21, 2002
     * 
     * @param connection
     *  The connection for the socket
     * @param writeClogThreshold
     *  The threshold for writing to a clogged sink.
     * @param selectable
     *  The underlying non blocking selectable socket.
     */
    public ReadWriteSocketState(
        AsyncConnection connection,
        AsyncSelectable selectable,
        int writeClogThreshold,
        int bufferLength)
    {
        m_connection = connection;
        m_selectable = selectable;
        m_writeClogThreshold = writeClogThreshold;

        m_readByteBuffer = new byte[bufferLength];
    }

    //----------------------------- ReadWriteSocketState specific implementation
    /**
     * Returns the Tcp connection object that this state
     * is representing.
     * @since Aug 27, 2002
     * 
     * @return AsyncTcpConnection
     *  the Tcp connection object that this state is
     *  representing
     */
    public AsyncConnection getConnection()
    {
        return m_connection;
    }

    /**
     * Returns the {@link AsyncSelectableSocket} into which the
     * socket data is added.
     * @since Aug 27, 2002
     * 
     * @return AsyncSelectableSocket
     *  the {@link AsyncSelectableSocket} into which the
     *  socket data is added.
     */
    public AsyncSelectable getSelectable()
    {
        return m_selectable;
    }

    /**
     * Allocates a write buffer with the specified input
     * information. Also sets the current offset and target
     * length information.
     * @see #getCurrentWriteLengthTarget()
     * @see #getCurrentWriteOffset()
     * @since Aug 27, 2002
     * 
     * @param data
     *  The byte array making up the internal buffer
     * @param offset
     *  The write buffers start offset 
     * @param length
     *  The length of the write buffer starting from the
     *  offset.
     */
    public void allocateWriteBuffer(byte[] data, int offset, int length)
    {
        m_currentWriteOffset = offset;
        m_currentWriteLengthTarget = length + m_currentWriteOffset;

        m_writeByteBuffer = data;
    }
    
    /**
     * Increments the number of read attempts from
     * a clogged reader sink.
     * @since Aug 27, 2002
     */
    public void incrementCloggedReadAttempts()
    {
        m_cloggedReadAttempts++;
    }

    /**
     * Increments the number of outstanding writes to
     * the writer sink.
     * @since Aug 27, 2002
     */
    public void incrementCloggedWriteAttempts()
    {
        m_outstandingWrites++;
    }

    /**
     * Resets the number of read attempts from
     * a clogged reader sink to <m_code>0</m_code>.
     * @since Aug 27, 2002
     */
    public void resetCloggedReadAttempts()
    {
        m_cloggedReadElement = null;
        m_cloggedReadAttempts = 0;
    }

    /**
     * Returns a boolean indicating whether the number 
     * of attempts to read from a clogged sink exceeds
     * the configured limit.
     * @since Aug 27, 2002
     * 
     * @return boolean
     *  Boolean indicating whether the number of attempts 
     *  to read from a clogged sink exceeds the limit.
     */
    public boolean hasCloggedReadAttemptsExceeded()
    {
        return m_readClogTries != -1 && m_cloggedReadAttempts >= m_readClogTries;
    }

    /**
     * Returns a boolean indicating whether the number 
     * of outstanding writes exceeds the configured 
     * threshold.
     * @since Aug 27, 2002
     * 
     * @return boolean
     *  Boolean indicating whether the number of outstanding 
     *  writes exceeds the configured threshold.
     */
    public boolean hasCloggedWriteAttemptsExceeded()
    {
        return m_writeClogThreshold != -1
            && m_outstandingWrites > m_writeClogThreshold;
    }

    /**
     * Adds a write request object to the inner list
     * of sequential write operations.
     * @since Aug 27, 2002
     * 
     * @param writeRequest
     *  a write request object to be added to the inner 
     *  list of sequential write operations
     */
    public void pushWriteRequest(Object writeRequest)
    {
        m_writeRequestList.add(writeRequest);
    }

    /**
     * Removes a write request object from the inner list
     * of sequential write operations.
     * @since Aug 27, 2002
     * 
     * @return Object
     *  A write request object from the top of the
     *  inner list of sequential write operations.
     */
    public Object popWriteRequest()
    {
        if(!m_writeRequestList.isEmpty())
        {
            return m_writeRequestList.remove(0);
        }
        return null;
    }

    /**
     * Returns a write request object from the top of the
     * inner list of sequential write operations.
     * @since Aug 27, 2002
     * 
     * @return WriteRequest
     *  A write request object from the top of the
     *  inner list of sequential write operations.
     */
    public Object peekWriteRequest()
    {
        if(!m_writeRequestList.isEmpty())
        {
            return m_writeRequestList.get(0);
        }
        return null;
    }
    
    /**
     * Returns the current write request to be processed.
     * @since Aug 27, 2002
     * 
     * @return WriteRequest
     *  The current write request for processing
     */
    public Object getCurrentWriteRequest()
    {
        return m_currentWriteRequest;
    }

    /**
     * Resets the current writing requests to be 
     * <m_code>null</m_code> and decrements the number of
     * outstanding write operations.
     * @since Aug 27, 2002
     */
    public void setWriteRequestCompleted()
    {
        m_currentWriteRequest = null;
        m_outstandingWrites--;
    }

    /**
     * Returns a boolean indicating whether the connection
     * was closed.
     * @since Aug 27, 2002
     * 
     * @return boolean
     *  a boolean value indicating whether the connection
     *  was closed.
     */
    public boolean isClosed()
    {
        return m_closed;
    }

    /**
     * Returns the number of empty write operations, meaning 
     * operations in which not a single byte were written.
     * @since Aug 27, 2002
     * 
     * @return int
     *  The number of empty write operations
     */
    public int getEmptyWritesCount()
    {
        return m_emptyWritesCount;
    }

    /**
     * Increases the number of empty write operations by
     * <m_code>1</m_code>.
     * @since Aug 27, 2002
     */
    public void incrementEmptyWritesCount()
    {
        m_emptyWritesCount++;
    }

    /**
     * Resets the number of empty write operations to be
     * <m_code>0</m_code>.
     * @since Aug 27, 2002
     */
    public void resetEmptyWritesCount()
    {
        m_emptyWritesCount = 0;
    }

    /**
     * Returns the current data packet's index number.
     * @since Aug 27, 2002
     * 
     * @return long
     *  the current data packet's index number.
     */
    public long getCurrentReadPacketIndex()
    {
        return m_currentReadPacketIndex;
    }

    /**
     * Increments the current data packet's index number
     * by <m_code>1</m_code>. Guarantees that the index is
     * always greater than 0.
     * @since Aug 27, 2002
     */
    public void incrementReadPacketIndex()
    {
        m_currentReadPacketIndex++;

        if (m_currentReadPacketIndex == 0)
        {
            m_currentReadPacketIndex = 1;
        }
    }

    /**
     * Advances the current offset for the write buffer
     * the <m_code>length</m_code> amount of positions.
     * @since Aug 27, 2002
     * 
     * @param length
     *  The amount of positions to add to the current
     *  write buffer's offset.
     * @return boolean
     *  <m_code>true </m_code> if the buffer has been fully
     *  written out, otherwise <m_code>false</m_code>.
     */
    public boolean advanceCurrentWriteOffset(int length)
    {
        m_currentWriteOffset += length;
        return m_currentWriteOffset == m_currentWriteLengthTarget;
    }

    /**
     * Returns a boolean value indicating wether there are 
     * sill writes outstanding.
     * @since Aug 27, 2002
     * 
     * @return boolean
     *  a boolean value indicating wether there are sill 
     *  writes outstanding.
     */
    public boolean isWriteOutStanding()
    {
        return m_outstandingWrites > 0;
    }

    /**
     * Attaches the read completion queue with the 
     * socket state.
     * @since Aug 27, 2002
     * 
     * @param readCompletionQueue 
     *  The read completion queue to attach
     */
    public void setCompletionQueue(Sink readCompletionQueue)
    {
        m_completionQueue = readCompletionQueue;
    }

    /**
     * Allows to set the read selection key for the
     * read operation on the select queue.
     * @since Aug 27, 2002
     * 
     * @param readSelectionKey
     *  The read selection key for the read operation 
     *  on the select queue.
     */
    public void setReadSelectionKey(AsyncSelection readSelectionKey)
    {
        m_readSelectionKey = readSelectionKey;
    }
//
//    /**
//     * Returns the {@link AsyncSelectableSocket} into which the
//     * socket data is added.
//     * @since Aug 27, 2002
//     * 
//     * @return AsyncSelectableSocket
//     *  the {@link AsyncSelectableSocket} into which the
//     *  socket data is added.
//     */
//    public AsyncSelectableSocket getSocketChannel()
//    {
//        return m_socketChannel;
//    }

    /**
     * Allows to set the Limit on read operations from
     * a clogged sink.
     * @since Aug 27, 2002
     * 
     * @param readClogTries
     *  The Limit on read operations from a clogged sink.
     */
    public void setReadClogTries(int readClogTries)
    {
        m_readClogTries = readClogTries;
    }

    /**
     * Returns the queue element that could not be 
     * enqueued because the queue was clogged.
     * @since Aug 27, 2002 
     * 
     * @return Object
     *  The queue element that could not be enqueued
     *  because the queue was clogged.
     */
    public Object getCloggedReadElement()
    {
        return m_cloggedReadElement;
    }

    /**
     * Allows to set the queue element that could not be 
     * enqueued because the queue was clogged.
     * @since Aug 27, 2002 
     * 
     * @param cloggedQueueElement
     *  The queue element that could not be enqueued
     *  because the queue was clogged.
     */
    public void setCloggedReadElement(Object cloggedQueueElement)
    {
        m_cloggedReadElement = cloggedQueueElement;
    }

    /**
     * Returns the read completion queue for the read 
     * operations on the socket state.
     * @since Aug 27, 2002
     * 
     * @return Sink 
     *  The read completion queue for the read 
     *  operations on the socket state.
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

    /**
     * Returns the allocated byte array buffer for all
     * read operations with this socket. The byte buffer
     * is always overwritten for each read operation.
     * 
     * @return byte[]
     *  the allocated byte array buffer for all read 
     *  operations with this socket.
     */
    public byte[] getReadByteBuffer()
    {
        return m_readByteBuffer;
    }

    /**
     * Returns the {@link AsyncSelection} object which 
     * identifies write operations on the select queue
     * for this state's socket.
     * @since Aug 27, 2002
     * 
     * @return AsyncSelection
     *  The object that identifies write operations on 
     *  the select queue
     */
    public AsyncSelection getWriteSelectionKey()
    {
        return m_writeSelectionKey;
    }

    /**
     * Allows to set the {@link AsyncSelection} object which 
     * identifies write operations on the select queue
     * for this state's socket.
     * @since Aug 27, 2002
     * 
     * @param writeSelectionKey
     *  The object that identifies write operations on 
     *  the select queue
     */
    public void setWriteSelectionKey(AsyncSelection writeSelectionKey)
    {
        m_writeSelectionKey = writeSelectionKey;
    }

    /**
     * Returns whether the write operation has already
     * kicked in.
     * @see #setWriteStarted()
     * @since Aug 27, 2002
     * 
     * @return boolean
     *  A boolean value indicating whether the write 
     *  operation has already kicked in
     */
    public boolean isWriteStarted()
    {
        return m_writeStarted;
    }

    /**
     * Sets the state of the socket to be in writing mode.
     * @see #isWriteStarted()
     * @since Aug 27, 2002
     */
    public void setWriteStarted()
    {
        m_writeStarted = true;
    }

    /**
     * Sets the state of the socket to be closed and clears
     * the list of outstanding write requests.
     * @see #isClosed()
     * @since Aug 27, 2002
     */
    public void setClosed()
    {
        m_closed = true;
        m_writeRequestList.clear();
    }

    /**
     * Allows to set the current write request to be 
     * processed.
     * @since Aug 27, 2002
     * 
     * @param currentWriteRequest 
     *  The current Write Request to be processed.
     */
    public void setCurrentWriteRequest(Object currentWriteRequest)
    {
        m_currentWriteRequest = currentWriteRequest;
    }

    /**
     * Returns the current target length for write 
     * operations on the write buffer.
     * @since Aug 27, 2002
     * 
     * @return int
     *  The current target length for write operations 
     *  on the write buffer.
     */
    public int getCurrentWriteLengthTarget()
    {
        return m_currentWriteLengthTarget;
    }

    /**
     * Returns the current offset for write operations 
     * on the write buffer.
     * @since Aug 27, 2002
     * 
     * @return int
     *  The current offset for write operations on the 
     *  write buffer.
     */
    public int getCurrentWriteOffset()
    {
        return m_currentWriteOffset;
    }

    /**
     * Returns the byte array buffer for the write
     * operation.
     * @since Aug 27, 2002
     * 
     * @return byte[]
     *  A byte array buffer for the write operation.
     */
    public byte[] getWriteByteBuffer()
    {
        return m_writeByteBuffer;
    }

    /**
     * Returns the {@link AsyncSelection} representing the 
     * registration to receive reading events.
     * @since Aug 27, 2002
     * 
     * @return SelectionKey
     *  the {@link AsyncSelection} registered to receive 
     *  reading events.
     */
    public AsyncSelection getReadSelectionKey()
    {
        return m_readSelectionKey;
    }
}