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

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;

/**
 * A factory for asynchronous SEDA datagram sockets.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncDatagramSocketFactory
{
    String ROLE = AsyncDatagramSocketFactory.class.getName();

    /**
     * Create a socket bound to the given local address and 
     * local port.
     * @since May 21, 2002
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
     *  before a {@link ConnectionCloggedEvent} is pushed to the 
     *  connection's completion queue.
     * @throws IOException
     *  When an IOException occurs
     */
    public AsyncDatagramSocket createSocket(
        InetAddress localAddress,
        int localPort,
        Sink completionQueue,
        int maxPacketSize,
        int writeClogThreshold)
        throws SinkException, IOException;

    /**
     * Create a socket bound to a given local port. This is 
     * mainly used to create outgoing-only sockets.
     * @since May 21, 2002
     * 
     * @param localport
     *  The port the socket is bound to.
     * @param completionQueue
     *  The completion queue to push events on.
     * @throws IOException
     *  When an IOException occurs
     */
    public AsyncDatagramSocket createSocket(
        int localport,
        Sink completionQueue)
        throws SinkException, IOException;

    /**
     * Create a socket bound to any available local 
     * port. This is mainly used to create outgoing-only 
     * sockets.
     * @since May 21, 2002
     * 
     * @param completionQueue
     *  The completion queue to push events on.
     * @throws IOException
     *  When an IOException occurs
     */
    public AsyncDatagramSocket createSocket(Sink completionQueue)
        throws SinkException, IOException;

}