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

/**
 * A DisconnectEvent object will be passed to the 
 * Sink associated with a DefaultUdpSocket when the socket 
 * successfully disconnects.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DisconnectEvent
{
    /** The udp socket that disconnected. */
    private final AsyncDatagramSocket m_udpSocket;

    //------------------------- DisconnectEvent constructors
    /**
     * Constructs an event based on the udp socket that
     * is disconnected.
     * @since May 21, 2002
     * 
     * @param udpSocket
     *  The upd socket that got disconnected.
     */
    public DisconnectEvent(AsyncDatagramSocket udpSocket)
    {
        m_udpSocket = udpSocket;
    }

    //------------------------- DisconnectEvent specific implementation
    /**
     * Returns the Udp Socket for which the disconnect 
     * succeeded.
     * @since May 21, 2002
     * 
     * @return {@link AsyncDatagramSocket}
     *  the Udp Socket for which the disconnect succeeded.
     */
    public AsyncDatagramSocket getSocket()
    {
        return m_udpSocket;
    }
}