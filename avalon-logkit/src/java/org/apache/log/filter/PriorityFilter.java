/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.filter;

import org.apache.log.LogEntry;
import org.apache.log.Priority;

/**
 * Filters log entrys based on priority.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PriorityFilter 
    extends AbstractFilterTarget 
{
    protected Priority.Enum    m_priority;

    public PriorityFilter( final Priority.Enum priority )
    {
        m_priority = priority;
    }

    /**
     * Filter the log entry.
     *
     * @param entry the entry
     * @return return true to discard entry, false otherwise
     */
    protected boolean filter( final LogEntry entry )
    {
        return
            ( m_priority.isLowerOrEqual( entry.getPriority() ) );
    }
}
