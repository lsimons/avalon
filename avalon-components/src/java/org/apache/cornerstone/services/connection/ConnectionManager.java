/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.connection;

import java.net.ServerSocket;
import org.apache.phoenix.Service;
import org.apache.avalon.util.thread.ThreadPool;

/**
 * This is the service through which ConnectionManagement occurs.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ConnectionManager
    extends Service
{
    /**
     * Start managing a connection.
     * Management involves accepting connections and farming them out to threads 
     * from pool to be handled.
     *
     * @param name the name of connection
     * @param socket the ServerSocket from which to 
     * @param handlerFactory the factory from which to aquire handlers
     * @param threadPool the thread pool to use
     * @exception Exception if an error occurs
     */
    void connect( String name, 
                  ServerSocket socket,
                  ConnectionHandlerFactory handlerFactory,
                  ThreadPool threadPool )
        throws Exception;

    /**
     * Start managing a connection. 
     * This is similar to other connect method except that it uses default thread pool.
     *
     * @param name the name of connection
     * @param socket the ServerSocket from which to 
     * @param handlerFactory the factory from which to aquire handlers
     * @exception Exception if an error occurs
     */
    void connect( String name, 
                  ServerSocket socket, 
                  ConnectionHandlerFactory handlerFactory )
        throws Exception;

    /**
     * This shuts down all handlers and socket, waiting for each to gracefully shutdown.
     *
     * @param name the name of connection
     * @exception Exception if an error occurs
     */
    void disconnect( String name )
        throws Exception;

    /**
     * This shuts down all handlers and socket. 
     * If tearDown is true then it will forcefully shutdown all connections and try 
     * to return as soon as possible. Otherwise it will behave the same as 
     * void disconnect( String name );
     *
     * @param name the name of connection
     * @param tearDown if true will forcefully tear down all handlers
     * @exception Exception if an error occurs
     */
    void disconnect( String name, boolean tearDown )
        throws Exception;
}
