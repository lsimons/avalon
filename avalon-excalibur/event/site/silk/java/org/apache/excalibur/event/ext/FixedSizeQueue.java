/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.SinkFullException;

/**
 * This implementation is based on a fixed size element array.
 * It can only accept the size of elements it can hold and will
 * reject any further enqueue operations.
 * 
 * @todo Add blocking m_code to dequeue operation
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public final class FixedSizeQueue extends AbstractQueue
{
    /**
     * The element array holding the event elements
     */
    private final Object[] m_elements;

    /**
     * A mutex to synchronize operation on.
     */
    private final Mutex m_mutex;

    private int m_start = 0;
    private int m_end = 0;
    private int m_reserve = 0;

    //---------------------- FixedSizeQueue constructors
    /**
     * Default constructor that sets the m_sink's length to 
     * a default value of <m_code>100</m_code>
     * @since May 15, 2002
     * 
     * @param tag
     *  A name for the queue for debugging
     */
    public FixedSizeQueue(String tag)
    {
        this(tag, 100);
    }

    /**
     * Constructor that takes as an argument the fixed 
     * length of this m_sink.
     * @since May 15, 2002
     * 
     * @param tag
     *  A name for the queue for debugging
     * @param size
     *  The size of this fixed size m_sink.
     */
    public FixedSizeQueue(String tag, int size)
    {
        super(tag);
        m_elements = new Object[size + 1];
        m_mutex = new Mutex();
    }

    //------------------------- Sink implementation
    /**
     * @see Sink#size()
     */
    public int size()
    {
        int size = 0;

        if (m_end < m_start)
        {
            size = maxSize() - m_start + m_end;
        }
        else
        {
            size = m_end - m_start;
        }

        return size;
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

            if (elements.length + m_reserve + size() > maxSize())
            {
                throw new SinkFullException("Not enough room to enqueue these elements.");
            }

            // test the elegibility of the elements with the predicate
            checkEnqueuePredicate(elements);

            enqueue = new FixedSizePreparedEnqueue(this, elements);
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.release();
        }

        return enqueue;
    }

    /**
     * @see org.apache.excalibur.event.seda.event.Sink#tryEnqueue(Object)
     */
    public boolean tryEnqueue(final Object element)
    {
        boolean success = false;

        try
        {
            m_mutex.acquire();

            if (1 + m_reserve + size() > maxSize())
            {
                return false;
            }

            // test the elegibility of the element with the predicate
            if (getEnqueuePredicate() != null
                && !getEnqueuePredicate().accept(element, this))
            {
                return false;
            }

            addElement(element);
            success = true;
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.release();
        }

        return success;
    }

    /**
     * @see org.apache.excalibur.event.seda.event.Sink#enqueue(Object[])
     */
    public void enqueue(final Object[] elements) throws SinkException
    {
        final int len = elements.length;

        try
        {
            m_mutex.acquire();
            if (elements.length + m_reserve + size() > maxSize())
            {
                throw new SinkFullException("Not enough room to enqueue these elements.");
            }

            // test the elegibility of the elements with the predicate
            checkEnqueuePredicate(elements);

            for (int i = 0; i < len; i++)
            {
                addElement(elements[i]);
            }
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.release();
        }
    }

    /**
     * @see org.apache.excalibur.event.seda.event.Sink#enqueue(Object)
     */
    public void enqueue(final Object element) throws SinkException
    {
        try
        {
            m_mutex.acquire();
            if (1 + m_reserve + size() > maxSize())
            {
                throw new SinkFullException("Not enough room to enqueue these elements.");
            }

            // test the elegibility of the element with the predicate
            checkEnqueuePredicate(new Object[] { element });

            addElement(element);
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.release();
        }
    }

    //------------------------- Source implementation
    /**
     * @see org.apache.excalibur.event.seda.event.Source#dequeue(int)
     */
    public Object[] dequeue(final int numElements)
    {
        int arraySize = numElements;

        if (size() < numElements)
        {
            arraySize = size();
        }

        Object[] elements = null;

        try
        {
            m_mutex.attempt(m_timeout);

            if (size() < numElements)
            {
                arraySize = size();
            }

            elements = new Object[arraySize];

            for (int i = 0; i < arraySize; i++)
            {
                elements[i] = removeElement();
            }
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.release();
        }

        return elements;
    }

    /**
     * @see org.apache.excalibur.event.seda.event.Source#dequeue(int)
     */
    public Object[] dequeueAll()
    {
        Object[] elements = null;

        try
        {
            m_mutex.attempt(m_timeout);

            elements = new Object[size()];

            for (int i = 0; i < elements.length; i++)
            {
                elements[i] = removeElement();
            }
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.release();
        }

        return elements;
    }

    /**
     * @see org.apache.excalibur.event.seda.event.Source#dequeue()
     */
    public Object dequeue()
    {
        Object element = null;

        try
        {
            m_mutex.attempt(m_timeout);

            if (size() > 0)
            {
                element = removeElement();
            }
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.release();
        }

        return element;
    }

    //------------------------- overridden methods in AbstractQueue
    /**
     * @see AbstractQueue#maxSize()
     */
    public int maxSize()
    {
        return m_elements.length;
    }

    //------------------------ FixedSizeQueue specific implementation
    /**
     * Allows to add an element to the internal array
     * of elements backing this queue.
     * @since May 15, 2002
     * 
     * @param element
     *  The element to be added.
     */
    private final void addElement(Object element)
    {
        m_elements[m_end] = element;

        m_end++;
        if (m_end >= maxSize())
        {
            m_end = 0;
        }
    }

    /**
     * Allows to remove an element from the internal array
     * of elements backing this m_sink.
     * @since May 15, 2002
     * 
     * @return element
     *  The element that was removed.
     */
    private final Object removeElement()
    {
        Object element = m_elements[m_start];

        if (null != element)
        {
            m_elements[m_start] = null;

            m_start++;
            if (m_start >= maxSize())
            {
                m_start = 0;
            }
        }

        return element;
    }

    //------------------------ FixedSizeQueue inner classes
    /**
     * This is the FixedSizeQueue specific implementation of
     * the {@link PreparedEnqueue} interface.
     * @since May 15, 2002
     * 
     * @author <a href = "mailto:mschier@earthlink.net">schierma</a>
     */
    private static final class FixedSizePreparedEnqueue
        implements PreparedEnqueue
    {
        /**
         * A reference to the owning m_sink object
         */
        private final FixedSizeQueue m_parent;

        /**
         * The elements that must be enqueued
         */
        private Object[] m_elements;

        //-------------------------- FixedSizePreparedEnqueue constructors
        /**
         * This constructor creates a FixedSizePreparedEnqueue
         * using the passed in reference to the issueing m_sink
         * and the elements that should be enqueued.
         * @since May 15, 2002
         * 
         * @param parent
         *  The reference to the parent m_sink
         * @param elements
         *  The elements to be enqueued
         */
        private FixedSizePreparedEnqueue(
            FixedSizeQueue parent,
            Object[] elements)
        {
            m_parent = parent;
            m_elements = elements;
        }

        //-------------------------- PreparedEnqueue implementation
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
    } //-- end inner class FixedSizePreparedEnqueue
}