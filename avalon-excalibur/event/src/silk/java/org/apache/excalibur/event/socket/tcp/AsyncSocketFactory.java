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

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;

/**
 * A factory for client socket objects.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncSocketFactory
{
    String ROLE = AsyncSocketFactory.class.getName();

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
    public AsyncSocket createSocket(
        InetAddress address,
        int port,
        Sink completionQueue,
        int writeClogThreshold,
        int connectClogTries) throws SinkException;

    /**
     * Create a socket connecting to the given address 
     * and port. A {@link AsyncTcpConnection} will be posted 
     * to the given Sink when the connection is established. 
     * If an error occurs, a {@link ConnectFailedEvent}
     * will be posted instead.
     * 
     * @param address
     */
    public AsyncSocket createSocket(
        InetAddress address,
        int port,
        Sink completionQueue) throws SinkException;

    /**
     * Create a socket connecting to the given address 
     * and port. A {@link AsyncTcpConnection} will be posted 
     * to the given Sink when the connection is established. 
     * If an error occurs, a {@link ConnectFailedEvent}
     * will be posted instead.
     * @since May 21, 2002
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
    public AsyncSocket createSocket(
        String hostName,
        int port,
        Sink completionQueue,
        int writeClogThreshold,
        int connectClogTries)
        throws SinkException, UnknownHostException;

    /**
     * Create a socket connecting to the given address 
     * and port. A {@link AsyncTcpConnection} will be posted 
     * to the given Sink when the connection is established. 
     * If an error occurs, a {@link ConnectFailedEvent}
     * will be posted instead.
     * @since May 21, 2002
     * 
     * @param host
     *  A string identifying the host name.
     * @param port
     *  The port number of the host
     * @param completionQueue
     *  The completion queue for the completion events
     */
    public AsyncSocket createSocket(
        String host,
        int port,
        Sink completionQueue)
        throws SinkException, UnknownHostException;
}