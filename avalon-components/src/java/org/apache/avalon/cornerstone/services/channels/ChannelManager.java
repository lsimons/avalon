/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.channels;

/**
 * Service to manage the channel factories.
 *
 * @author <a href="mailto:khoehn@smartstream.net">Kurt R. Hoehn</a>
 */
public interface ChannelManager
{
    String ROLE = ChannelManager.class.getName();

    /**
     * Retrieve a server channel factory by name.
     *
     * @param name the name of server channel factory
     * @return the ServerChannelFactory
     * @exception Exception if server socket factory is not available
     */
    public ServerChannelFactory getServerChannelFactory( String name )
        throws Exception;

    /**
     * Retrieve a client socket channel factory by name.
     *
     * @param name the name of client socket channel factory
     * @return the SocketChannelFactory
     * @exception Exception if socket channel factory is not available
     */
    public SocketChannelFactory getSocketChannelFactory( String name )
        throws Exception;
}
