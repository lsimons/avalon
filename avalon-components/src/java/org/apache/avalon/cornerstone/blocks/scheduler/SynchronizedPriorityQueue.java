/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.scheduler;

import java.util.NoSuchElementException;

/**
 * A thread safe version of the PriorityQueue.
 * Provides synchronized wrapper methods for all the methods
 * defined in the PriorityQueue interface.
 *
 * @author <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/12 00:06:37 $
 * @since 4.0
 */
final class SynchronizedPriorityQueue
    implements PriorityQueue
{
    private final PriorityQueue m_priorityQueue;

    public SynchronizedPriorityQueue( final PriorityQueue priorityQueue )
    {
        if( null == priorityQueue )
        {
            throw new NullPointerException( "priorityQueue" );
        }
        m_priorityQueue = priorityQueue;
    }

    /**
     * Clear all elements from queue.
     */
    public void clear()
    {
        synchronized( m_priorityQueue )
        {
            m_priorityQueue.clear();
        }
    }

    /**
     * Test if queue is empty.
     *
     * @return true if queue is empty else false.
     */
    public boolean isEmpty()
    {
        synchronized( m_priorityQueue )
        {
            return m_priorityQueue.isEmpty();
        }
    }

    /**
     * Insert an element into queue.
     *
     * @param element the element to be inserted
     */
    public void insert( final Object element )
    {
        synchronized( m_priorityQueue )
        {
            m_priorityQueue.insert( element );
        }
    }

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    public Object peek() throws NoSuchElementException
    {
        synchronized( m_priorityQueue )
        {
            return m_priorityQueue.peek();
        }
    }

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    public Object pop() throws NoSuchElementException
    {
        synchronized( m_priorityQueue )
        {
            return m_priorityQueue.pop();
        }
    }

    public String toString()
    {
        synchronized( m_priorityQueue )
        {
            return m_priorityQueue.toString();
        }
    }
}

