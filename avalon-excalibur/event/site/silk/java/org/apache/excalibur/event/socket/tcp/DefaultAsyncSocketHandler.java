/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.AbstractAsyncSocketErrorEvent;
import org.apache.excalibur.event.socket.AbstractAsyncSocketHandlerBase;
import org.apache.excalibur.event.socket.ReadWriteSocketState;
import org.apache.excalibur.event.socket.SocketConstants;
import org.apache.excalibur.nbio.AsyncSelectableSocket;
import org.apache.excalibur.nbio.AsyncSelection;

/**
 * Represents an implementation of an asynchronous socket SEDA 
 * event handler.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultAsyncSocketHandler extends AbstractAsyncSocketHandlerBase
    implements AsyncSocketHandler, Configurable
{
    /** The size of the internal read buffer in bytes */
    private int m_readLength = SocketConstants.READ_BUFFER_SIZE;
    
    /** Indicates whether the reader should copy data into a new buffer */
    private boolean m_readBufferCopy = SocketConstants.READ_BUFFER_COPY;
    
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
        
        m_readLength = configuration.getAttributeAsInteger(
            "read-length", m_readLength);
        m_readBufferCopy = configuration.getAttributeAsBoolean(
            "read-buffer-copy", m_readBufferCopy);
        m_maxWritesAtOnce = configuration.getAttributeAsInteger(
            "max-writes", m_maxWritesAtOnce);
    }

    //-------------------------- AsyncSocketHandlerBase implementation
    /**
     * @see AsyncSocketHandlerBase#write(AsyncSelection[])
     */
    public void write(AsyncSelection[] keys)
    {
        final int length = 
            m_maxWritesAtOnce < 1 ? keys.length : m_maxWritesAtOnce;
            
        for(int i = 0, j = 0; i < keys.length; i++)
        {
            final AsyncSelection key = keys[i];
            final Object attachment = key.attachment();
            
            if (attachment instanceof ConnectSocketState)
            {
                final ConnectSocketState state = 
                    (ConnectSocketState) attachment;
                key.clear(); // not in SEDA m_code but should be ???
                handleConnectState(state);
            }
            else if(j < length)
            {
                final ReadWriteSocketState state = 
                    (ReadWriteSocketState) attachment;
                key.clear();
                handleSocketState(state);
            }
        }
    }
        
    //-------------------------- AsyncSocketHandler implementation
    /**
     * @see AsyncSocketHandler#connect(ConnectRequest)
     */
    public void connect(ConnectRequest connect)
    {
        try
        {
            // open a socket channel 
            final AsyncSelectableSocket channel = 
                m_connectionManager.createAsyncSelectableSocket();
            channel.open();

            final InetAddress address = connect.getInetAddress();
            final int port = connect.getPort();
            final AsyncSocket socket = connect.getClientSocket();
            channel.connect(address, port);

            final AsyncSelection key = m_writeSelector.register(channel);
            key.subscribeConnect(true);
            final ConnectSocketState connectState =
                new ConnectSocketState(connect, key, channel);

            key.attach(connectState);
        }
        catch (IOException e)
        {
            final String message =
                "Got error trying to connect: " + e.getMessage();
            
            if (getLogger().isErrorEnabled())
            {
                getLogger().error(message, e);
            }
            
            final ConnectFailedEvent event =
                new ConnectFailedEvent(connect.getClientSocket(), message);
            // Cannot connect 
            connect.getCompletionQueue().tryEnqueue(event);
        }
    }

    //-------------------------- AbstractAsyncSocketHandlerBase implementation
    /** 
     * @see AbstractAsyncSocketHandlerBase#createIncomingPacket(ReadWriteSocketState, int)
     */
    protected Object createIncomingPacket(
        ReadWriteSocketState socketState, final int length)
    {
        // Pushing up new IncomingPacket
        final IncomingPacket packet =
            new IncomingPacket(
                (AsyncTcpConnection)socketState.getConnection(),
                socketState.getReadByteBuffer(),
                length,
                m_readBufferCopy,
                socketState.getCurrentReadPacketIndex());
        
        // increment the current sequence index
        socketState.incrementReadPacketIndex();
        return packet;
    }

    
    //----------------------- DefaultAsyncSocketHandler specific implementation
    /**
     * Finishes off the connection process using the
     * internal connection state of the socket. Creates
     * a connection which is enqueued into the sockets
     * completion queue.
     * @since May 23, 2002
     * 
     * @param connect
     *  The internal connection state of the socket.
     */
    private void handleConnectState(ConnectSocketState connect)
    {
        final AsyncSelectableSocket channel = 
            (AsyncSelectableSocket)connect.getSocketChannel();

        final InetAddress address = channel.getInetAddress();
        final int port = channel.getPort();
        final AsyncSocket client = connect.getClientSocket();
        
        final Sink queue = connect.getCompletionQueue();
        final DefaultAsyncTcpConnection connection =
            new DefaultAsyncTcpConnection(
                m_writeSink, m_readSink, queue, client, address, port);

        // Now connect to the socket
        try
        {
            channel.finishConnect();
        }
        catch (IOException e)
        {
            if (getLogger().isErrorEnabled())
            {
                getLogger().error("Error connecting the socket.", e);
            }
        }

        // In case we get triggered for complete twice
        if (!connect.hasCompleted())
        {
            // complete the connection
            complete(connect, connection);
        }
    }

    /**
     * Completes the connection process by putting the
     * connection into the queue. 
     * @since Aug 26, 2002
     * 
     * @param connection
     *  The connection.
     * @param connect
     *  The state of the connect operation for the current
     *  socket channel
     */
    private void complete(
        ConnectSocketState connect, DefaultAsyncTcpConnection connection)
    {
        // the key token for a transactional enqueue
        final Sink completion = connect.getCompletionQueue();
        PreparedEnqueue key = null;
        try
        {
            final AsyncSelectableSocket channel = 
                (AsyncSelectableSocket)connect.getSocketChannel();
            try
            {
                // Do a split-phase enqueue: First prepare an empty 
                // connection, prepare it for enqueue, then finish the connect
                key = completion.prepareEnqueue(new Object[] { connection });
            }
            catch (SinkException e)
            {
                // Whoops - cannot enqueue it
                connect.incrementAttempts();
                if (connect.isAttemptsExceeded())
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn(
                            "Attempts exceeded. Could not enqueue.", e);
                    }
                    // Can't do it then just drop it
                    connect.getSelectionKey().close();
                    // and close the channel.
                    channel.close();
                }
                // Otherwise return and try again later
                return;
            }

            // Reserved entry, complete connection
            if (channel.getInetAddress() == null)
            {
                if (getLogger().isInfoEnabled())
                {
                    getLogger().info("Remote socket address is null.");
                }
                key.abort();
                // Return and try again later
                return;
            }

            final int clog = connect.getWriteClogThreshold();

            // create a new socket state for the established connection
            final ReadWriteSocketState sockState =
                new ReadWriteSocketState(connection, channel, clog, m_readLength);
            // Now attach the socket state with the previously
            // created server connection
            connection.setSockState(sockState);

            // Finally enqueue the connection into the completion queue
            key.commit();
            connect.setCompleted();
        }
        catch (IOException ioe)
        {
            if (getLogger().isErrorEnabled())
            {
                getLogger().error("IOException occurred. Closing.", ioe);
            }
                
            connect.getSelectionKey().close();

            final AsyncSocket clientSocket = connect.getClientSocket();
            final String message =
                "Got error trying to connect: " + ioe.getMessage();
            final AbstractAsyncSocketErrorEvent errorEvent =
                new ConnectFailedEvent(clientSocket, message);

            completion.tryEnqueue(errorEvent);

            if (key != null)
            {
                // abort the enqueue operation (test should not be necessary)
                key.abort();
            }
        }
    }
}