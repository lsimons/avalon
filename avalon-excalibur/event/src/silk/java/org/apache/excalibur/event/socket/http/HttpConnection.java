/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;

import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.AsyncConnection;

/**
 * 
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface HttpConnection extends AsyncConnection
{
    /**
     * Enqueue outgoing data on this connection. The 'element' 
     * must be of type httpResponder.
     */
    void write(AbstractHttpResponse response) throws SinkException;
    
    /**
     * Return the connector.
     */
    HttpConnector getConnector();
}
