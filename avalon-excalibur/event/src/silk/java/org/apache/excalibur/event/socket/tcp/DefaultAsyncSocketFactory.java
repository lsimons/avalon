/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.seda.StageManager;

/**
 * An implementation of the {@link AsyncSocketFactory} interface
 * representing a factory for SEDA client sockets. A client socket 
 * acts as a facade to the SEDA style dispatch mechanism.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultAsyncSocketFactory
    extends AbstractLogEnabled
    implements AsyncSocketFactory, Serviceable, Initializable
{
    /** The components service manager. */
    private ServiceManager m_serviceManager;

    /** The socket handler */
    private AsyncSocketHandler m_handler = null;

    //-------------------------- AsyncSocketFactory implementation
    /**
     * @see AsyncSocketFactory#createSocket(InetAddress, int, Sink, int, int)
     */
    public AsyncSocket createSocket(
        InetAddress address,
        int port,
        Sink completionQueue,
        int writeClogThreshold,
        int connectClogTries) throws SinkException
    {
        return new DefaultAsyncSocket(
            address,
            port,
            completionQueue,
            writeClogThreshold,
            connectClogTries);
    }

    /**
     * @see AsyncSocketFactory#createSocket(InetAddress, int, Sink)
     */
    public AsyncSocket createSocket(
        InetAddress address,
        int port,
        Sink completionQueue) throws SinkException
    {
        return new DefaultAsyncSocket(address, port, completionQueue);
    }

    /**
     * @see AsyncSocketFactory#createSocket(String, int, Sink, int, int)
     */
    public AsyncSocket createSocket(
        String hostName,
        int port,
        Sink completionQueue,
        int writeClogThreshold,
        int connectClogTries)
        throws SinkException, UnknownHostException
    {
        return new DefaultAsyncSocket(
            hostName,
            port,
            completionQueue,
            writeClogThreshold,
            connectClogTries);
    }

    /**
     * @see AsyncSocketFactory#createSocket(String, int, Sink)
     */
    public AsyncSocket createSocket(
        String hostName,
        int port,
        Sink completionQueue)
        throws SinkException, UnknownHostException
    {
        return new DefaultAsyncSocket(hostName, port, completionQueue);
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
            final String role = AsyncSocketHandler.ROLE;
            m_handler = (AsyncSocketHandler)manager.lookup(role);
        }
        finally
        {
            m_serviceManager.release(stageManager);
        }
    }

    //-------------------------- DefaultAsyncSocketFactory inner classes
    /**
     * A ClientSocket implements an asynchronous outgoing 
     * socket connection. Applications create a ClientSocket 
     * and associate a Sink with it. When the connection is 
     * established a {@link AsyncTcpConnection} object will be pushed 
     * to the given Sink. The {@link AsyncTcpConnection} is then used 
     * for actual communication.
     *
     * @version $Revision: 1.1 $
     * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    public class DefaultAsyncSocket implements AsyncSocket
    {
        /** The address to connect to. */
        private final InetAddress m_address;

        /** The port of the remote host. */
        private final int m_port;

        //------------------------ ClientSocket constructors
        /**
         * Create a socket connecting to the given address 
         * and port. A {@link AsyncTcpConnection} will be posted 
         * to the given Sink when the connection is established. 
         * If an error occurs, a {@link ConnectFailedEvent}
         * will be posted instead.
         * 
         * @param address
         *  An ip address for the host to connect to.
         * @param port
         *  The port number of the host
         * @param completionQueue
         *  The completion queue for the completion events
         */
        public DefaultAsyncSocket(
            InetAddress address, int port, Sink completionQueue) 
                throws SinkException 
        {
            this(address, port, completionQueue, -1, -1);
        }

        /**
         * Create a socket connecting to the given address 
         * and port. A {@link AsyncTcpConnection} will be posted 
         * to the given Sink when the connection is established. 
         * If an error occurs, a {@link ConnectFailedEvent}
         * will be posted instead.
         * @since May 21, 2002)
         * 
         * @param host
         *  A string identifying the host name.
         * @param port
         *  The port number of the host
         * @param completionQueue
         *  The completion queue for the completion events
         */
        public DefaultAsyncSocket(
            String hostName,
            int port,
            Sink completionQueue)
            throws SinkException, UnknownHostException
        {
            this(
                InetAddress.getByName(hostName),
                port,
                completionQueue,
                -1,
                -1);
        }

        /**
         * Create a socket connecting to the given address 
         * and port. A {@link AsyncTcpConnection} will be posted 
         * to the given Sink when the connection is established. 
         * If an error occurs, a {@link ConnectFailedEvent}
         * will be posted instead.
         * @since May 21, 2002)
         *
         * @param address
         *  An ip address for the host to connect to.
         * @param port
         *  The port number of the host
         * @param completionQueue
         *  The completion queue for the completion events
         * @param writeClogThreshold 
         *  The maximum number of outstanding writes on this 
         *  socket before a {@link SinkCloggedEvent} is pushed 
         *  to the connection's completion queue. This is 
         *  effectively the maximum depth threshold for this 
         *  connection's Sink. The default value is <m_code>-1</m_code>
         *  whichindicates that no SinkCloggedEvents will be 
         *  generated.
         * @param connectClogTries 
         *  The number of times the aSocket layer will attempt 
         *  to push a new entry onto the given Sink while the
         *  Sink is full. The queue entry will be dropped after
         *  this many tries. The default value is <m_code>-1</m_code>, 
         *  which indicates that the Socketlayer will attempt 
         *  to push the queue entry indefinitely.
         */
        public DefaultAsyncSocket(
            InetAddress address,
            int port,
            Sink completionQueue,
            int writeClogThreshold,
            int connectClogTries) throws SinkException 
        {
            m_address = address;
            m_port = port;

            m_handler.connect(
                new ConnectRequest(
                    this,
                    address,
                    port,
                    completionQueue,
                    writeClogThreshold,
                    connectClogTries));
        }

        /**
         * Create a socket connecting to the given address 
         * and port. A {@link AsyncTcpConnection} will be posted 
         * to the given Sink when the connection is established. 
         * If an error occurs, a {@link ConnectFailedEvent}
         * will be posted instead.
         * @since May 21, 2002)
         * 
         * @param host
         *  A string identifying the host name.
         * @param port
         *  The port number of the host
         * @param completionQueue
         *  The completion queue for the completion events
         * @param writeClogThreshold 
         *  The maximum number of outstanding writes on this 
         *  socket before a {@link SinkCloggedEvent} is pushed 
         *  to the connection's completion queue. This is 
         *  effectively the maximum depth threshold for this 
         *  connection's Sink. The default value is <m_code>-1</m_code>
         *  whichindicates that no SinkCloggedEvents will be 
         *  generated.
         * @param connectClogTries 
         *  The number of times the aSocket layer will attempt 
         *  to push a new entry onto the given Sink while the
         *  Sink is full. The queue entry will be dropped after
         *  this many tries. The default value is <m_code>-1</m_code>, 
         *  which indicates that the Socketlayer will attempt 
         *  to push the queue entry indefinitely.
         * @throws UnknownHostException 
         *  if the host name cannot be resolved.
         */
        public DefaultAsyncSocket(
            String hostName,
            int port,
            Sink completionQueue,
            int writeClogThreshold,
            int connectClogTries)
            throws SinkException, UnknownHostException
        {
            this(
                InetAddress.getByName(hostName),
                port,
                completionQueue,
                writeClogThreshold,
                connectClogTries);
        }

        //------------------------ AsyncSocket implementation
        /**
         * @see AsyncSocket#getAddress()
         */
        public InetAddress getAddress()
        {
            return m_address;
        }

        /**
         * @see AsyncSocket#getPort()
         */
        public int getPort()
        {
            return m_port;
        }
    }
}