/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.udp;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.seda.StageManager;
import org.apache.excalibur.event.socket.ReadWriteSocketState;

/**
 * Creates {@link AsyncDatagramSocket} implementing objects that can 
 * be used to asynchronously connect with other udp sockets.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultAsyncDatagramSocketFactory
    extends AbstractLogEnabled
    implements AsyncDatagramSocketFactory, Serviceable, Initializable
{
    /** The socket handler */
    private AsyncDatagramSocketHandler m_handler = null;

    /** The components manager object. */
    private ServiceManager m_serviceManager;

    //----------------------- AsyncDatagramSocketFactory implementation
    /**
     * @see AsyncDatagramSocketFactory#createSocket(InetAddress, int, Sink, int, int)
     */
    public AsyncDatagramSocket createSocket(
        InetAddress localAddress,
        int localPort,
        Sink completionQueue,
        int maxPacketSize,
        int writeClogThreshold)
        throws SinkException, IOException
    {
        return new DefaultAsyncDatagramSocket(
            localAddress,
            localPort,
            completionQueue,
            maxPacketSize,
            writeClogThreshold);
    }

    /**
     * @see AsyncDatagramSocketFactory#createSocket(int, Sink)
     */
    public AsyncDatagramSocket createSocket(
        int localPort,
        Sink completionQueue)
        throws SinkException, IOException
    {
        return new DefaultAsyncDatagramSocket(localPort, completionQueue);
    }

    /**
     * @see AsyncDatagramSocketFactory#createSocket(Sink)
     */
    public AsyncDatagramSocket createSocket(Sink completionQueue)
        throws SinkException, IOException
    {
        return new DefaultAsyncDatagramSocket(completionQueue);
    }

    //-------------------------- Serviceable implementation
    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }

    //------------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        final StageManager stageManager =
            (StageManager) m_serviceManager.lookup(StageManager.ROLE);
        try
        {
            final ServiceManager manager = stageManager.getServiceManager();
            final String role = AsyncDatagramSocketHandler.ROLE;
            m_handler = (AsyncDatagramSocketHandler)manager.lookup(role);
        }
        finally
        {
            m_serviceManager.release(stageManager);
        }
    }

    //-------------------------- DefaultAsyncDatagramSocketFactory inner classes
    /**
     * A DefaultUdpSocket implements an asynchronous datagram socket. 
     * Applications create a DefaultUdpSocket and associate a Sink with 
     * it. Packets received on the socket will be pushed onto 
     * the Sink as {@link IncomingPacket} objects.
     * The DefaultUdpSocket can also be used to send messages to the 
     * socket, and to associate a default send address using 
     * the {@link #connect(InetAddress, int)} method.
     *
     * @version $Revision: 1.1 $
     * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    final class DefaultAsyncDatagramSocket implements AsyncDatagramSocket
    {
        /** The default maximum packet size read by the socket. */
        public static final int DEFAULT_MAX_PACKETSIZE = 16384;

        /**
         * The maximum size, in bytes, of packets that this socket 
         * will attempt to receive. The default is 
         * {@link #DEFAULT_MAX_PACKETSIZE}
         */
        private final int m_maxPacketSize;

        /**
         * The maximum number of outstanding writes on this socket 
         * before a {@link SinkCloggedEvent} is pushed to the 
         * connection's completion queue. This is effectively 
         * the maximum depth threshold for this connection's Sink. 
         * The default value is -1, which indicates that no 
         * SinkCloggedEvents will be generated.
         */
        private final int m_writeClogThreshold;

        /** Queue on which the events for this socket are pushed. */
        private final Sink m_completionQueue;

        /** The address of the remote host. */
        private final InetAddress m_address;

        /** The port number of the remote host. */
        private final int m_port;

        /** Internal ReadWriteSocketState associated with this connection */
        private ReadWriteSocketState m_socketState = null;

        //------------------------- DefaultUdpSocket constructors
        /**
         * Create a socket bound to any available local 
         * port. This is mainly used to create outgoing-only 
         * sockets.
         * @since May 21, 2002)
         * 
         * @param completionQueue
         *  The completion queue to push events on.
         * @throws IOException
         *  When an IOException occurs
         */
        public DefaultAsyncDatagramSocket(Sink completionQueue) 
            throws SinkException, IOException
        {
            this(null, -1, completionQueue, DEFAULT_MAX_PACKETSIZE, -1);
        }

        /**
         * Create a socket bound to a given local port. This is 
         * mainly used to create outgoing-only sockets.
         * @since May 21, 2002)
         * 
         * @param localPort
         *  The port the socket is bound to.
         * @param completionQueue
         *  The completion queue to push events on.
         * @throws IOException
         *  When an IOException occurs
         */
        public DefaultAsyncDatagramSocket(int localPort, Sink completionQueue)
            throws SinkException, IOException
        {
            this(null, localPort, completionQueue, DEFAULT_MAX_PACKETSIZE, -1);
        }

        /**
         * Create a socket bound to the given local address and 
         * local port.
         * @since May 21, 2002)
         * 
         * @param localAddress
         *  The address of the local host the socket is bound to.
         * @param localPort 
         *  The port that the scoket is bound to.
         * @param completionQueue
         *  The completion queue to push events on.
         * @param maxPacketSize 
         *  The maximum size, in bytes, of packets that this socket 
         *  will attempt to receive. 
         * @param writeClogThreshold 
         *  The maximum number of outstanding writes on this socket 
         *  before a {@link SinkCloggedEvent} is pushed to the 
         *  connection's completion queue.
         * @throws IOException
         *  When an IOException occurs
         */
        public DefaultAsyncDatagramSocket(
            InetAddress localAddress,
            int localPort,
            Sink completionQueue,
            int maxPacketSize,
            int writeClogThreshold)
            throws SinkException, IOException
        {
            m_address = localAddress;
            m_port = localPort;
            m_maxPacketSize = maxPacketSize;
            m_writeClogThreshold = writeClogThreshold;
            m_completionQueue = completionQueue;

            m_handler.open(
                new OpenRequest(
                    this,
                    m_address,
                    m_port,
                    getCompletionQueue(),
                    getWriteClogThreshold(),
                    getMaxPacketSize()));
        }

        //-------------------------- DefaultUdpSocket specific implementation
        /**
         * Returns the maximum number of outstanding writes on 
         * this socket before a {@link SinkCloggedEvent} is pushed 
         * to the  connection's completion queue. This is effectively 
         * the maximum depth threshold for this connection's Sink. 
         * The default value is -1, which indicates that no 
         * {@link SinkCloggedEvent}s will be generated.
         * @since May 21, 2002)
         * 
         * @return int
         *  the maximum number of outstanding writes on 
         *  this socket
         */
        public int getWriteClogThreshold()
        {
            return m_writeClogThreshold;
        }

        /**
         * Returns the maximum size, in bytes, of packets that 
         * this socket will attempt to receive. The default is 
         * {@link #DEFAULT_MAX_PACKETSIZE}.
         * @since May 21, 2002)
         * 
         * @return int
         *  the maximum size, in bytes, of packets that this socket 
         *  will attempt to receive
         */
        public int getMaxPacketSize()
        {
            return m_maxPacketSize;
        }

        /**
         * Returns the completion queue for this socket, which 
         * is a queue where socket events are enqueued on.
         * @since May 21, 2002)
         * 
         * @return {@link Sink}
         *  The completion queue for this socket
         */
        public Sink getCompletionQueue()
        {
            return m_completionQueue;
        }
      
    }
}