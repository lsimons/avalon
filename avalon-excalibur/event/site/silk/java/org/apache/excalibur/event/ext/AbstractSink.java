/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkFullException;

/**
 * An abstract base implementation of a sink. It provides 
 * an implementation of the set and get method for the 
 * sink's enqueue predicate.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class AbstractSink implements EnqueuePredicateSink, Sink
{
    /** The sink's enqueue predicate used to accept elements */
    private EnqueuePredicate m_enqueuePredicate = null;

    //----------------------- AbstractSink consttructor
    /**
     * @see Object#Object()
     */
    public AbstractSink()
    {
        super();
    }

    /**
     * Constructor that takes as an argument the predicate
     * for this m_sink.
     * @since May 16, 2002
     * 
     * @param {@link EnqueuePredicate}
     *  The enqueue predicate for this m_sink.
     */
    public AbstractSink(EnqueuePredicate enqueuePredicate)
    {
        super();
        m_enqueuePredicate = enqueuePredicate;
    }

    //----------------------- Sink implementation
    /**
     * @see Sink#getEnqueuePredicate()
     */
    public final EnqueuePredicate getEnqueuePredicate()
    {
        return m_enqueuePredicate;
    }

    /**
     * @see Sink#setEnqueuePredicate(EnqueuePredicate)
     */
    public final void setEnqueuePredicate(EnqueuePredicate enqueuePredicate)
    {
        m_enqueuePredicate = enqueuePredicate;
    }

    //----------------------- AbstractSink specific implementation
    /**
     * Checks the elements if they can be enqueued using 
     * the predicate.  Returns immediately if no predicate 
     * is set for the sink.
     * @since May 15, 2002
     * 
     * @param elements 
     *  The {@link QueueElement}s to check for elegibility.
     * @throws SinkFullException
     *  If the predicate does not allow the object to be 
     *  enqueued.
     */
    protected final void checkEnqueuePredicate(Object[] elements)
        throws SinkFullException
    {
        if (getEnqueuePredicate() == null)
        {
            // no predicate set so just return;
            return;
        }

        for (int i = 0; i < elements.length; i++)
        {
            if (!getEnqueuePredicate().accept(elements[i], this))
            {
                throw new SinkFullException(
                    "Predicate does not allow to enqueue these elements.");
            }
        }
    }

}
