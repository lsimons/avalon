/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.scheduler;

import org.apache.avalon.cornerstone.services.scheduler.Target;
import org.apache.avalon.cornerstone.services.scheduler.TimeTrigger;

/**
 * Class use internally to package to hold scheduled time entries.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
final class TimeScheduledEntry
    implements Comparable
{
    protected final String       m_name;
    protected final TimeTrigger  m_trigger;
    protected final Target       m_target;

    //cached version of time from TimeTrigger class
    protected long               m_time;
    protected boolean            m_isValid;

    public TimeScheduledEntry( String name, TimeTrigger trigger, Target target )
    {
        m_name = name;
        m_trigger = trigger;
        m_target = target;
        //m_time = m_trigger.getTimeAfter( System.currentTimeMillis() );
        m_isValid = true;
    }

    /**
     * Return name of trigger.
     *
     * @return the name of trigger
     */
    public String getName()
    {
        return m_name;
    }

    public Target getTarget()
    {
        return m_target;
    }

    public TimeTrigger getTimeTrigger()
    {
        return m_trigger;
    }

    /**
     * Determine if this entry is valid
     *
     * @return true if trigger is valid, false otherwise
     */
    public boolean isValid()
    {
        return m_isValid;
    }

    /**
     * Invalidate trigger
     */
    public void invalidate()
    {
        m_isValid = false;
    }

    /**
     * Retrieve cached time when trigger should run next.
     *
     * @return the time in milliseconds when trigger should run
     */
    public long getNextTime()
    {
        return m_time;
    }

    /**
     * Set cached time in milliseconds when trigger should run
     *
     * @param time the time
     */
    public void setNextTime( long time )
    {
        m_time = time;
    }

    /**
     * Implement comparable interface used to help sort triggers.
     * Triggers are compared based on next time to run
     *
     * @param object the other trigger
     * @return -'ve value if other trigger occurs before this trigger
     */
    public int compareTo( final Object object )
    {
        final TimeScheduledEntry other = (TimeScheduledEntry)object;
        return (int)-(other.m_time - m_time);
    }

    public String toString()
    {
        return "TimeEntry[ name=" + m_name + " valid=" + m_isValid + " time=" + m_time;
    }
}

