/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.excalibur.event.Sink;

/**
 * This enqueue predicate implements a simple threshold for the
 * size of the sink.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class ThresholdPredicate implements EnqueuePredicate
{
//    /**
//     * The sink for which the predicate is in place.
//     */
//    private final Sink m_sink;
//
    /** The enqueue threshold for the sink. */
    private int m_threshold;

    //---------------------- ThresholdPredicate constructors
    /**
     * Create a new ThresholdPredicate for the given 
     * sink. This predicate has no threshold.
     * @since May 15, 2002
     * 
     * @param sink
     *  The sink associated with this predicate
     */
    public ThresholdPredicate()
    {
        this(-1);
    }

    /**
     * Create a new ThresholdPredicate for the given 
     * sink and threshold. A threshold of <m_code>-1</m_code> 
     * indicates no threshold.
     * @since May 15, 2002
     * 
     * @param sink
     *  The sink associated with this predicate
     * @param threshold
     *  The threshold for the enqueue operation
     */
    public ThresholdPredicate(int threshold)
    {
        m_threshold = threshold;
    }

    //---------------------- EnqueuePredicate implementation
    /**
     * @see EnqueuePredicate#accept(Object, Sink)
     */
    public boolean accept(Object element, Sink sink)
    {
        if (m_threshold == -1)
        {
            return true;
        }

        if (sink.size() >= m_threshold)
        {
            return false;
        }
        return true;
    }

    //---------------------- ThresholdPredicate specific implementation
    /**
     * Returns the current m_sink threshold.
     * @since May 15, 2002
     * 
     * @return int
     *  the current m_sink threshold
     */
    public final int getThreshold()
    {
        return m_threshold;
    }

    /**
     * Allows to set the current m_sink threshold. A m_sink 
     * threshold of <m_code>-1</m_code> indicates an infinite 
     * threshold.
     * @since May 15, 2002
     * 
     * @param threshold
     *  the current m_sink threshold. 
     */
    public final void setThreshold(int threshold)
    {
        m_threshold = threshold;
    }

}