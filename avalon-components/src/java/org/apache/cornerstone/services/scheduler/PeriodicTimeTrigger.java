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
 */
public class PeriodicTimeTrigger
    implements TimeTrigger
{
    protected final long    m_startTime;
    protected final long    m_period;
    
    public PeriodicTimeTrigger( final int startTime, final int period )
    {
        final long current = System.currentTimeMillis();

        if( -1 == startTime )
        {
            m_startTime = current;
        }
        else
        {
            m_startTime = current + startTime;
        }

        m_period = period;
    }

    /**
     * Retrieve the next time at which this trigger activates.
     *
     * @return the time at which the trigger will activate
     */
    public long getTimeAfter( final long time )
    {
        if( time <= m_startTime ) return m_startTime;
        else
        {
            if( -1 == m_period ) return -1;

            final long over = time - m_startTime;
            final long remainder = over % m_period;

            return time + ( m_period - remainder );
        }
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "PeriodicTimeTrigger[ " );

        sb.append( "start=" );
        sb.append( m_startTime );
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



