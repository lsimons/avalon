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
import org.apache.log.LogEvent;

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
     * Abstract method that will output event.
     *
     * @param data the data to be output
     */
    protected abstract void output( final String data );

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    public void processEvent( final LogEvent event )
    {
        String outputData = null;
        if( null != m_formatter ) outputData = m_formatter.format( event );
        else outputData = event.toString();
        output( outputData );
    }
}
