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
 * Enqueue predicates allow users to specify a method that 
 * will 'screen' elements being enqueued onto a sink, either 
 * accepting or rejecting them. This mechanism can be used 
 * to implement many interesting load-conditioning policies, 
 * for example, simple thresholding, rate control, credit-based 
 * flow control, and so forth. Note that the enqueue predicate 
 * runs in the context of the <b>caller of enqueue()</b>, which
 * means it must be simple and fast.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface EnqueuePredicate
{

    /**
     * Tests the given element for acceptance onto the m_sink.
     * @since May 8, 2002
     * 
     * @param element  
     *  The <m_code>QueueElement</m_code> to enqueue
     * @param context
     *  The sink that is the context for this predicate
     * @return 
     *  <m_code>true</m_code> if the sink accepts the element; 
     *  <m_code>false</m_code> otherwise.
     */
    public boolean accept(Object element, Sink context);

}