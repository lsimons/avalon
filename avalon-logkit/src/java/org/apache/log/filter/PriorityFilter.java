/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.filter;

import org.apache.log.LogEvent;
import org.apache.log.Priority;

/**
 * Filters log events based on priority.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PriorityFilter 
    extends AbstractFilterTarget 
{
    private Priority    m_priority;

    public PriorityFilter( final Priority priority )
    {
        m_priority = priority;
    }

    /**
     * Filter the log event.
     *
     * @param event the event
     * @return return true to discard event, false otherwise
     */
    protected boolean filter( final LogEvent event )
    {
        return ( m_priority.isLowerOrEqual( event.getPriority() ) );
    }
}
