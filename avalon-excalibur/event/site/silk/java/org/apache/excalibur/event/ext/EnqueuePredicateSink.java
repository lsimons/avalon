/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;



/**
 * A Sink implements the 'sink' end of a finite-length event m_sink: 
 * it supports enqueue operations only. These operations can throw a
 * SinkException if the sink is closed or becomes full, allowing event
 * queues to support thresholding and backpressure.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface EnqueuePredicateSink
{
    /**
     * Set the enqueue predicate for this sink. This mechanism 
     * allows user to define a method that will 'screen' 
     * QueueElementIF's during the enqueue procedure to either 
     * accept or reject them. The enqueue predicate runs in the 
     * context of the caller of {@link #enqueue(QueueElement)},
     * which means it must be simple and fast. This can be used
     * to implement many interesting m_sink-thresholding policies, 
     * such as simple count threshold, credit-based mechanisms, 
     * and more.
     * @since May 14, 2002
     * 
     * @param enqueuePredicate
     *  the enqueue predicate for this sink
     */
    public void setEnqueuePredicate(EnqueuePredicate enqueuePredicate);

    /**
     * Return the enqueue predicate for this sink.
     * @since May 14, 2002
     * 
     * @return {@link EnqueuePredicate}
     *  the enqueue predicate for this sink.
     */
    public EnqueuePredicate getEnqueuePredicate();
}