/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.io.IOException;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;

/**
 * A factory for asynchronous server sockets. Events
 * are enqueued into the given completion queue.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncServerSocketFactory
{
    String ROLE = AsyncServerSocketFactory.class.getName();

    /**
     * Open a server socket listening on the given port. 
     * When a connection arrives, a {@link AsyncTcpConnection}
     * will be posted to the given completionQueue. If the 
     * server socket dies, an {@link ServerSocketClosedEvent}
     * will be posted instead.
     * @since May 21, 2002
     * 
     * @param serverPort
     *  The port on which to accept socket connections
     * @param eventSink
     *  The queue on which events are enqueued
     * @throws IOException 
     *  When an IOException ocurrs.
     */
    public AsyncServerSocket createServerSocket(int serverPort, Sink eventSink)
        throws IOException, SinkException;

    /**
     * Open a server socket listening on the given port. 
     * When a connection arrives, a {@link AsyncTcpConnection}
     * will be posted to the given completion queue. If the 
     * server socket dies, a {@link ServerSocketClosedEvent} 
     * will be posted instead.
     * @since May 21, 2002
     * 
     * @param serverPort
     *  The port at which to accept socket connections
     * @param eventSink 
     *  The queue on which events are enqueued
     * @param writeClogThreshold 
     *  The maximum number of outstanding write requests to a 
     *  connection established using this socket before a
     *  {@link SinkCloggedEvent} is pushed onto the completion 
     *  queue for that connection. The default value is 
     *  <m_code>-1</m_code>, which indicates that no 
     *  {@link SinkCloggedEvents} will be generated.
     * @throws IOException 
     *  When an IOException ocurrs.
     */
    public AsyncServerSocket createServerSocket(
        int serverPort, Sink eventSink, int writeClogThreshold)
            throws IOException, SinkException;
}
