/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example3;

import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.http.HttpConnection;
import org.apache.excalibur.event.socket.http.HttpRequest;

/**
 * A receiver handles all events on the server side. 
 * Each incoming packet is echoed back to the client.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface HttpRequestHandler
{
    /**
     * Notifies that a connection is there to read.
     * @since Sep 23, 2002
     * 
     * @param connection
     *  The connection to be read
     * @throws SinkException
     *  If an event cannot be enqueued
     */   
    void accept(HttpConnection connection) throws SinkException;
    
    /**
     * Echoes the passed in packet back to the sender identified
     * in the packet object.
     * @since Sep 23, 2002
     * 
     * @param packet
     *  The packet to be echoed back
     * @throws SinkException
     *  If an event cannot be enqueued
     */   
    void service(HttpRequest request) throws SinkException;
    
}
