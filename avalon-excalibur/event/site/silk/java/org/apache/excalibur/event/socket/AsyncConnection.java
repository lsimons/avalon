/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket;

import java.net.InetAddress;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkClosedException;
import org.apache.excalibur.event.SinkException;

/**
 * Interface that represents an asynchronous connection.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncConnection
{
    void setCompletionQueue(Sink queue);
    
    Sink getCompletionQueue();
    
    /**
     * Close the connection and the underlying socket. 
     * A {@link SinkClosedEvent} will be posted on the 
     * given completionQueue when the close is complete.
     * @since May 21, 2002
     * 
     * @param queue
     *  The queue to post the completion event to.
     * @throws SinkClosedException
     *  If the sink is already closed
     */
    void close()//Sink queue) 
        throws SinkException, SinkClosedException;

    /**
     * Flush the socket. A {@link SinkFlushedEvent} will be 
     * posted on the given completionQueue when the close 
     * is complete.
     * @since May 21, 2002
     * 
     * @param queue
     *  The queue to post the completion event to.
     * @throws SinkClosedException
     *  If the sink is already closed
     */
    void flush()//Sink queue) 
        throws SinkException, SinkClosedException;

    /** 
     * Returns the address of the peer.
     * @since May 21, 2002
     * 
     * @return {@link InetAddress}
     *  the address of the peer.
     */
    InetAddress getAddress();

    /**
     * Returns the port of the peer.
     * @since May 21, 2002
     * 
     * @return int
     *  the port of the peer.
     */
    int getPort();

    /** 
     * Associate a Sink with this connection and allow data
     * to start flowing into it. When data is read, 
     * {@link IncomingPacket} objects will be pushed into 
     * the given Sink. If this sink is full, the connection 
     * will attempt to allow packets to queue up in the O/S
     * network stack (i.e. by not issuing further read calls 
     * on the socket). Until this method is called, no data 
     * will be read from the socket.
     * @since May 21, 2002)
     *
     * @param queue
     *  The queue onto which the {@link IncomingPacket}  
     *  events are pushed
     * @param readAttempts 
     *  The number of times the Socket layer will attempt to 
     *  push a new entry onto the given Sink while the Sink 
     *  is full. The queue entry will be dropped after this 
     *  many tries. The default value is <m_code>-1</m_code>, 
     *  which indicates that the Socket layer will attempt 
     *  to push the queue entry indefinitely.
     * @throws SinkClosedException
     *  If the sink is already closed
     * @throws IllegalStateException
     *  If read is called twice on this socket.
     */
    void read(/*Sink queue, */int readAttempts) 
        throws SinkException, SinkClosedException;

    /** 
     * Associate a Sink with this connection and allow data
     * to start flowing into it. When data is read, 
     * {@link IncomingPacket} objects will be pushed into 
     * the given Sink. If this sink is full, the connection 
     * will attempt to allow packets to queue up in the O/S
     * network stack (i.e. by not issuing further read calls 
     * on the socket). Until this method is called, no data 
     * will be read from the socket.
     * @since May 21, 2002
     * 
     * @param queue
     *  The queue onto which the {@link IncomingPacket}  
     *  events are pushed
     * @throws SinkClosedException
     *  If the sink is already closed
     * @throws IllegalStateException
     *  If read is called twice on this socket.
     */
    void read()//Sink queue) 
        throws SinkException, SinkClosedException;

    /**
     * Enqueue an outgoing packet to be written to the
     * underlying socket.
     * @since May 21, 2002
     * 
     * @param bufferElement
     *  An outgoing packet to be written to this socket.
     * @throws SinkException
     *  If the sink is already closed or the element array 
     *  contains bad elements.
     * @throws SinkClosedException
     *  If the sink is already closed
     * @throws IllegalArgumentException
     *  If the buffer element cannot be written
     */
    void write(Buffer bufferElement) 
        throws SinkException, SinkClosedException;

    /**
     * Enqueue a set of outgoing packets to be written to 
     * the underlying socket.
     * @since May 21, 2002
     * 
     * @param bufarr
     *  a set of outgoing packets to be written to this socket.
     * @throws SinkException
     *  If the sink is already closed or the element array 
     *  contains bad elements
     * @throws SinkClosedException
     *  If the sink is already closed
     * @throws IllegalArgumentException
     *  If any buffer element cannot be written
     */
    void write(Buffer[] bufarr)
        throws SinkException, SinkClosedException;
}
