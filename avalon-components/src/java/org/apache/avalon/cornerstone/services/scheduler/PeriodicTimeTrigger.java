/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.scheduler;

/**
 * Goes off every <tt>period</tt> milliseconds after waiting for
 * <tt>offset</tt> milliseconds from the moment the trigger was
 * <tt>reset</tt>.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 */
public class PeriodicTimeTrigger
    implements TimeTrigger
{
    protected final long m_offset;
    protected final long m_period;
    private long m_triggerTime;

    /**
     * Creates a periodic trigger. It goes off the first time after
     * <tt>offset</tt> milliseconds from the time it was
     * <tt>reset</tt> and then every <tt>offset</tt>
     * milliseconds. The trigger is <tt>reset</tt> as
     * part of its construction.
     *
     * @param offset initial delay in milliseconds, -1 means fire immediately
     * @param period after initial delay in milliseconds, -1 means fire only once after initial delay
     */
    public PeriodicTimeTrigger( final int offset, final int period )
    {
        m_offset = offset;
        m_period = period;

        reset();
    }

    /**
     * Returns the next time after the given <tt>moment</tt> when
     * this trigger goes off.
     *
     * @param moment base point in milliseconds
     * @return the time in milliseconds when this trigger goes off
     */
    public long getTimeAfter( final long moment )
    {
        if( moment <= m_triggerTime )
            return m_triggerTime;
        else
        {
            if( -1 == m_period ) return -1;

            final long over = moment - m_triggerTime;
            final long remainder = over % m_period;

            return moment + ( m_period - remainder );
        }
    }

    public long getOffset()
    {
        return m_offset;
    }

    public long getPeriod()
    {
        return m_period;
    }

    /**
     * Reset the original TimeTrigger.
     * This will recalculate the activation time for this trigger.
     */
    public void reset()
    {
        final long current = System.currentTimeMillis();

        if( -1 == m_offset )
        {
            m_triggerTime = current;
        }
        else
        {
            m_triggerTime = current + m_offset;
        }
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "PeriodicTimeTrigger[ " );

        sb.append( "trigger time=" );
        sb.append( m_triggerTime );
        sb.append( " " );

        sb.append( "offset=" );
        sb.append( m_offset );
        sb.append( " " );

        if( -1 != m_period )
        {
            sb.append( "period=" );
            sb.append( m_period );
            sb.append( " " );
        }

        sb.append( "]" );

        return sb.toString();
    }
}



