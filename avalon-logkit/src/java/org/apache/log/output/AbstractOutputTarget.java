/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import org.apache.log.Formatter;
import org.apache.log.LogTarget;
import org.apache.log.LogEntry;

/**
 * An abstract implementation of a basic output target.
 * Concrete sub-classes have to set a formatter and implement output.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractOutputTarget 
    implements LogTarget
{
    protected Formatter                   m_formatter;

    /**
     * Retrieve the associated formatter.
     *
     * @return the formatter
     */
    public Formatter getFormatter()
    {
        return m_formatter;
    }

    /**
     * Set the formatter.
     *
     * @param formatter the formatter
     */
    public void setFormatter( final Formatter formatter )
    {
        m_formatter = formatter;
    }

    /**
     * Abstract method that will output entry.
     *
     * @param data the data to be output
     */
    protected abstract void output( final String data );

    /**
     * Process a log entry, via formatting and outputting it.
     *
     * @param entry the log entry
     */
    public void processEntry( final LogEntry entry )
    {
        String outputData = null;
        if( null != m_formatter ) outputData = m_formatter.format( entry );
        else outputData = entry.toString();
        output( outputData );
    }
}
