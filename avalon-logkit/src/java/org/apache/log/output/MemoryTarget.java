/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import org.apache.log.Hierarchy;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.Priority;

/**
 * Output LogEvents into an buffer in memory.
 * At a later stage these LogEvents can be forwarded or
 * pushed to another target. This pushing is triggered
 * when buffer is full, the priority of a LogEvent reaches a threshold
 * or when another class calls the push method.
 *
 * This is based on specification of MemoryHandler in Logging JSR47.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class MemoryTarget
    implements LogTarget
{
    ///Buffer for all the LogEvents
    private final LogEvent[]  m_buffer;

    ///Priority at which to push LogEvents to next LogTarget
    private Priority          m_threshold;

    ///Target to push LogEvents to
    private LogTarget         m_target;

    ///Count of used events
    private int               m_used;

    ///Position of last element inserted
    private int               m_index;

    ///Flag indicating whether it is possible to overite elements in array
    private boolean           m_overwrite;

    public MemoryTarget( final LogTarget target,
                         final int size,
                         final Priority threshold )
    {
        m_target = target;
        m_buffer = new LogEvent[ size ];
        m_threshold = threshold;
    }

    /**
     * Set flag indicating whether it is valid to overwrite memory buffer.
     *
     * @param overwrite true if buffer should overwrite logevents in buffer, false otherwise
     */
    protected void setOverwrite( final boolean overwrite )
    {
        m_overwrite = overwrite;
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    public synchronized void processEvent( final LogEvent event )
    {
        //Check if it is full
        if( isFull() )
        {
            if( m_overwrite ) m_used--;
            else
            {
                error( "Memory buffer is full", null );
                return;
            }
        }

        if( 0 == m_used ) m_index = 0;
        else
        {
            m_index = (m_index + 1) % m_buffer.length;
        }
        m_buffer[ m_index ] = event;
        m_used++;

        if( shouldPush( event ) )
        {
            push();
        }
    }

    /**
     * Check if memory buffer is full.
     *
     * @return true if buffer is full, false otherwise
     */
    public final boolean isFull()
    {
        return m_buffer.length == m_used;
    }

    /**
     * Determine if LogEvent should initiate a push to target.
     * Subclasses can overide this method to change the conditions
     * under which a push occurs.
     *
     * @param event the incoming LogEvent
     * @return true if should push, false otherwise
     */
    protected boolean shouldPush( final LogEvent event )
    {
        return ( m_threshold.isLowerOrEqual( event.getPriority() ) || isFull() );
    }

    /**
     * Push log events to target.
     */
    public synchronized void push()
    {
        if( null == m_target )
        {
            error( "Can not push events to a null target", null );
            return;
        }

        try
        {
            final int size = m_used;
            int base = m_index - m_used + 1;
            if( base < 0 ) base += m_buffer.length;

            for( int i = 0; i < size; i++ )
            {
                final int index = (base + i) % m_buffer.length;

                //process event in buffer
                m_target.processEvent( m_buffer[ index ] );

                //help GC
                m_buffer[ index ] = null;
                m_used--;
            }
        }
        catch( final Throwable throwable )
        {
            error( "Unknown error pushing events.", throwable );
        }
    }


    /**
     * Helper method to write error messages to error handler.
     *
     * @param message the error message
     * @param throwable the exception if any
     */
    protected final void error( final String message, final Throwable throwable )
    {
        Hierarchy.getDefaultHierarchy().log( message, throwable );
        //TODO:
        //Can no longer route to global error handler - somehow need to pass down error
        //handler from engine...
    }
}
