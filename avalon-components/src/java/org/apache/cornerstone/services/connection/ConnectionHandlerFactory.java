/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.connection;

/**
 * This interface is the way in which handlers are created.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ConnectionHandlerFactory
{
    /**
     * Construct an appropriate ConnectionHandler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    ConnectionHandler createConnectionHandler()
        throws Exception;
}

