/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import org.apache.avalon.Component;
import org.apache.cornerstone.services.sockets.ServerSocketFactory;

/**
 * Factory implementation for vanilla TCP sockets.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public class DefaultServerSocketFactory
    implements ServerSocketFactory, Component
{
    /**
     * Creates a socket on specified port.
     *
     * @param port the port
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( final int port )
        throws IOException
    {
        return new ServerSocket( port );
    }

    /**
     * Creates a socket on specified port with a specified backLog.
     *
     * @param port the port
     * @param backLog the backLog
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( int port, int backLog )
        throws IOException
    {
        return new ServerSocket( port, backLog );
    }

    /**
     * Creates a socket on a particular network interface on specified port
     * with a specified backLog.
     *
     * @param port the port
     * @param backLog the backLog
     * @param bindAddress the network interface to bind to.
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( int port, int backLog, InetAddress bindAddress )
        throws IOException
    {
        return new ServerSocket( port, backLog, bindAddress );
    }
}

