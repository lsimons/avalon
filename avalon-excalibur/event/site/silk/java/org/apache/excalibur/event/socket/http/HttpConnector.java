/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;

import org.apache.avalon.framework.context.Context;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;

/**
 * An HttpConnector represents a receptor based on a 
 * asynchronous server socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface HttpConnector
{
    /** 
     * Closes the HttpConnector and releases all associated
     * resources.
     * @since Sep 25, 2002
     */
    void close() throws SinkException;
    
    /**
     * Returns the completion queue the connector is
     * serviceing.
     * @since Sep 26, 2002
     * 
     * @return Sink
     *  The completion queue the connector is serviceing.
     */
    Sink getCompletionQueue();
    
    /**
     * Returns the context for the connector.
     * @since Sep 26, 2002
     * 
     * @return Context
     *  The context for the connector.
     */
    Context getContext();
}
