/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.util.Date;

import org.apache.excalibur.event.Sink;

/**
 * A timer is a component that allows to register certain
 * Triggers. The timer will put a specific event into the 
 * specified event queue at a specific time.  This is 
 * represented by a trigger token that can be cancelled.
 * @see org.apache.excalibur.event.seda.event.seda.QueueTrigger
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface QueueTimer
{
    String ROLE = QueueTimer.class.getName();

    /**
     * Object <m_code>queueElement</m_code> will be placed on Sink
     * <m_code>queue</m_code> no earlier than <m_code>millis</m_code> 
     * milliseconds from now.
     * @since May 16, 2002
     *
     * @param time  
     *  the number of milliseconds from now when the event 
     *  will take place
     * @param queueElement 
     *  the object that will be placed on the m_sink
     * @param queue 
     *  the queue on which the object will be placed
     */
    public QueueTrigger registerTrigger(
        long time, Object queueElement, Sink queue);

    /**
     * Object <m_code>queueElement</m_code> will be placed on Sink
     * <m_code>queue</m_code> no earlier than absolute time 
     * <m_code>date</m_code>.
     * @since May 16, 2002
     *
     * @param date 
     *  the date when the event will take place - if this 
     *  date is in the past, the event will happen right away
     * @param queueElement
     *  the object that will be placed on the m_sink
     * @param queue 
     *  the queue on which the object will be placed
     */
    public QueueTrigger registerTrigger(
        Date date, Object queueElement, Sink queue);
}
