/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

/**
 * A Sink implements the side of an event queue where QueueElements are
 * dequeued operations only.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Sink {
    /**
     * Sets the timeout on a blocking Sink.  Values above <code>1</code> will
     * force all <code>dequeue</code> operations to block for up to that number
     * of milliseconds waiting for new elements.  Values below <code>1</code>
     * will turn off blocking for Sink.  This is intentional because a Sink should
     * never block indefinitely.
     */
    void setTimeOut( long millis );

    /**
     * Dequeues the next element, or returns <code>null</code> if there is
     * nothing left on the queue.
     *
     * @return the next <code>QueueElement</code> on the queue
     */
    QueueElement dequeue();

    /**
     * Dequeues all available elements, or returns <code>null</code> if there is
     * nothing left on the queue.
     *
     * @return all pending <code>QueueElement</code>s on the queue
     */
    QueueElement[] dequeueAll();

    /**
     * Dequeues at most <code>num</code> available elements, or returns
     * <code>null</code> if there is nothing left on the queue.
     *
     * @return At most <code>num</code> <code>QueueElement</code>s on the queue
     */
    QueueElement[] dequeue(int num);

    /**
     * Returns the number of elements waiting in this queue.
     */
    int size();

}
