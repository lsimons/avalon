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
 * This defines a basic independent log hierarchy.
 *
 *  @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Hierarchy
{
    private static final Hierarchy  c_hierarchy      = new Hierarchy();

    private Logger                  m_rootLogger;

    /**
     * Retrieve the default log engine.
     *
     * @return the default Hierarchy
     */
    public static Hierarchy getDefaultHierarchy()
    {
        return c_hierarchy;
    }

    public Hierarchy()
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
        getRootLogger().setLogTargets( targets );
    }

    /**
     * Retrieve a logger for named category.
     *
     * @param category the context
     * @return the Logger
     */
    public synchronized Logger getLoggerFor( final String category )
    {
        return getRootLogger().getChildLogger( category );
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

    protected final Logger getRootLogger()
    {
        return m_rootLogger;
    }
}
