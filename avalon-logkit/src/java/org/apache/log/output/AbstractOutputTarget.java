/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import org.apache.log.Formatter;
import org.apache.log.Hierarchy;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;

/**
 * Abstract output target.
 * Any new output target that is writing to a single connected 
 * resource should extend this class directly or indirectly.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractOutputTarget
    implements LogTarget
{
    /**
     * Formatter for target.
     *
     * @deprecated You should not be directly accessing this attribute
     *             as it will become private next release
     */
    protected Formatter    m_formatter;

    ///Flag indicating that log session is finished (aka target has been closed)
    private boolean      m_closed;

    /**
     * Parameterless constructor.
     */
    public AbstractOutputTarget()
    {
    }

    public AbstractOutputTarget( final Formatter formatter )
    {
        m_formatter = formatter;
    }

    /**
     * Retrieve the associated formatter.
     *
     * @return the formatter
     * @deprecated Access to formatter is not advised and this method will be removed
     *             in future iterations. It remains only for backwards compatability.
     */
    public Formatter getFormatter()
    {
        return m_formatter;
    }

    /**
     * Set the formatter.
     *
     * @param formatter the formatter
     * @deprecated In future this method will become protected access.
     */
    public void setFormatter( final Formatter formatter )
    {
        writeTail();
        m_formatter = formatter;
        writeHead();
    }

    /**
     * Abstract method to write data.
     *
     * @param data the data to be output
     */
    protected void write( final String data )
    {
        output( data );
    }

    /**
     * Abstract method that will output event.
     *
     * @param data the data to be output
     * @deprecated User should overide write() instead of output(). Output exists
     *             for backwards compatability and will be removed in future.
     */
    protected void output( final String data )
    {
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    public void processEvent( final LogEvent event )
    {
        if( m_closed )
        {
            error( "Writing event to closed stream.", null );
            return;
        }

        try
        {
            final String data = format( event );
            write( data );
        }
        catch( final Throwable throwable )
        {
            error( "Unknown error writing event.", throwable );
        }
    }

    /**
     * Startup log session.
     *
     */
    protected void open()
    {
        m_closed = false;
        writeHead();
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public void close()
    {
        writeTail();
        m_closed = true;
    }

    /**
     * Helper method to format an event into a string, using the formatter if available.
     *
     * @param event the LogEvent
     * @return the formatted string
     */
    private String format( final LogEvent event )
    {
        if( null != m_formatter )
        {
            return m_formatter.format( event );
        }
        else
        {
            return event.toString();
        }
    }

    /**
     * Helper method to write out log head.
     * The head initiates a session of logging.
     */
    private void writeHead()
    {
        if( m_closed ) return;

        final String head = getHead();
        if( null != head )
        {
            write( head );
        }
    }

    /**
     * Helper method to write out log tail.
     * The tail completes a session of logging.
     */
    private void writeTail()
    {
        if( m_closed ) return;

        final String tail = getTail();
        if( null != tail )
        {
            write( tail );
        }
    }

    /**
     * Helper method to retrieve head for log session.
     * TODO: Extract from formatter
     *
     * @return the head string
     */
    private String getHead()
    {
        return null;
    }

    /**
     * Helper method to retrieve tail for log session.
     * TODO: Extract from formatter
     *
     * @return the head string
     */
    private String getTail()
    {
        return null;
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
