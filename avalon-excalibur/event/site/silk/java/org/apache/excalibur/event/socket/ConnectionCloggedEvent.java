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

package org.apache.excalibur.event.socket;

/**
 * This event indicates that a connection was clogged 
 * when trying to process the given element. A sink 
 * is considered clogged if it is full (that is, its 
 * length threshold has been reached), or some other 
 * condition is preventing the given element from being 
 * serviced. 
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class ConnectionCloggedEvent
{
    /** The connection which clogged. */
    private final AsyncConnection m_connection;

    /** The element which clogged. */
    private final Object m_element;

    //------------------------ ConnectionCloggedEvent constructors
    /**
     * Create a new ConnectionCloggedEvent with the given 
     * connection and element.
     * @since May 23, 2002
     * 
     * @param connection
     *  The connection that got clogged.
     * @param element
     *  The clogging element.
     */
    public ConnectionCloggedEvent(AsyncConnection connection, Object element)
    {
        m_connection = connection;
        m_element = element;
    }

    //------------------------ ConnectionCloggedEvent specific implementation
    /**
     * Returns the clogged connection.
     * @since May 23, 2002
     * 
     * @return {@link AsyncConnection}
     *  The clogged connection
     */
    public AsyncConnection getConnection()
    {
        return m_connection;
    }

    /**
     * Returns the element that clogged.
     * @since May 23, 2002
     * 
     * @return {@link Object}
     *  The element that clogged the connection
     */
    public Object getElement()
    {
        return m_element;
    }

}