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

/**
 * Request to establish a Tcp based connection. It is send to 
 * the Tcp Writing Stage and based on it a channel is opened
 * and a selection key is registered to receive connection
 * events from the non-blocking I/O implementation.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class ConnectRequest
{
    /** The client socket for the request. */
    private final AsyncSocket m_clientSocket;

    /** The completion queue for the completion events */
    private final Sink m_completionQueue;

    /** The address to connect to. */
    private final InetAddress m_inetAddress;

    /** The port number ofthe host to connect to. */
    private final int m_port;

    private final int m_writeClogThreshold;
    private final int m_connectClogTries;

    //-------------------------- ConnectRequest constructors
    /**
     * Constructs a new connect request based on the passed in
     * client socket, ip address, and completion queue.
     * @since May 21, 2002
     * 
     * @param clientSocket
     *  The client socket to connect with
     * @param inetAddress 
     *  The ip address of the host to connect to.
     * @param port
     *  The port number on the host
     * @param completionQueue
     *  The completion queue for the completion events
     * @param writeClogThreshold 
     *  The threshold for writing to clogged sinks
     * @param connectClogTries
     *  The number of tries to connect when a sink is clogged.
     */
    public ConnectRequest(
        AsyncSocket clientSocket,
        InetAddress inetAddress,
        int port,
        Sink completionQueue,
        int writeClogThreshold,
        int connectClogTries)
    {
        super();
        m_clientSocket = clientSocket;
        m_completionQueue = completionQueue;
        m_inetAddress = inetAddress;
        m_port = port;
        m_writeClogThreshold = writeClogThreshold;
        m_connectClogTries = connectClogTries;
    }

    //-------------------------- ConnectRequest specific implementation
    /**
     * Returns the threshold for writing to clogged sinks.
     * @since May 21, 2002
     * 
     * @return int
     *  the threshold for writing to clogged sinks.
     */
    public int getWriteClogThreshold()
    {
        return m_writeClogThreshold;
    }

    /**
     * Returns the port number on the host to connect to.
     * @since May 21, 2002
     * 
     * @return int
     *  the port number on the host to connect to.
     */
    public int getPort()
    {
        return m_port;
    }

    /**
     * Returns the ip address of the host to connect to.
     * @since May 21, 2002
     * 
     * @return {@link InetAddress}
     *  the ip address of the host to connect to.
     */
    public InetAddress getInetAddress()
    {
        return m_inetAddress;
    }

    /**
     * Returns the number of tries to connect when a sink 
     * is clogged.
     * @since May 21, 2002
     * 
     * @return int
     *  The number of tries to connect when a sink is clogged.
     */
    public int getConnectClogTries()
    {
        return m_connectClogTries;
    }

    /**
     * Returns the sink where to enqueue the completion events.
     * @since May 21, 2002
     * 
     * @return {@link Sink}
     *  The completion queue for the completion events
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }

    /**
     * Returns the client socket to connect with the server.
     * @since May 21, 2002
     * 
     * @return {@link ClientSocket}
     *  the client socket to connect with the server.
     */
    public AsyncSocket getClientSocket()
    {
        return m_clientSocket;
    }
}
