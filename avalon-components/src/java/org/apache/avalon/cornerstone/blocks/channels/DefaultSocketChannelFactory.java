/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import org.apache.avalon.cornerstone.services.channels.SocketChannelFactory;

/**
 * Factory implementation for vanilla socket channels.
 *
 * @author <a href="mailto:khoehn@smartstream.net">Kurt R. Hoehn</a>
 */
public class DefaultSocketChannelFactory
    implements SocketChannelFactory
{
    /**
     * Create a socket channel and connect to remote address specified.
     *
     * @param address the remote address
     * @return the socket channel
     * @exception IOException if an error occurs
     */
    public SocketChannel createSocketChannel( final InetSocketAddress address )
        throws IOException
    {
        final SocketChannel channel = SocketChannel.open();
        channel.connect( address );

        return channel;
    }

    /**
     * Create a socket channel and connect to remote address specified
     * from host and port.
     *
     * @param host the remote host
     * @param port the remote port
     * @return the socket channel
     * @exception IOException if an error occurs
     */
    public SocketChannel createSocketChannel( String host, int port )
        throws IOException
    {
        return createSocketChannel( new InetSocketAddress( host, port ) );
    }
}
