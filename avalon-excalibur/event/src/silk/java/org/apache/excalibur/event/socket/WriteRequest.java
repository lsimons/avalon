/* 
 * Copyright (c) 2000 by Matt Welsh and The Regents of the University of 
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
 * Request to write data to a connection.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class WriteRequest
{
    /** The socket state of the connection */
    private final ReadWriteSocketState m_socketState;

    /** The connection to write to. */
    private final AsyncConnection m_connection;

    /** The data to write to the socket */
    private final Buffer m_bufferElement;

    //------------------------- UdpWriteRequest constructors
    /**
     * Constructor that creates this request based
     * on the connection and buffer element data to 
     * be written.
     * @since May 21, 2002
     * 
     * @param connection
     *  The connection to write to.
     * @param socketState
     *  The internal socket state of the connection
     * @param bufferElement
     *  The buffer to write
     */
    public WriteRequest(
        AsyncConnection connection,
        ReadWriteSocketState socketState,
        Buffer bufferElement)
    {
        m_connection = connection;
        m_bufferElement = bufferElement;
        m_socketState = socketState;
    }

    //------------------------- WriteRequest specific implementation
    /**
     * Returns the connection element to write to.
     * @since May 21, 2002
     * 
     * @return {@link AsyncConnection}
     *  The connection element to write to.
     */
    public AsyncConnection getConnection()
    {
        return m_connection;
    }

    /**
     * Returns the buffered data to write to the socket.
     * @since May 21, 2002
     * 
     * @return {@link Buffer}
     *  The buffered data to write to the socket.
     */
    public Buffer getBufferElement()
    {
        return m_bufferElement;
    }

    /**
     * Returns the internal state of the socket 
     * which is used to read.
     * @since May 21, 2002
     * 
     * @return {@link ReadWriteSocketState}
     *  the internal state of the socket which is 
     *  used to write.
     */
    public ReadWriteSocketState getSocketState()
    {
        return m_socketState;
    }
}