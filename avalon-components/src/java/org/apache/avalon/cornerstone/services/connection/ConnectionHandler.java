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
 * This interface is the way in which incoming connections are processed.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface ConnectionHandler
{
    /**
     * Processes connections as they occur. The handler should not
     * close the <tt>connection</tt>, the caller will do that.
     *
     * @param connection the connection
     * @exception IOException if an error reading from socket occurs
     * @exception ProtocolException if an error handling connection occurs
     */
    void handleConnection( Socket connection )
        throws IOException, ProtocolException;
}
