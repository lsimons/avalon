/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.connection;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;

/**
 * This interface is the way in which handlers are created.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ConnectionHandler
{
    /**
     * Handle a connection.
     * This handler is responsible for processing connections as they occur.
     *
     * @param connection the connection
     * @exception IOException if an error reading from socket occurs
     * @exception ProtocolException if an error handling connection occurs
     */
    void handleConnection( Socket connection )
        throws IOException, ProtocolException;
}