/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * The interface used to create server socket channels.
 *
 * @author <a href="mailto:khoehn@smartstream.net">Kurt R. Hoehn</a>
 */
public interface ServerChannelFactory
{
    /**
     * Creates a server socket channel on a particular network interface.
     *
     * @param address the network interface to bind to.
     * @return the created ServerSocketChannel
     * @exception IOException if an error occurs
     */
    public ServerSocketChannel createServerChannel( InetSocketAddress address )
        throws IOException;

    /**
     * Creates a server socket channel on specified port.
     *
     * @param port the server port
     * @return the created ServerSocketChannel
     * @exception IOException if an error occurs
     */
    public ServerSocketChannel createServerChannel( int port )
        throws IOException;
}
