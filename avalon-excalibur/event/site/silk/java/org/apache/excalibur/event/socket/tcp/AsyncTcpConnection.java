/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import org.apache.excalibur.event.socket.AsyncConnection;

/**
 * Represents a tcp specific connection between a server and
 * a client over a socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncTcpConnection extends AsyncConnection
{
    /**
     * Returns the {@link AsyncSocketBase} from which this 
     * connection came. Returns <m_code>null</m_code> if this 
     * connection resulted from a ServerSocket.
     * @since May 21, 2002
     * 
     * @return {@link AsyncSocketBase}
     *  the socket from which this connection came
     */
    AsyncSocket getSocket();
    
    /**
     * Returns the {@link AsyncServerSocket} from which this 
     * connection came. Returns <m_code>null</m_code> if this 
     * connection resulted from a ClientSocket.
     * @since May 21, 2002
     * 
     * @return {@link AsyncServerSocket}
     *  the socket from which this connection came
     */
    AsyncServerSocket getServerSocket();
}
