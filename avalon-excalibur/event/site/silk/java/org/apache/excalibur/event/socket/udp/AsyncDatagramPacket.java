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

package org.apache.excalibur.event.socket.udp;

import java.net.InetAddress;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.socket.Buffer;

/**
 * An AUdpPacket is an extension to Buffer that supports
 * specifying the destination address and port for a given packet.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class AsyncDatagramPacket extends Buffer
{
    /** The ip address associated with this packet */
    private final InetAddress m_inetAddress;

    /** The port number of the ip address. */
    private final int m_port;

    //--------------------------- AsyncDatagramPacket constructors
    /**
     * Create an AUdpPacket with the given data, an 
     * offset of <m_code>0</m_code>, and a size of 
     * <m_code>data.length</m_code>.
     * @since May 21, 2002
     * 
     * @param data
     *  The data of the packet
     */
    public AsyncDatagramPacket(byte data[])
    {
        this(data, 0, data.length, null, null, -1);
    }

    /**
     * Create an AUdpPacket with a new data array of the 
     * given size.
     * @since May 21, 2002
     * 
     * @param size
     *  The size of the packet
     */
    public AsyncDatagramPacket(int size)
    {
        this(new byte[size], 0, size, null, null, -1);
    }

    /**
     * Create an AUdpPacket with the given data, 
     * an offset of 0, and a size of data.length, with 
     * the given completion queue.
     * @since May 21, 2002
     * 
     * @param data
     *  The data of this packet
     * @param completionQueue
     *  The queue to enqueue after processing
     */
    public AsyncDatagramPacket(byte data[], Sink compQ)
    {
        this(data, 0, data.length, compQ, null, -1);
    }

    /**
     * Create an AUdpPacket with the given data, offset, 
     * and size.
     * @since May 21, 2002
     * 
     * @param data
     *  The data of this packet
     * @param offset
     *  The offset from which to read the data
     * @param size
     *  The length to read the data
     */
    public AsyncDatagramPacket(byte data[], int offset, int size)
    {
        this(data, offset, size, null, null, -1);
    }

    /**
     * Create an AUdpPacket with the given data, offset, 
     * size, and completion queue.
     * @since May 21, 2002
     * 
     * @param data
     *  The data of this packet
     * @param offset
     *  The offset from which to read the data
     * @param size
     *  The length to read the data
     * @param completionQueue
     *  The queue to enqueue after processing
     */
    public AsyncDatagramPacket(
        byte data[],
        int offset,
        int size,
        Sink completionQueue)
    {
        this(data, offset, size, completionQueue, null, -1);
    }

    /**
     * Create an AUdpPacket with the given data, offset, size, 
     * completion queue, destination address, and port.
     * @since May 21, 2002
     * 
     * @param data
     *  The data of this packet
     * @param offset
     *  The offset from which to read the data
     * @param size
     *  The length to read the data
     * @param completionQueue
     *  The queue to enqueue after processing
     * @param inetAddress
     *  The associated ip address
     * @param port
     *  The port for the ip address
     */
    public AsyncDatagramPacket(
        byte data[],
        int offset,
        int size,
        Sink completionQueue,
        InetAddress inetAddress,
        int port)
    {
        super(data, offset, size, completionQueue);
        m_inetAddress = inetAddress;
        m_port = port;
    }

    //--------------------------- AsyncDatagramPacket specific implementation
    /**
     * Returns the destination address. Returns <m_code>null</m_code>
     * if not set.
     * @since May 21, 2002
     * 
     * @return {@link InetAddress}
     *  the destination address, <m_code>null</m_code> if not set.
     */
    public final InetAddress getAddress()
    {
        return m_inetAddress;
    }

    /**
     * Returns the destination port. Returns <m_code>-1</m_code>
     * if not set.
     * @since May 21, 2002
     * 
     * @return int
     *  the destination port number, <m_code>-1</m_code> if not set.
     */
    public final int getPort()
    {
        return m_port;
    }

}