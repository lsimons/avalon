/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;



/**
 * Request to open a specified HttpConnector.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
class OpenRequest
{
    /** The port the listener should listen on */
    private final int m_port;
    
    /** The connector object to be opened */
    private final HttpConnector m_connector;
    
    /** The threshold for the tcp/ip socket */
    private final int m_writeClogThreshold;

    //------------------------- OpenRequest constructors
    /**
     * Constructor for a creation request that takes
     * the connector, port number and other information.
     * @since May 21, 2002
     * 
     * @param connector
     *  The connector for connection establishment
     * @param writeClogThreshold
     *  The threshold for the tcp/ip socket
     * @param port
     *  The port the listener should listen on.
     */
    public OpenRequest(
        HttpConnector connector,
        int port,
        int writeClogThreshold)
    {
        m_connector = connector;
        m_port = port;
        m_writeClogThreshold = writeClogThreshold;
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

    /**
     * Returns the port number of the destination host.
     * @since May 21, 2002
     * 
     * @return int
     *  the port number of the destination host.
     */
    public int getPort()
    {
        return m_port;
    }

    /**
     * Returns the threshold for writes to a clogged
     * sink with this socket.
     * @since May 23, 2002
     * 
     * @return int
     *  the threshold for writes to a clogged sink with 
     *  this socket.
     */
    public int getWriteClogThreshold()
    {
        return m_writeClogThreshold;
    }

}