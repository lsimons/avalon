/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.connection;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import org.apache.avalon.framework.component.Component;

/**
 * This interface is the way in which handlers are created.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ConnectionHandler
    extends Component
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

