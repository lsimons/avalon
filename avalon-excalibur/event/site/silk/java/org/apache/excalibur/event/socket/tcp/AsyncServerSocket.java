/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.AsyncSocketBase;

/**
 * Represents an asynchronous server socket. Allows to suspend 
 * and resume acceptence of socket connections and to close the
 * socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncServerSocket extends AsyncSocketBase
{
    /**
     * Asynchronously close this server socket. 
     * A {@link ServerSocketClosedEvent} will be posted 
     * to the completion queue associated with this
     * server socket when the close completes.
     * @since May 21, 2002)
     */
    void close() throws SinkException;

    /**
     * Return the local port for this socket. Returns 
     * <m_code>-1</m_code> if no local port has yet been 
     * established.
     * @since May 21, 2002
     * 
     * @return int 
     *  the local port for this socket; <m_code>-1</m_code>
     *  if no local port has yet been established.
     */
    int getLocalPort();

    /**
     * Returns the port that this socket is listening on.
     * @since May 21, 2002
     * 
     * @return int
     *  The port that this socket is listening on.
     */
    int getPort();

    /**
     * Request that this server socket resume accepting 
     * new connections. This request will not take effect 
     * immediately.
     * @since May 21, 2002)
     */
    void resumeAccept() throws SinkException;

    /**
     * Request that this server socket stop accepting new 
     * connections. This request will not take effect 
     * immediately.
     * @since May 21, 2002)
     */
    void suspendAccept() throws SinkException;

}
