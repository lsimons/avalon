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

import org.apache.excalibur.event.Sink;

/**
 * A Buffer is a Queue Element which represents a 
 * memory buffer.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class Buffer
{
    /** The data associated with this Buffer. */
    private final byte m_data[];

    /** The offet into the data associated with this Buffer. */
    private final int m_offset;

    /** The completion queue associated with this buffer. */
    private Sink m_completionQueue;

    /** 
     * A user-defined tag object associated with this 
     * buffer. can be used as a back-pointer from the 
     * buffer to application state, e.g., for handling 
     * completions.
     */
    private Object m_userTag;

    /**
     * The size of the data associated with this 
     * Buffer. May not be equal to data.length; 
     * may be any value less than or equal to 
     * (m_data.length - m_offset).
     */
    private final int m_size;

    //-------------------------- Buffer constructors
    /**
     * Create a Buffer with the given data, an offset
     * of 0, and a size of data.length.
     * @since Aug 26, 2002
     * 
     * @param data
     *  The byte array data representing this buffer
     */
    public Buffer(byte data[])
    {
        this(data, 0, data.length, null);
    }

    /**
     * Create a Buffer with the given data, an offset 
     * of 0, and a size of data.length, with the given 
     * completion queue.
     * @since Aug 26, 2002
     * 
     * @param data
     *  The buffer element data
     * @param completionQueue
     *  The queue in which events are enqueued when
     *  the data was processed.
     */
    public Buffer(byte data[], Sink completionQueue)
    {
        this(data, 0, data.length, completionQueue);
    }

    /**
     * Create a Buffer with the given data, offset, 
     * and size.
     * @since Aug 26, 2002
     * 
     * @param data
     *  The buffer element data
     * @param offset
     *  An offset into the data byte array
     * @param size
     *  The size of the buffer element.
     */
    public Buffer(byte data[], int offset, int size)
    {
        this(data, offset, size, null);
    }

    /**
     * Create a Buffer with the given data, 
     * offset, size, and completion queue.
     * @since Aug 26, 2002
     * 
     * @param completionQueue
     *  The queue in which events are enqueued when
     *  the data was processed.
     * @param data
     *  The buffer element data
     * @param offset
     *  An offset into the data byte array
     * @param size
     *  The size of the buffer element.
     */
    public Buffer(
        byte data[],
        int offset,
        int size,
        Sink completionQueue)
    {
        m_data = data;
        
        if ((offset >= data.length) || (size > (data.length - offset)))
        {
            throw new IllegalArgumentException(
                "Buffer created with invalid offset and/or size (off="
                    + offset
                    + ", size="
                    + size
                    + ", data.length="
                    + data.length
                    + ")");
        }
        
        m_offset = offset;
        m_size = size;
        m_completionQueue = completionQueue;
    }

    /**
     * Create a Buffer with a new data array of the 
     * given size.
     * @since Aug 26, 2002
     * 
     * @param size
     *  The size of the buffer data array
     */
    public Buffer(int size)
    {
        this(new byte[size], 0, size, null);
    }

    //------------------------ Buffer specific implementation
    /**
     * Returns the queue in which events are enqueued when
     * the data was processed.
     * @since Aug 26, 2002
     * 
     * @return Sink
     *  The queue in which events are enqueued when the data 
     *  was processed.
     */
    public Sink getCompletionQueue()
    {
        return m_completionQueue;
    }
    
    /**
     * Allows to set the queue in which events are enqueued when
     * the data was processed.
     * @since Aug 26, 2002
     * 
     * @param completionQueue
     *  The queue in which events are enqueued when the data 
     *  was processed.
     */
    public void setCompletionQueue(Sink completionQueue)
    {
        m_completionQueue = completionQueue;
    }


    /**
     * Returns the internal byte array data for this buffer.
     * @since Aug 26, 2002
     * 
     * @return byte[]
     *  The buffer byte array
     */
    public byte[] getData()
    {
        return m_data;
    }

    /**
     * Returns the offset into the data buffer
     * @since Aug 26, 2002
     * 
     * @return int
     *  The offset into the data buffer
     */
    public int getOffset()
    {
        return m_offset;
    }

    /**
     * Returns the size of the buffer element.
     * @since Aug 26, 2002
     * 
     * @return int
     *  The size of the buffer element.
     */
    public int getSize()
    {
        return m_size;
    }

    /**
     * Allows to set a user-defined tag object associated 
     * with this buffer that can be used as a back-pointer 
     * from the buffer to application state, e.g., for handling 
     * completions.
     * @since Aug 26, 2002
     * 
     * @param userTag
     *  A user-defined tag object associated with this buffer
     */
    public void setUserTag(Object userTag)
    {
        m_userTag = userTag;
    }

    /**
     * Returns a user-defined tag object associated with this 
     * buffer used as a back-pointer from the buffer to application 
     * state.
     * @since Aug 26, 2002 
     * 
     * @return Object
     *  A user-defined tag object associated with this buffer.
     */
    public Object getUserTag()
    {
        return m_userTag;
    }
}