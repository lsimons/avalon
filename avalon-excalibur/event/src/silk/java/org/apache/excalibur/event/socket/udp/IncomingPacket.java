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

package org.apache.excalibur.event.socket.udp;

import java.net.DatagramPacket;

/** 
 * An AUdpInPacket represents a packet which was received 
 * from a datagram socket. 
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class IncomingPacket
{

    /** The udp connection object. */
    private final AsyncUdpConnection m_connection;

    /** The datagram packet wrapped by this object. */
    private final DatagramPacket m_packet;

    /** The sequence index for this packet */
    private final long m_index;

    //--------------------------- IncomingPacket constructors
    /**
     * Constructor that takes a udp socket and a data gram
     * packet and creates this Wrapper object from it.
     * @since May 21, 2002
     * 
     * @param udpSocket
     *  The underlying udp socket object
     * @param packet
     *  The packet from the socket.
     */
    public IncomingPacket(AsyncUdpConnection connection, DatagramPacket packet)
    {
        this(connection, packet, 0);
    }

    /**
     * Constructor that takes a udp socket and a data gram
     * packet and creates this Wrapper object from it.
     * @since May 21, 2002
     * 
     * @param udpSocket
     *  The underlying udp socket object
     * @param packet
     *  The packet from the socket.
     * @param index
     *  An index number for this packet.
     */
    public IncomingPacket(
        AsyncUdpConnection connection,
        DatagramPacket packet,
        long index)
    {
        m_connection = connection;
        m_packet = packet;
        m_index = index;
    }

    //------------------------- IncomingPacket specific implementation
    /**
     * Return the sequence number associated with this packet.
     * Sequence numbers range from 1 to Long.MAX_VALUE, then 
     * wrap around to Long.MIN_VALUE. A sequence number of 0 
     * indicates that no sequence number was associated with 
     * this packet when it was created.
     * @since May 21, 2002
     * 
     * @return long
     *  The index number associated with this packet.
     */
    public long getIndex()
    {
        return m_index;
    }

    /**
     * Returns the connection from which this packet was 
     * received.
     * @since May 21, 2002
     * 
     * @return {@link AsyncUdpConnection}
     *  the connection from which this packet was received.
     */
    public AsyncUdpConnection getConnection()
    {
        return m_connection;
    }

    /** 
     * Returns the DatagramPacket represented by this 
     * queue element.
     * @since May 21, 2002
     * 
     * @return {@link DatagramPacket}
     *  the datagram packet represented by the element.
     */
    public DatagramPacket getPacket()
    {
        return m_packet;
    }

}