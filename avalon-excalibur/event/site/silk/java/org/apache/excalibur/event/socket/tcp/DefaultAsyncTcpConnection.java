/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.net.InetAddress;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkClosedException;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.Buffer;
import org.apache.excalibur.event.socket.CloseRequest;
import org.apache.excalibur.event.socket.FlushRequest;
import org.apache.excalibur.event.socket.ReadRequest;
import org.apache.excalibur.event.socket.WriteRequest;

/**
 * A DefaultAsyncTcpConnection represents an established connection on an 
 * asynchronous socket. It is used to send outgoing packets over the 
 * connection, and to initiate packet reads from the connection. When a 
 * packet arrives on this connection, a {@link IncomingPacket} object will 
 * be pushed to the Sink specified by the {@link #read(Sink, int)} call. 
 * The {@link IncomingPacket} will contain a reference to this 
 * AsyncTcpConnection. This object also allows the connection to be 
 * flushed or closed.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
final class DefaultAsyncTcpConnection extends AbstractAsyncTcpConnection
    implements AsyncTcpConnection
{
    /** The write sink. */
    private final Sink m_writeSink;
    
    /** The read sink. */
    private final Sink m_readSink;

    /** Indicates whether the connection is closed or not */
    private boolean m_closed;

    /** A flag indicating that the reader is started */
    private boolean m_readerStarted;
    
    private Sink m_completionQueue = null;

    //----------------------- DefaultAsyncTcpConnection constructors
    /**
     * @see AbstractAsyncTcpConnection#AbstractAsyncTcpConnection(TcpSocket, InetAddress, int)
     */
    public DefaultAsyncTcpConnection(
        Sink writeSink,
        Sink readSink,
        Sink completionQueue,
        AsyncSocket socket,
        InetAddress remoteAddress,
        int port)
    {
        super(socket, remoteAddress, port);
        m_writeSink = writeSink;
        m_readSink = readSink;
        m_closed = false;
        m_completionQueue = completionQueue;
    }
    
    /**
     * @see AbstractAsyncTcpConnection#AbstractAsyncTcpConnection(TcpSocket, InetAddress, int)
     */
    public DefaultAsyncTcpConnection(
        Sink writeSink,
        Sink readSink,
        Sink completionQueue,
        AsyncServerSocket socket, 
        InetAddress remoteAddress, 
        int port)
    {
        super(socket, remoteAddress, port);
        m_writeSink = writeSink;
        m_readSink = readSink;
        m_closed = false;
        m_completionQueue = completionQueue;
    }

    //----------------------- AsyncConnection implementation
    /**
     * @see AsyncConnection#getCompletionQueue()
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

    /**
     * @see AsyncConnection#setCompletionQueue(Sink)
     */
    public void setCompletionQueue(Sink completionQueue)
    {
        if(completionQueue != null)
        {
            m_completionQueue = completionQueue;
        }
    }

    /** 
     * @see AsyncConnection#read(Sink)
     */
    public void read(/*Sink queue*/) throws SinkException, SinkClosedException
    {
        if (m_closed)
        {
            throw new SinkClosedException("AsyncConnection closed");
        }
        if (m_readerStarted || m_completionQueue == null)
        {
            throw new IllegalArgumentException();
        }
        enqueueRead(new ReadRequest(this, getSockState(), /*queue*/ m_completionQueue, -1));
        m_readerStarted = true;
    }

    /** 
     * @see AsyncConnection#read(Sink, int)
     */
    public void read(/*Sink queue,*/ int attempts)
        throws SinkException, SinkClosedException
    {
        if (m_closed)
        {
            throw new SinkClosedException("AsyncConnection closed");
        }
        if (m_readerStarted || m_completionQueue == null)
        {
            throw new IllegalStateException();
        }
        enqueueRead(new ReadRequest(this, getSockState(), /*queue*/ m_completionQueue, attempts));
        m_readerStarted = true;
    }

    /** 
     * @see AsyncConnection#write(Buffer)
     */
    public void write(Buffer element)
        throws SinkException, SinkClosedException
    {
        if (m_closed)
        {
            throw new SinkClosedException("AsyncConnection closed");
        }
        if (element == null)
        {
            throw new IllegalArgumentException("Cannot write null");
        }

        enqueueWrite(new WriteRequest(this, getSockState(), element));
    }

    /** 
     * @see AsyncConnection#write(Buffer[])
     */
    public void write(Buffer[] elements)
        throws SinkException, SinkClosedException
    {
        if (m_closed)
        {
            throw new SinkClosedException("AsyncConnection closed");
        }

        for (int i = 0; i < elements.length; i++)
        {
            if (elements[i] == null)
            {
                throw new IllegalArgumentException("Cannot write null");
            }

            enqueueWrite(new WriteRequest(this, getSockState(), elements[i]));
        }
    }

    /** 
     * @see AsyncConnection#close(Sink)
     */
    public void close(/*Sink queue*/) throws SinkException, SinkClosedException
    {
        if (m_closed)
        {
            throw new SinkClosedException("AsyncConnection closed");
        }
        if (m_completionQueue == null)
        {
            throw new IllegalStateException();
        }
        m_closed = true;
        enqueueWrite(new CloseRequest(this, getSockState(), /*queue*/ m_completionQueue));
    }

    /** 
     * @see AsyncConnection#flush(Sink)
     */
    public void flush(/*Sink queue*/) throws SinkException, SinkClosedException
    {
        if (m_closed)
        {
            throw new SinkClosedException("AsyncConnection closed");
        }
        if (m_completionQueue == null)
        {
            throw new IllegalStateException();
        }
        enqueueWrite(new FlushRequest(this, getSockState(), /*queue*/ m_completionQueue));
    }

    //----------------------- DefaultAsyncTcpConnection specific implementation
    /**
     * Enqueues the request made by the connection into 
     * the correct event queue.
     * @since May 23, 2002
     * 
     * @param request
     *  The server sockets requests to enqueue.
     * @throws SinkException
     *  If the request could not be enqueued
     */
    protected void enqueueWrite(Object request) throws SinkException
    {
        m_writeSink.enqueue(request);
    }

    /**
     * Enqueues the request made by the connection into 
     * the correct event queue.
     * @since May 23, 2002
     * 
     * @param request
     *  The server sockets requests to enqueue.
     * @throws SinkException
     *  If the request could not be enqueued
     */
    protected void enqueueRead(Object request) throws SinkException
    {
        m_readSink.enqueue(request);
    }
}
