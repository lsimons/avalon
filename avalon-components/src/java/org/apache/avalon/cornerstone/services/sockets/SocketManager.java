/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.sockets;

/**
 * Service to manager the socket factories.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface SocketManager
{
    String ROLE = SocketManager.class.getName();

    /**
     * Retrieve a server socket factory by name.
     *
     * @param name the name of server socket factory
     * @return the ServerSocketFactory
     * @exception Exception if server socket factory is not available
     */
    ServerSocketFactory getServerSocketFactory( String name )
        throws Exception;

    /**
     * Retrieve a client socket factory by name.
     *
     * @param name the name of client socket factory
     * @return the SocketFactory
     * @exception Exception if socket factory is not available
     */
    SocketFactory getSocketFactory( String name )
        throws Exception;
}
