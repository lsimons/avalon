/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

import java.util.Hashtable;
import org.apache.log.output.DefaultOutputLogTarget;

/**
 * This defines the basic interface to a log engine.
 * The log engine represents an independent hierarchy.
 *
 *  @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class LogEngine
{
    private static final LogEngine  c_engine                 = new LogEngine();

    private Priority                m_priority               = Priority.DEBUG;
    private Logger                  m_rootLogger;

    /**
     * Retrieve the default log engine.
     *
     * @return the default LogEngine
     */
    public static LogEngine getDefaultLogEngine()
    {
        return c_engine;
    }

    public LogEngine()
    {
        m_rootLogger = new Logger( this, "", null, null );
        setDefaultLogTarget( new DefaultOutputLogTarget() );
    }

    public synchronized void setDefaultLogTarget( final LogTarget target )
    {
        if( null == target )
        {
            throw new IllegalArgumentException( "Can not set DefaultLogTarget to null" );
        }

        final LogTarget[] targets = new LogTarget[] { target };
        m_rootLogger.setLogTargets( targets );
    }

    /**
     * Retrieve a logger for named category.
     *
     * @param category the context
     * @return the Logger
     */
    public synchronized Logger getLoggerFor( final String category )
    {
        return m_rootLogger.getChildLogger( category );
    }

    /**
     * Return VM global priority.
     *
     * @return the priority
     */
    public Priority getGlobalPriority()
    {
        return m_priority;
    }

    /**
     * Set the global priority for this virtual machine.  Nothing below
     * this level will be logged when using this LogKit.
     */
    public void setGlobalPriority( final Priority priority )
    {
        m_priority = priority;
    }

    /**
     * Log an error message and exception to stderr.
     * TODO: replace this with an error handler
     */
    public void log( final String message, final Throwable t )
    {
        System.err.println( "Error: " + message );
        t.printStackTrace();
    }

    /**
     * Logs an error message to stderr.
     * TODO: replace this with an error handler
     */
    public void log( final String message )
    {
        System.err.println( "Error: " + message );
    }
}
