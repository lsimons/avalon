/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;

/**
 * The DefaultQueueTimer class provides a mechanism for registering timer 
 * events that will go off at some future time. The future time can 
 * be specified in absolute or relative terms. When the timer goes off, 
 * an element is placed on a specified queue. Events will be delivered 
 * guaranteed, but the time that they are delivered may slip depending 
 * on how loaded the system is.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultQueueTimer extends AbstractLogEnabled
    implements QueueTimer, Startable, Configurable
{
    /** The timer that this implementation is based on */
    private Timer m_timer = null;
    
    /** The retry delay in ms. Default value is <m_code>-1 (= no retry)</m_code> */
    private long m_retryDelay = -1;

    //------------------------- Timer implementation
    /**
     * @see QueueTimer#registerTrigger(long, Object, Sink)
     */
    public QueueTrigger registerTrigger(long time, Object queueElement, Sink sink)
    {
        final DefaultTrigger trigger = new DefaultTrigger(queueElement, sink);
        m_timer.schedule(trigger.getTask(), time);
        return trigger;
    }

    /**
     * @see QueueTimer#registerTrigger(Date, Object, Sink)
     */
    public QueueTrigger registerTrigger(Date date, Object queueElement, Sink sink)
    {
        final DefaultTrigger trigger = new DefaultTrigger(queueElement, sink);
        m_timer.schedule(trigger.getTask(), date);
        return trigger;
    }

    //------------------------ Configurable implementation
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration)
    {
        m_retryDelay = 
            configuration.getAttributeAsLong("retry-delay", m_retryDelay);
    }
    
    //------------------------ Startable implementation
    /**
     * @see Startable#start()
     */
    public void start() throws Exception
    {
        stop();
        m_timer = new Timer();
    }

    /**
     * @see Startable#stop()
     */
    public void stop() throws Exception
    {
        if(m_timer != null)
        {
            m_timer.cancel();
        }
        m_timer = null;
    }
    
    //------------------------ DefaultQueueTimer inner classes
    /**
     * The Task class is an implementation of the the abstract
     * timer task class. It delivers a queue element to a specified
     * sink. It reschedules the delivery if the attempt does not
     * succeed and the re-try delay is greater than -1.
     * @since Sep 17, 2002
     * 
     * @author <a href = "mailto:mschier@earthlink.net">schierma</a>
     */
    private final class EventDeliveryTask extends TimerTask
    {
        /** The queue element to be enqueued */
        private final Object m_queueElement;

        /** The sink to enqueue to */
        private final Sink m_sink;
        
        //------------------------ EventDeliveryTask constructors
        /**
         * Constructs a task object from the queue element to 
         * be enqueued and the sink to enqueue the element on.
         * @since May 16, 2002
         * 
         * @param queueElement
         *  the queue element to be enqueued
         * @param sink
         *  The sink to enqueue the element to
         */
        public EventDeliveryTask(Object queueElement, Sink sink)
        {
            super();
            m_queueElement = queueElement;
            m_sink = sink;
        }
        
        //------------------------ Runnable implementation
        /**
         * @see Runnable#run()
         */
        public void run()
        {
            try
            {
                m_sink.enqueue(m_queueElement);
            }
            catch(SinkException e)
            {
                if(m_retryDelay > -1)
                {
                    if(getLogger().isDebugEnabled())
                    {
                        getLogger().debug("Reschedule delivery of event.", e);
                    }
                    // re-schedule
                    m_timer.schedule(this, m_retryDelay);
                }
                else
                {
                    if(getLogger().isErrorEnabled())
                    {
                        getLogger().error("Could not deliver timed event.", e);
                    }
                }    
            }
            
        }
        
        //------------------------ EventDeliveryTask specific implementation
        /**
         * Returns the Queue element that should be inserted
         * into the queue.
         * @since May 16, 2002
         * 
         * @return Object
         *  the queue element to be enqueued at the specific time.
         */
        final Object getQueueElement()
        {
            return m_queueElement;
        }

        /**
         * Returns the sink where the element is inserted.
         * @since May 16, 2002
         * 
         * @return Sink
         *  the sink where the element is inserted
         */
        final Sink getSink()
        {
            return m_sink;
        }

    } //-- end inner class EventDeliveryTask

    /**
     * The QueueTrigger class is an entry for the timer list and 
     * also implements the trigger interface that allows a 
     * client to cancel the event.
     * @since May 16, 2002
     * 
     * @author <a href = "mailto:mschier@earthlink.net">schierma</a>
     */
    private final class DefaultTrigger implements QueueTrigger
    {
        /** The queue element to be enqueued */
        private final TimerTask m_task;
        
        //------------------------ QueueTrigger constructors
        /**
         * Constructs a trigger object from the delay time
         * the queue element to be enqueued and the sink to
         * enqueue the element on.
         * @since May 16, 2002
         * 
         * @param time
         *  the delay time in milliseconds
         * @param queueElement
         *  the queue element to be enqueued
         * @param sink
         *  The sink to enqueue the element to
         */
        public DefaultTrigger(Object queueElement, Sink sink)
        {
            m_task = new EventDeliveryTask(queueElement, sink);
        }
        
        //------------------------ QueueTrigger implementation
        /**
         * @see QueueTrigger#cancel()
         */
        public void cancel()
        {
            m_task.cancel();
        }

        /**
         * Returns the timer task implementation for the trigger.
         * @since Sep 17, 2002
         * 
         * @return TimerTask
         *  The timer task implementation for the trigger.
         */
        public TimerTask getTask()
        {
            return m_task;
        }

    } //-- end DefaultTrigger inner class

}