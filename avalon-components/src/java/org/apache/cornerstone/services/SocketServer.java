/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services;

import java.net.InetAddress;
import java.net.Socket;
import org.apache.avalon.Component;
import org.apache.phoenix.Service;

/**
 * @author  Federico Barbieri <fede@apache.org>
 * @deprecated This class is deprecated in favour of org.apache.avalon.cornerstone.sockets.* and org.apache.cornerstone.services.connection.*. This still has bugs with respect to closing connections at shutdown time and it also exhibits scalability problems.
 */
public interface SocketServer 
    extends Service
{
    String DEFAULT = "DEFAULT";
    String IPFILTERING = "IPFILTERING";
    String TLS = "TLS";

    void openListener( String name, 
                       String type, 
                       int port, 
                       InetAddress bind, 
                       SocketServer.SocketHandler handler );

    void openListener( String name, String type, int port, SocketHandler handler );

    void closeListener( String name );

    public interface SocketHandler 
    {
        /**
         * Menage request on passed socket.
         *
         * @param socket The opened socket. This method is called by a Listener.
         */
        void parseRequest( Socket socket );
    }

    public interface Listener 
        extends Component, Runnable 
    {
        void listen( int port, SocketHandler handler, InetAddress bind );
    }
}
