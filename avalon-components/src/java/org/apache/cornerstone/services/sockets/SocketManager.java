/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.sockets;

import org.apache.avalon.ComponentNotFoundException;
import org.apache.phoenix.Service;

/**
 * Service to manager the socket factories.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface SocketManager 
    extends Service
{
    /**
     * Retrieve a server socket factory by name.
     *
     * @param name the name of server socket factory
     * @return the ServerSocketFactory
     * @exception ComponentNotFoundException if server socket factory is not available
     */
    ServerSocketFactory getServerSocketFactory( String name )
        throws ComponentNotFoundException;

    /**
     * Retrieve a client socket factory by name.
     *
     * @param name the name of client socket factory
     * @return the SocketFactory
     * @exception ComponentNotFoundException if socket factory is not available
     */
    SocketFactory getSocketFactory( String name )
        throws ComponentNotFoundException;
}
