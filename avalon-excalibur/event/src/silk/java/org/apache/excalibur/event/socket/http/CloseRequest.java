/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;



/**
 * Request to close a specified connector object.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class CloseRequest
{
    private final HttpConnector m_connector;

    //------------------------- OpenRequest constructors
    /**
     * Constructor for a close request that takes
     * the connector to be closed.
     * @since May 21, 2002
     * 
     * @param connector
     *  The connector for connection establishment
     */
    public CloseRequest(HttpConnector connector)
    {
        m_connector = connector;
    }

    //------------------------- OpenRequest specific implementation
    /**
     * Returns the connector for connection establishment
     * @since May 21, 2002
     * 
     * @return {@link HttpConnector}
     *  the HttpConnector for connection establishment
     */
    public HttpConnector getConnector()
    {
        return m_connector;
    }
}