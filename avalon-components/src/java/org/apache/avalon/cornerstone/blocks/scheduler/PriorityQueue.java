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
 * Iterface for priority queues.
 * This interface does not dictate whether it is min or max heap.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/12 00:06:37 $
 * @since 4.0
 */
interface PriorityQueue
{
    /**
     * Clear all elements from queue.
     */
    void clear();

    /**
     * Test if queue is empty.
     *
     * @return true if queue is empty else false.
     */
    boolean isEmpty();

    /**
     * Insert an element into queue.
     *
     * @param element the element to be inserted
     */
    void insert( Object element );

    /**
     * Return element on top of heap but don't remove it.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    Object peek() throws NoSuchElementException;

    /**
     * Return element on top of heap and remove it.
     *
     * @return the element at top of heap
     * @throws NoSuchElementException if isEmpty() == true
     */
    Object pop() throws NoSuchElementException;
}

