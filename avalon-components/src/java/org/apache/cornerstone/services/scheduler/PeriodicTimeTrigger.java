/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.scheduler;

/**
 * This is the triggers based on a start time and period.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 */
public class PeriodicTimeTrigger
    implements TimeTrigger
{
    protected final long    m_offset;
    protected final long    m_period;
    private   long          m_triggerTime;
    
    public PeriodicTimeTrigger( final int offset, final int period )
    {
        m_offset = offset;
        m_period = period;

        reset();
    }

    /**
     * Retrieve the next time at which this trigger activates.
     *
     * @return the time at which the trigger will activate
     */
    public long getTimeAfter( final long time )
    {
        if( time <= m_triggerTime ) return m_triggerTime;
        else
        {
            if( -1 == m_period ) return -1;

            final long over = time - m_triggerTime;
            final long remainder = over % m_period;

            return time + ( m_period - remainder );
        }
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
        
        sb.append("]");

        return sb.toString();
    }
}



