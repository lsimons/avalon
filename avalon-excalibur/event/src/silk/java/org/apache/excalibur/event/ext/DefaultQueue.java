/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.avalon.excalibur.collections.Buffer;
import org.apache.avalon.excalibur.collections.VariableSizeBuffer;
import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.SinkFullException;

/**
 * The default queue implementation is a variable size 
 * queue.  
 * @todo Add blocking m_code to dequeue operation
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public final class DefaultQueue extends AbstractQueue
{
    /** The underlying Buffer with queue elements. */
    private final Buffer m_elements;

    /** A mutex object to sychronize on. */
    private final Mutex m_mutex;

    private int m_reserve;
    private final int m_maxSize;

    //------------------------------- DefaultQueue constructors
    /**
     * A default constructor for a queue. The size of the 
     * m_sink is infinite.
     * @since May 15, 2002
     * 
     * @param tag
     *  A name for the queue for debugging
     */
    public DefaultQueue(String tag)
    {
        this(tag, -1);
    }

    /**
     * A constructor for a default queue that takes the size
     * of the queue as an integer.  Setting the size to 
     * <m_code>-1</m_code> makes the queue's length infinite.
     * @since May 15, 2002
     * 
     * @param tag
     *  A name for the queue for debugging
     * @param size
     *  The size of the queue. <m_code>-1</m_code> means infinite
     *  queue size.
     */
    public DefaultQueue(String tag, int size)
    {
        super(tag);

        int maxSize;

        if (size > 0)
        {
            m_elements = new VariableSizeBuffer(size);
            maxSize = size;
        }
        else
        {
            m_elements = new VariableSizeBuffer();
            maxSize = -1;
        }

        m_mutex = new Mutex();
        m_reserve = 0;
        m_maxSize = maxSize;
    }

    //------------------------- Sink implementation
    /**
     * @see Sink#size()
     */
    public int size()
    {
        return m_elements.size();
    }

    /**
     * @see Sink#prepareEnqueue(Object[])
     */
    public PreparedEnqueue prepareEnqueue(final Object[] elements)
        throws SinkException
    {
        PreparedEnqueue enqueue = null;

        try
        {
            m_mutex.acquire();
            try
            {

                if (maxSize() > 0
                    && elements.length + m_reserve + size() > maxSize())
                {
                    throw new SinkFullException("Not enough room to enqueue these elements.");
                }

                // test the elegibility of the elements with the predicate
                checkEnqueuePredicate(elements);

                enqueue = new DefaultPreparedEnqueue(this, elements);
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch (InterruptedException ie)
        {
        }

        return enqueue;
    }

    /**
     * @see Sink#tryEnqueue(Object)
     */
    public boolean tryEnqueue(final Object element)
    {
        boolean success = false;

        try
        {
            m_mutex.acquire();
            try
            {

                if (maxSize() > 0 && 1 + m_reserve + size() > maxSize())
                {
                    return false;
                }

                // test the elegibility of the element with the predicate
                if (getEnqueuePredicate() != null
                    && !getEnqueuePredicate().accept(element, this))
                {
                    return false;
                }

                m_elements.add(element);
                success = true;
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch (InterruptedException ie)
        {
        }

        return success;
    }

    /**
     * @see Sink#enqueue(Object[])
     */
    public void enqueue(final Object[] elements) throws SinkException
    {
        final int len = elements.length;

        try
        {
            m_mutex.acquire();
            try
            {
                if (maxSize() > 0
                    && elements.length + m_reserve + size() > maxSize())
                {
                    throw new SinkFullException("Not enough room to enqueue these elements.");
                }

                // test the elegibility of the elements with the predicate
                checkEnqueuePredicate(elements);

                for (int i = 0; i < len; i++)
                {
                    m_elements.add(elements[i]);
                }
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch (InterruptedException ie)
        {
        }
    }

    /**
     * @see Sink#enqueue(Object)
     */
    public void enqueue(final Object element) throws SinkException
    {
        try
        {
            m_mutex.acquire();
            try
            {
                if (maxSize() > 0 && 1 + m_reserve + size() > maxSize())
                {
                    throw new SinkFullException("Not enough room to enqueue this element.");
                }

                // test the elegibility of the elements with the predicate
                checkEnqueuePredicate(new Object[] { element });

                m_elements.add(element);
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch (InterruptedException ie)
        {
        }
    }

    //------------------------- Source implementation
    /**
     * @see Source#dequeue(int)
     */
    public Object[] dequeue(final int numElements)
    {
        getDequeueInterceptor().before(this);
        
        int arraySize = numElements;

        if (size() < numElements)
        {
            arraySize = size();
        }

        Object[] elements = null;

        try
        {
            if (m_mutex.attempt(m_timeout))
            {
                try
                {
                    if (size() < numElements)
                    {
                        arraySize = size();
                    }

                    elements = new Object[arraySize];

                    for (int i = 0; i < arraySize; i++)
                    {
                        elements[i] = (Object) m_elements.remove();
                    }
                }
                finally
                {
                    m_mutex.release();
                }
            }
        }
        catch (InterruptedException ie)
        {
        }
        
        getDequeueInterceptor().after(this);

        return elements;
    }

    /**
     * @see Source#dequeueAll()
     */
    public Object[] dequeueAll()
    {
        Object[] elements = null;

        getDequeueInterceptor().before(this);
        try
        {
            if (m_mutex.attempt(m_timeout))
            {
                try
                {
                    elements = new Object[size()];

                    for (int i = 0; i < elements.length; i++)
                    {
                        elements[i] = (Object) m_elements.remove();
                    }
                }
                finally
                {
                    m_mutex.release();
                }
            }
        }
        catch (InterruptedException ie)
        {
        }
        
        getDequeueInterceptor().after(this);

        return elements;
    }

    /**
     * @see Source#dequeue()
     */
    public Object dequeue()
    {
        Object element = null;

        getDequeueInterceptor().before(this);
        try
        {
            if (m_mutex.attempt(m_timeout))
            {
                try
                {
                    if (size() > 0)
                    {
                        element = (Object) m_elements.remove();
                    }
                }
                finally
                {
                    m_mutex.release();
                }
            }
        }
        catch (InterruptedException ie)
        {
        }
        
        getDequeueInterceptor().after(this);

        return element;
    }

    //------------------------- overridden methods in AbstractQueue
    /**
     * @see AbstractQueue#maxSize()
     */
    public int maxSize()
    {
        return m_maxSize;
    }

    //------------------------- DefaultQueue inner classes  
    /**
     * A default implementation of the {@link PreparedEnqueue}
     * interface.  This object is returned by the 
     * <m_code>prepareEnqueue</m_code> operations and is used to
     * commit and abort enqueue actions.
     * @since May 15, 2002
     * 
     * @author <a href = "mailto:mschier@earthlink.net">schierma</a>
     */
    private static final class DefaultPreparedEnqueue
        implements PreparedEnqueue
    {
        /**
         * The owning m_sink object.
         */
        private final DefaultQueue m_parent;

        /**
         * The elements to be enqueued.
         */
        private Object[] m_elements;

        //------------------------ DefaultPreparedEnqueue constructors
        /**
         * Constructor for a DefaultPreparedEnqueue object that
         * takes a reference to the parent m_sink and the elements
         * in the process to be enqueued.
         * @since May 15, 2002
         * 
         * @param parent
         *  The reference to the owning m_sink object
         * @param elements
         *  The elements to be enqueued
         */
        private DefaultPreparedEnqueue(
            DefaultQueue parent, Object[] elements)
        {
            m_parent = parent;
            m_elements = elements;
        }

        /**
         * @see PreparedEnqueue#commit()
         */
        public void commit()
        {
            if (null == m_elements)
            {
                throw new IllegalStateException("This PreparedEnqueue has already been processed!");
            }

            try
            {
                m_parent.enqueue(m_elements);
                m_parent.m_reserve -= m_elements.length;
                m_elements = null;
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Default enqueue did not happen--should be impossible");
                // will never happen
            }
        }

        /**
         * @see PreparedEnqueue#abort()
         */
        public void abort()
        {
            if (null == m_elements)
            {
                throw new IllegalStateException("This PreparedEnqueue has already been processed!");
            }

            m_parent.m_reserve -= m_elements.length;
            m_elements = null;
        }
    }
}