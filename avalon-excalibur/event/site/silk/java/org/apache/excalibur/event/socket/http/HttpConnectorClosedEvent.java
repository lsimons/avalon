/* 
 * Copyright (c) 2001 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;

/**
 * This event indicates that a HttpConnector has closed, either 
 * intentionally by the application, or unintentionally,
 * due to an error condition. A HttpConnector is considered 
 * closed when it is no longer being serviced.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class HttpConnectorClosedEvent
{
    /** The connection which was closed. */
    private final HttpConnector m_connector;

    //-------------------------- HttpConnectorClosedEvent constructors
    /**
     * Create a new HttpConnectorClosedEvent with the given 
     * connection.
     * @since May 23, 2002
     * 
     * @param AsyncConnection
     *  The connection which was closed.
     */
    public HttpConnectorClosedEvent(HttpConnector connector)
    {
        m_connector = connector;
    }

    //-------------------------- HttpConnectorClosedEvent specific implementation
    /**
     * Returns the closed connector.
     * @since May 23, 2002
     * 
     * @return {@link HttpConnector}
     *  The connector which was closed.
     */
    public HttpConnector getConnector()
    {
        return m_connector;
    }
}