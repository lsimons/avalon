/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;

import java.io.IOException;

import org.apache.avalon.framework.context.Context;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;

/**
 * The http connector factory creates connector objects based
 * on a the passed in queue.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface HttpConnectorFactory
{
    String ROLE = HttpConnectorFactory.class.getName();
    
    /**
     * Creates an Http Connector based on the passed in
     * event queue. The event queue is fed with requests 
     * and connections.
     * @since Sep 26, 2002
     * 
     * @param queue
     *  An event queue that is fed with requests and 
     *  connections
     * @return HttpConnector
     *  A created http connector object.
     */
    HttpConnector createHttpConnector(
        int port, Context context, Sink queue) 
            throws IOException, SinkException;
    
    /**
     * Creates an Http Connector based on the passed in
     * event queue. The event queue is fed with requests 
     * and connections.
     * @since Sep 26, 2002
     * 
     * @param queue
     *  An event queue that is fed with requests and 
     *  connections
     * @return HttpConnector
     *  A created http connector object.
     */
    HttpConnector createHttpConnector(
        int port, Context context, int threshold, Sink queue)
             throws IOException, SinkException;
}
