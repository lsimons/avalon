/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.filter;

import org.apache.log.FilterTarget;
import org.apache.log.LogEntry;
import org.apache.log.LogTarget;

/**
 * Abstract implementation of FilterTarget.
 * A concrete implementation has to implement filter method.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractFilterTarget 
    implements FilterTarget
{
    protected LogTarget              m_targets[];

    /**
     * Add a new target to output chain.
     *
     * @param target the target
     */
    public void addTarget( final LogTarget target )
    {
        if( null == m_targets )
        {
            m_targets = new LogTarget[] { target };
        }
        else
        {
            final LogTarget oldTargets[] = m_targets;
            m_targets = new LogTarget[ oldTargets.length + 1 ];
            System.arraycopy( oldTargets, 0, m_targets, 0, oldTargets.length );
            m_targets[ m_targets.length - 1 ] = target;
        }
    }

    /**
     * Filter the log entry.
     *
     * @param entry the entry
     * @return return true to discard entry, false otherwise
     */
    protected abstract boolean filter( LogEntry entry );

    /**
     * Process a log entry
     *
     * @param entry the log entry
     */
    public void processEntry( final LogEntry entry ) 
    {
        if( null == m_targets || filter( entry ) ) return;
        else
        {
            for( int i = 0; i < m_targets.length; i++ )
            {
                m_targets[ i ].processEntry( entry );
            }
        }
    }
}
