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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class allows you to treat a list of byte arrays as 
 * a single NonblockingInputStream. This is helpful for 
 * parsing data contained within network packets, where 
 * the m_payload for one ADU might be  spread across multiple 
 * packets. This is a *nonblocking* interface; if you attempt 
 * to read data from it, and none is available, it will
 * return immediately.
 * 
 * @author Matt Welsh
 */
public class BufferInputStream extends InputStream
{
    private static final int NUMARRAYS = 2;

    /** Indicates if this stream is closed */
    private boolean m_closed;

    /** Stores the current read offset */
    private int m_currentOffset;

    /** The read position in the current array */
    private int m_currentArray;

    /** Holds the underlying byte array array */
    private byte[] m_byteArrayArrays[];

    /** The position in the array that is pushed. */
    private int m_pushArray;

    /** The index of the array for the marker. */
    private int m_markArray;

    /** The current marked position for reset. */
    private int m_markOffset;

    //------------------------ MultiByteArrayInputStream constructors
    /**
     * Default constructor.  Creates an empty 
     * MultiByteArrayInputStream.
     * @since May 22, 2002)
     */
    public BufferInputStream()
    {
        m_byteArrayArrays = new byte[NUMARRAYS][];
        m_pushArray = 0;
        m_currentOffset = 0;
        m_currentArray = 0;
        m_markArray = -1;
        m_markOffset = -1;
        m_closed = false;
    }

    /**
     * Creates a MultiByteArrayInputStream with the given 
     * array of byte arrays.
     * @since May 22, 2002
     * 
     * @param byteArrayArray
     *  An array of byte arrays from which the stream reads
     */
    public BufferInputStream(byte[][] byteArrayArray)
    {
        m_byteArrayArrays = new byte[byteArrayArray.length + NUMARRAYS][];
        System.arraycopy(
            byteArrayArray,
            0,
            m_byteArrayArrays,
            0,
            byteArrayArray.length);
        m_pushArray = byteArrayArray.length;
        m_currentOffset = 0;
        m_currentArray = 0;
        m_markArray = -1;
        m_markOffset = -1;
        m_closed = false;
    }

    //------------------------ InputStream overriden methods and implmentation
    /**
     * @see InputStream#read()
     */
    public synchronized int read() throws IOException
    {
        if (m_closed)
        {
            throw new EOFException("MultiByteArrayInputStream is closed!");
        }

        if (m_currentArray == m_pushArray)
        {
            return -1;
        }
        else
        {
            int c = (int) m_byteArrayArrays[m_currentArray][m_currentOffset];
            m_currentOffset++;
            if (m_currentOffset == m_byteArrayArrays[m_currentArray].length)
            {
                m_currentOffset = 0;
                m_currentArray++;
            }
            return c;
        }
    }
    /**
     * @see InputStream#read(byte[])
     */
    public synchronized int read(byte b[]) throws IOException
    {
        if (m_closed)
        {
            throw new EOFException("MultiByteArrayInputStream is closed!");
        }
        return read(b, 0, b.length);
    }

    /**
     * @see InputStream#read(byte[], int, int)
     */
    public synchronized int read(byte b[], int off, int len) throws IOException
    {
        if (m_closed)
        {
            throw new EOFException("MultiByteArrayInputStream is closed!");
        }
        int n = off;
        int total = 0;
        int last = Math.min(off + len, b.length);

        if (m_currentArray == m_pushArray)
        {
            return -1;
        }

        while ((m_currentArray < m_byteArrayArrays.length)
            && (m_currentArray != m_pushArray)
            && (n < last))
        {
            int num_left =
                m_byteArrayArrays[m_currentArray].length - m_currentOffset;
            int tocopy = Math.min(num_left, last - n);
            System.arraycopy(
                m_byteArrayArrays[m_currentArray],
                m_currentOffset,
                b,
                n,
                tocopy);
            total += tocopy;
            n += tocopy;
            m_currentOffset += tocopy;
            if (m_currentOffset == m_byteArrayArrays[m_currentArray].length)
            {
                m_currentOffset = 0;
                m_currentArray++;
            }
        }
        return total;
    }

