/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.net.InetAddress;

import org.apache.excalibur.event.socket.AsyncSocketBase;

/**
 * Abstracts client sockets and defines their interface
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncSocket extends AsyncSocketBase
{
    /**
     * Returns the {@link InetAddress}, which this 
     * socket is connected to.
     * @since May 21, 2002
     * 
     * @return {@link InetAddress}
     *  The address that this socket is connected to.
     */
    public InetAddress getAddress();

    /**
     * Returns the port which this socket is connected to.
     * @since May 21, 2002
     * 
     * @return int
     *  The port number that this socket is connected to.
     */
    public int getPort();
}
