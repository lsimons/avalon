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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkClosedException;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.AbstractAsyncSocketHandlerBase;
import org.apache.excalibur.event.socket.Buffer;
import org.apache.excalibur.event.socket.CloseRequest;
import org.apache.excalibur.event.socket.FlushRequest;
import org.apache.excalibur.event.socket.ReadRequest;
import org.apache.excalibur.event.socket.ReadWriteSocketState;
import org.apache.excalibur.event.socket.SocketConstants;
import org.apache.excalibur.event.socket.WriteRequest;
import org.apache.excalibur.nbio.AsyncSelectableDatagramSocket;
import org.apache.excalibur.nbio.AsyncSelection;

/**
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class DefaultAsyncDatagramSocketHandler extends AbstractAsyncSocketHandlerBase
    implements AsyncDatagramSocketHandler, Configurable
{
    /** Maximum number of writes to process at once */
    private int m_maxWritesAtOnce = SocketConstants.MAX_WRITES_AT_ONCE;

    //-------------------------- Configurable implementation
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration)
        throws ConfigurationException
    {
        super.configure(configuration);
        
        m_maxWritesAtOnce = configuration.getAttributeAsInteger(
            "max-writes", m_maxWritesAtOnce);
    }
    
    //------------------------------ AsyncSocketHandlerBase implementation
    /**
     * @see AsyncSocketHandlerBase#write(AsyncSelection)
     */
    public void write(AsyncSelection[] keys)
    {
        final int length = 
            m_maxWritesAtOnce < 1 ? keys.length : m_maxWritesAtOnce;
            
        for(int i = 0; i < length; i++)
        {
            final AsyncSelection key = keys[i];
            final ReadWriteSocketState socketState = 
                (ReadWriteSocketState) key.attachment();
            key.clear();
            handleSocketState(socketState);
        }
    }

    //------------------------------ AsyncDatagramSocketHandler implementation
    /**
     * @see AsyncDatagramSocketHandler#connect(ConnectRequest)
     */
    public void connect(ConnectRequest connect)
    {
        try
        {
            final AsyncSelectableDatagramSocket channel = 
                (AsyncSelectableDatagramSocket)
                    connect.getSocketState().getSelectable();

            final InetAddress remote = connect.getInetAddress();
            final int port = connect.getPort();
            channel.connect(remote, port);

            final AsyncUdpConnection connection = connect.getConnection();
            final AsyncDatagramSocket socket = connection.getDatagramSocket();
            final ConnectEvent ev = new ConnectEvent(socket);
            connect.getCompletionQueue().tryEnqueue(ev);
        }
        catch (IOException e)
        {
            final String message =
                "Got error trying to connect: " + e.getMessage();
            final ConnectFailedEvent event =
                new ConnectFailedEvent(
                    connect.getConnection().getDatagramSocket(), message);
            // Cannot connect 
            connect.getCompletionQueue().tryEnqueue(event);
        }
    }

    /**
     * @see AsyncDatagramSocketHandler#open(OpenRequest)
     */
    public void open(OpenRequest create) throws IOException
    {
        // open a socket channel 
        final AsyncSelectableDatagramSocket channel = 
            m_connectionManager.createAsyncSelectableDatagramSocket();
        channel.open();
        //channel.configureBlocking(false);

        final InetAddress address = create.getInetAddress();
        final int port = create.getPort();
        channel.bind(address, port);

        final AsyncDatagramSocket socket = create.getUdpSocket();
        final Sink queue = create.getCompletionQueue();
        final DatagramConnection connection = new DatagramConnection(socket, queue);

        final ReadWriteSocketState socketState =
            new ReadWriteSocketState(
                connection,
                channel,
                create.getWriteClogThreshold(),
                create.getMaxPacketSize());

        connection.setSocketState(socketState);

        try
        {
            queue.enqueue(connection);
        }
        catch (SinkException e)
        {
            close(socketState, queue);
        }
    }

    /**
     * @see AsyncDatagramSocketHandler#disconnect(DisconnectRequest)
     */
    public void disconnect(DisconnectRequest disconnect)
    {
        final AsyncUdpConnection connection = disconnect.getConnection();
        final AsyncSelectableDatagramSocket channel = 
            (AsyncSelectableDatagramSocket)
                disconnect.getSocketState().getSelectable();
        channel.disconnect();

        final AsyncDatagramSocket socket = connection.getDatagramSocket();
        final DisconnectEvent event = new DisconnectEvent(socket);
        disconnect.getCompletionQueue().tryEnqueue(event);
    }

    //-------------------------- AbstractAsyncSocketHandlerBase implementation
    /** 
     * @see AbstractAsyncSocketHandlerBase#createIncomingPacket(ReadWriteSocketState, int)
     */
    protected Object createIncomingPacket(
        ReadWriteSocketState socketState, final int length)
    {
        final AsyncSelectableDatagramSocket channel =
            (AsyncSelectableDatagramSocket) socketState.getSelectable();
        // Pushing up new IncomingPacket
        final DatagramPacket datagramPacket =
            new DatagramPacket(
                socketState.getReadByteBuffer(),
                length,
                channel.getRemoteInetAddress(),
                channel.getRemotePort());

        final IncomingPacket packet =
            new IncomingPacket(
                (AsyncUdpConnection) socketState.getConnection(),
                datagramPacket,
                socketState.getCurrentReadPacketIndex());

        // increment the current sequence index
        socketState.incrementReadPacketIndex();
        return packet;
    }

    //------------------------------ 
    class DatagramConnection implements AsyncUdpConnection
    {
        private final AsyncDatagramSocket m_socket;

        /** Indicates whether the packet reader is started or not. */
        private boolean m_readerStarted = false;

        /** Indicates whether the socket is closed or not. */
        private boolean m_closed = false;

        /** Internal ReadWriteSocketState associated with this connection */
        private ReadWriteSocketState m_sockState;
        
        private Sink m_completionQueue = null;


        DatagramConnection(AsyncDatagramSocket socket, Sink completionQueue)
        {
            super();
            m_socket = socket;
            m_completionQueue = completionQueue;
        }

        //--------------------------- AsyncUdpConnection implementation
        /**
         * @see AsyncUdpConnection#getDatagramSocket()
         */
        public AsyncDatagramSocket getDatagramSocket()
        {
            return m_socket;
        }

        //--------------------------- AsyncConnection implementation
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
        public void setCompletionQueue(Sink queue)
        {
            if(queue != null)
            {
                m_completionQueue = queue;
            }
        }

        /**
         * @see AsyncConnection#write(Buffer)
         */
        public void write(Buffer packet) throws SinkException
        {
            if (m_closed)
            {
                throw new SinkClosedException("AsyncDatagramSocket closed");
            }
            if (packet == null)
            {
                throw new IllegalArgumentException("Write got null element");
            }
            
            requestWrite(new WriteRequest(this, getSockState(), packet));
        }

        /**
         * @see AsyncConnection#write(Buffer[])
         */
        public void write(Buffer[] packets) throws SinkException
        {
            if (m_closed)
            {
                throw new SinkClosedException("AsyncDatagramSocket closed");
            }
            for (int i = 0; i < packets.length; i++)
            {
                if (packets[i] == null)
                {
                    throw new IllegalArgumentException("Write got null element");
                }
                
                requestWrite(new WriteRequest(this, getSockState(), packets[i]));
            }
        }

        /**
         * @see AsyncConnection#read(Sink)
         */
        public void read(/* Sink queue */)
            throws SinkClosedException
        {
            if (m_closed)
            {
                throw new SinkClosedException("AsyncConnection closed");
            }
            if (m_readerStarted || m_completionQueue == null)
            {
                throw new IllegalStateException();
            }
            
            m_readerStarted = true;

            requestRead(
                new ReadRequest(
                    this,
                    getSockState(),
                    /* queue */ m_completionQueue,
                    -1));
        }

        /**
         * @see AsyncConnection#read(Sink, int)
         */
        public void read(/* Sink queue, */ int readClogTries) 
            throws SinkClosedException
        {
            if (m_closed)
            {
                throw new SinkClosedException("AsyncConnection closed");
            }
            if (m_readerStarted || m_completionQueue == null)
            {
                throw new IllegalStateException();
            }
            
            m_readerStarted = true;

            requestRead(
                new ReadRequest(
                    this,
                    getSockState(),
                    /* queue */ m_completionQueue,
                    readClogTries));
        }

        //--------------------------- AsyncConnection implementation
        /**
         * @see AsyncDatagramSocket#connect(InetAddress, int)
         */
        public void connect(InetAddress remote, int port)
        {
            requestWrite(new ConnectRequest(
                this, getSockState(), m_completionQueue, remote, port));
        }

        /**
         * @see AsyncDatagramSocket#disconnect()
         */
        public void disconnect()
        {
            requestWrite(new DisconnectRequest(
                this, getSockState(), m_completionQueue));
        }
        
        /**
         * @see AsyncConnection#close(Sink)
         */
        public void close(/* Sink queue */) throws SinkClosedException
        {
            if (m_closed)
            {
                throw new SinkClosedException("AsyncDatagramSocket already closed");
            }
            m_closed = true;
            requestWrite(new CloseRequest(this, getSockState(), /* queue */ m_completionQueue));
        }

        /**
         * @see AsyncConnection#flush(Sink)
         */
        public void flush(/* Sink queue */) throws SinkClosedException
        {
            if (m_closed)
            {
                throw new SinkClosedException("AsyncDatagramSocket closed");
            }
            if (m_completionQueue == null)
            {
                throw new IllegalStateException();
            }
            requestWrite(new FlushRequest(this, getSockState(), /* queue */ m_completionQueue));
        }

        /**
         * @see AsyncConnection#getAddress()
         */
        public InetAddress getAddress()
        {
            return getDatagramChannel().getRemoteInetAddress();
        }

        /**
         * @see AsyncConnection#getPort()
         */
        public int getPort()
        {
            return getDatagramChannel().getRemotePort();
        }

        //-------------------------- DefaultUdpSocket specific implementation
        public AsyncSelectableDatagramSocket getDatagramChannel()
        {
            return (AsyncSelectableDatagramSocket)getSockState().getSelectable();
        }
        
        /**
         * Returns the local {@link InetAddress} for this socket.
         * @since May 21, 2002)
         * 
         * @return {@link InetAddress}
         *  the local ip Address for this socket.
         */
        public InetAddress getLocalAddress()
        {
            return getDatagramChannel().getLocalInetAddress();
        }

        /**
         * Return the local port for this socket.
         * @since May 21, 2002)
         * 
         * @return int
         *  the local port for this socket.
         */
        public int getLocalPort()
        {
            return getDatagramChannel().getLocalPort();
        }

        /**
         * Returns the next sequence number for packets arriving 
         * on this socket. Returns <m_code>0</m_code> if this socket 
         * is not active. Note that this method may return 
         * an <b>inaccurate</b> sequence number since the call 
         * is not synchronized with new message arrivals that 
         * may increment the sequence number.
         * @since May 21, 2002)
         * 
         * @return long
         *  The next sequence number for packets arriving on this 
         *  socket. 
         */
        public long getSequenceNumber()
        {
            if (getSockState() == null)
            {
                return 0;
            }
            return getSockState().getCurrentReadPacketIndex();
        }

        /**
         * Allows to set the internal state of this socket.
         * @since May 21, 2002)
         * 
         * @param sockState 
         *  the internal state of this socket.
         */
        public void setSocketState(ReadWriteSocketState sockState)
        {
            m_sockState = sockState;
        }

        /**
         * Returns the internal state of the socket.
         * @since May 21, 2002)
         * 
         * @return {@link ReadWriteSocketState} 
         *  the internal state of this socket.
         */
        public ReadWriteSocketState getSockState()
        {
            return m_sockState;
        }

        /**
         * Enqueues the requests made by the server socket into 
         * the correct stages event queues.
         * @since May 23, 2002)
         * 
         * @param request
         *  The server sockets requests to enqueue.
         */
        protected void requestWrite(Object request) 
        {
            try
            {
                m_writeSink.enqueue(request);
            }
            catch (SinkException e)
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error(
                        "Sink exception when enqeueing "
                            + request.getClass().getName()
                            + " into write sink",
                        e);
                }
            }
        }

        /**
         * Enqueues the requests made by the server socket into 
         * the correct stages event queues.
         * @since May 23, 2002)
         * 
         * @param request
         *  The server sockets requests to enqueue.
         */
        protected void requestRead(Object request)
        {
            try
            {
                m_readSink.enqueue(request);
            }
            catch (SinkException e)
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error(
                        "Sink exception when enqeueing "
                            + request.getClass().getName()
                            + " into read sink",
                        e);
                }
            }
        }

    }

}