    /**
     * @see InputStream#skip(long)
     */
    public synchronized long skip(long n) throws IOException
    {
        if (m_closed)
        {
            throw new EOFException("MultiByteArrayInputStream is closed!");
        }
        int requested = Math.min((int) n, Integer.MAX_VALUE);
        int totalskipped = 0;

        if (m_currentArray == m_pushArray)
        {
            return 0;
        }

        while ((m_currentArray < m_byteArrayArrays.length) && (requested > 0))
        {
            int num_left =
                m_byteArrayArrays[m_currentArray].length - m_currentOffset;
            int toskip = Math.min(num_left, requested);
            totalskipped += toskip;
            requested -= toskip;
            m_currentOffset = 0;
            m_currentArray++;
        }
        return totalskipped;
    }

    /**
     * @see InputStream#available()
     */
    public synchronized int available() throws IOException
    {
        if (m_closed)
        {
            throw new EOFException("MultiByteArrayInputStream is closed!");
        }
        if (m_currentArray == m_pushArray)
        {
            return 0;
        }
        int num_left =
            m_byteArrayArrays[m_currentArray].length - m_currentOffset;
        for (int i = m_currentArray + 1; i < m_byteArrayArrays.length; i++)
        {
            if (m_byteArrayArrays[i] == null)
            {
                break;
            }
            num_left += m_byteArrayArrays[i].length;
        }
        return num_left;
    }

    /**
     * @see InputStream#close()
     */
    public synchronized void close() throws IOException
    {
        if (m_closed)
        {
            throw new EOFException("MultiByteArrayInputStream is closed!");
        }
        m_byteArrayArrays = null;
        m_closed = true;
    }

    /**
     * @see InputStream#markSupported()
     */
    public boolean markSupported()
    {
        return true;
    }

    /**
     * @see InputStream#reset()
     */
    public synchronized void reset() throws IOException
    {
        if (m_markArray == -1)
        {
            throw new IOException("MultiByteArrayInputStream not marked!");
        }
        m_currentArray = m_markArray;
        m_currentOffset = m_markOffset;
    }

    /**
     * @see InputStream#mark(int)
     */
    public synchronized void mark(int readlimit)
    {
        m_markArray = m_currentArray;
        m_markOffset = m_currentOffset;
    }

    //------------------------ MultiByteArrayInputStream specific implementation
    /**
     * Add an array to this MultiByteArrayInputStream.
     * @since May 22, 2002
     * 
     * @param byteArray
     *  The array to be added to this MultiByteArrayInputStream.
     */
    public synchronized void addArray(byte byteArray[])
    {
        m_byteArrayArrays[m_pushArray] = byteArray;
        m_pushArray++;

        if (m_pushArray == m_byteArrayArrays.length)
        {
            expandArrays();
        }
    }

    /**
     * Expands arrays when they become too long
     * @since May 22, 2002)
     */
    private void expandArrays()
    {
        byte[] oldarr[] = m_byteArrayArrays;
        m_byteArrayArrays = new byte[oldarr.length + NUMARRAYS][];
        System.arraycopy(oldarr, 0, m_byteArrayArrays, 0, oldarr.length);
    }

    /**
     * Read the next byte from this stream in non 
     * blocking fashion. Returns <m_code>-1</m_code> 
     * if no data is available.
     * @since May 22, 2002
     * 
     * @return int
     *  The next byte read from the stream;  
     *  <m_code>-1</m_code> if no data is available.
     * @throws IOException 
     *  If the stream is closed.
     */
    public synchronized int nonBlockingRead() throws IOException
    {
        if (m_closed)
            throw new EOFException("MultiByteArrayInputStream is closed!");
        int c;
        if (m_currentArray == m_pushArray)
        {
            return -1;
        }
        else
        {
            c = (int) m_byteArrayArrays[m_currentArray][m_currentOffset];
            m_currentOffset++;
            if (m_currentOffset == m_byteArrayArrays[m_currentArray].length)
            {
                m_currentOffset = 0;
                m_currentArray++;
            }
            return c;
        }
    }

    /**
     * Returns the number of bytes registered.
     * @since May 22, 2002
     * 
     * @return int
     *  the number of bytes registered.
     */
    public synchronized int numArrays()
    {
        return m_pushArray;
    }

    /**
     * Reset this input stream - clear all internal data 
     * and references to a fresh initialized state. 
     * @since May 22, 2002)
     */
    public synchronized void clear()
    {
        m_byteArrayArrays = new byte[NUMARRAYS][];
        m_pushArray = 0;
        m_currentOffset = 0;
        m_currentArray = 0;
        m_markArray = -1;
        m_markOffset = -1;
        m_closed = false;
    }
}