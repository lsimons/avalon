/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services;

import java.util.Date;
import org.apache.avalon.Stoppable;
import org.apache.phoenix.Service;

/**
 * The <code>Scheduler</code> specifies the interface to a system for setting
 * and receiving alarms within Avalon. The <code>Scheduler</code> allows to set
 * a listener on a specific event.
 *
 * @author Federico Barbieri <fede@apache.org>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Scheduler 
    extends Service
{
    /**
     * Sets an alarm by name, callback (Target), and the class (EventTrigger) 
     * that trigger the event. For example:
     *   <code> setAlarm("wakeup", new Alarm(10*Alarm.MIN, "Wake up!", 
     *            Alarm.DAY), this); </code>
     * this line sets an alarm based on time (the event triggered by the class 
     * Alarm) to call <code> this </code> in 10 minunes from now and every day
     * after that.
     *
     * @param name          The name of the alarm
     * @param target        The callback for the alarm
     * @param eventTrigger  The class Event that trigger the desidered event.
     */
    void setAlarm( String name, EventTrigger eventTrigger, Target target );

    /**
     * Removes an alarm by name.  If the alarm does not exist a RuntimeException 
     * is thrown.
     *
     * @param name The name of the alarm
     */
    void removeAlarm( String name );

    /**
     * Resets the alarm specified.
     *
     * @param name The name of the alarm
     */
    void resetAlarm( String name );

    /**
     * The <code>TimeServer.Bell</code> is the interface that any consumer of the
     * <code>TimeServer</code> must implement to handle the event that the alarm
     * went off.  In other words, the <code>TimeServer.Bell</code> is the effect
     * of the alarm.
     */
    public interface Target 
    {
        /**
         * Wakes up the destination so that it receives the name of the alarm and
         * the memo.  The memo is not required by the <code>TimeServer</code> system,
         * and so it must have meaning with the implementer of the <code>TimerServer.Bell</code>.
         */
        void wake( String name, Event event );

    }

    public interface EventTrigger 
        extends Runnable, Stoppable
    {
        /**
         * This method is called by the scheduler just after creatin to init
         * the EventTrigger.
         *
         * @param name      Sets the name of this EventTrigger
         * @param target    Sets the target of this EventTrigger
         * @param parent    Sets the parent of this EventTrigger that must
         *                  be called to to notify the Scheduler that this 
         *                  EvenTrigger wish to be destroyed.
         */
        void initialize( String name, Target target, Scheduler parent );
        
        /**
         * This method is used to reset the EventTrigger. 
         * NOTE:
         * Is the reset method generic for any EventTrggers? In other words a
         * SystemEvent(SIG_TERM) EventTrigger can use the reset method? 
         */
        void reset();
    }
    
    public class Event 
    {
        private Object m_memo;
        private Object m_descriptor;
        
        protected Event( final Object memo )
        {
            m_memo = memo;
        }
        
        /**
         * This method is used by the target to retrive the memo.
         */
        public Object getMemo() 
        {
            return m_memo;
        }
        
        /**
         * This method is used by the target to retrive a decriptor of the event.
         */
        public Object getDescriptor() 
        {
            return m_descriptor;
        }
        
        protected void setDescriptor( final Object descriptor ) 
        {
            m_descriptor = descriptor;
        }
    }
    
/*    protected class AlarmServer extends Thread {
      private Vector waypoints;
        
      {
      waypoints = new Vector();
      this.start();
      }
        
      protected static addWaypoint(long time) {
*/          
        
    /**
     * This is an implementation of an EventTrigger that triggers time events. 
     * NOTE:
     * I don't like the idea of a class like this in the .services interfaces 
     * but since it must somehow be as public as the interface... 
     */
    public class Alarm 
        implements EventTrigger
    {
        private Target m_target;
        private long m_starttime, m_periodicity;
        private boolean m_stop, m_reset;
        private Event m_event;
        private String m_name;
        private Scheduler m_parent;
        
        // public memo for periodicity;
        public static final long SEC = 1000;
        public static final long MIN = 60 * SEC;
        public static final long HOUR = 60 * MIN;
        public static final long DAY = 24 * HOUR;
        public static final long MONTH = 30 * DAY; // 30.416 * DAY 
        public static final long YEAR = 365 * DAY;

        // constructors
        public Alarm( final long starttime )
        {
            this( starttime, null, 0 );
        }
        
        public Alarm( final long starttime, final Object memo )
        {
            this( starttime, memo, 0 );
        }
        
        public Alarm( final long starttime, final Object memo, final long periodicity )
        {
            if( starttime < 0 || periodicity < 0 ) 
            {
                throw new IllegalArgumentException( "starttime and periodicity must be " +
                                                    "both positive");
            }

            m_starttime = starttime;
            m_periodicity = periodicity;
            m_event = new Event( memo );
        }

        public Alarm( final Date startdate ) 
        {
            this( startdate, null, 0 );
        }
        
        public Alarm( final Date startdate, final Object memo ) 
        {
            this( startdate, memo, 0 );
        }

        public Alarm( final Date startdate, final Object memo, final long periodicity ) 
        {
            this( startdate.getTime() - new Date().getTime(), memo, periodicity );
        }
            
        public void initialize( final String name, final Target target, final Scheduler parent ) 
        {
            m_target = target;
            m_name = name;
            m_parent = parent;
        }

        public void run() 
        {
            boolean again = true;

            while( again ) 
            {
                synchronized( this ) 
                {
                    try { wait( m_starttime ); } 
                    catch( final InterruptedException ie ) {}
                    if( m_stop ) 
                    {
                        again = false;
                    }
                    else if( m_reset ) 
                    {
                        m_reset = false;
                    }
                    else 
                    {
                        m_event.setDescriptor( new Date() );
                        m_target.wake( m_name, m_event );
                        m_starttime = m_periodicity;
                        again = ( m_periodicity != 0 );
                        if( !again ) m_parent.removeAlarm( m_name );
                    }
                }
            }
        }

        public void reset() 
        {
            synchronized( this ) 
            {
                m_reset = true;
                notifyAll();
            }
        }
    
        public void stop() 
        {
            synchronized( this ) 
            {
                m_stop = true;
                notifyAll();
            }
        }
    }
}
