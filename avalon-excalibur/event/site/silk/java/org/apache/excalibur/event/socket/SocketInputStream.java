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

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.apache.excalibur.event.socket.tcp.IncomingPacket;

/**
 * This is a utility class that allows you to push multiple 
 * {@link TcpIncomingPackets} in, and read bytes out as a 
 * stream. This is meant to be a convenience for performing 
 * packet processing using the socket interfaces. This class 
 * also takes care of reordering packets according to the
 * ATcpInPacket sequence number; that is, if multiple threads 
 * in a stage are receiving {@link TcpIncomingPackets} for 
 * the same connection, the SocketInputStream will internally 
 * reorder those packets.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class SocketInputStream extends BufferInputStream
{
    /** The Tree set collection to order the packets with. */
    private final TreeSet m_set;

    /** The next index sequence number. */
    private long m_nextIndex;

    //-------------------------- SocketInputStream constructors
    /**
     * Create an SocketInputStream with an initial 
     * sequence number of 1.
     * @since May 21, 2002)
     */
    public SocketInputStream()
    {
        super();
        m_set = new TreeSet(new IndexNumberComparator());
        m_nextIndex = 1;
    }

    /**
     * Create an SocketInputStream using the given 
     * initial sequence number.
     * @since May 21, 2002
     * 
     * @param initialIndex 
     *  The initial index number for the packets
     */
    public SocketInputStream(long initialIndex)
    {
        super();
        m_set = new TreeSet(new IndexNumberComparator());
        m_nextIndex = initialIndex;
    }

    //-------------------------- SocketInputStream specific implementation
    /**
     * Add a packet to this aSocketInputStream. Reorders 
     * packets internally so that bytes will be read from 
     * this InputStream according to the sequence number 
     * order of the packets.
     * @since May 21, 2002
     * 
     * @param packet
     *  The packet to be added
     */
    public synchronized void addPacket(IncomingPacket packet)
    {
        long sn = packet.getIndex();
        if (sn == 0)
        {
            // No sequence number -- assume it's in order, but 
            // don't increment the nextSeqNum
            addArray(packet.getBytes());
            return;
        }

        if (sn == m_nextIndex)
        {
            addArray(packet.getBytes());
            m_nextIndex++;
            // seqNum of 0 is special
            if (m_nextIndex == 0)
            {
                m_nextIndex = 1;
            }
        }
        else
        {
            // Assume out of order. Don't treat (sn < nextSeqNum)
            // differently than (sn > nextSeqNum), since we have
            // wraparound.
            m_set.add(packet);

            // Push any 'ready' outoforder elements
            try
            {
                IncomingPacket first = (IncomingPacket) m_set.first();
                while (first != null && first.getIndex() == m_nextIndex)
                {
                    m_set.remove(first);
                    addArray(first.getBytes());
                    m_nextIndex++;
                    // seqNum of 0 is special
                    if (m_nextIndex == 0)
                    {
                        m_nextIndex = 1;
                    }
                    first = (IncomingPacket) m_set.first();
                }
            }
            catch (NoSuchElementException e)
            {
                // Ignore
            }
        }
    }

    /**
     * Reinitialize the state of this input stream, 
     * clearing all internal data and pointers. The 
     * next sequence number will be preserved.
     * @since May 21, 2002)
     */
    public synchronized void clear()
    {
        super.clear();
        m_set.clear();
    }

    /**
     * Return the next expected sequence number.
     * @since May 21, 2002
     * 
     * @return long 
     *  the next expected sequence number.
     */
    public synchronized long getNextIndex()
    {
        return m_nextIndex;
    }

    //------------------------- SocketInputStream inner classes
    /**
     * Internal class used to reorder elements 
     * of {@link #m_set} according to sequence number
     */
    class IndexNumberComparator implements Comparator
    {
        /**
         * @see Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) throws ClassCastException
        {
            IncomingPacket p1 = (IncomingPacket) o1;
            IncomingPacket p2 = (IncomingPacket) o2;

            long sn1 = p1.getIndex();
            long sn2 = p2.getIndex();

            if (sn1 == sn2)
            {
                return 0;
            }
            if (sn1 < sn2)
            {
                return -1;
            }
            return 1;
        }
    }
}