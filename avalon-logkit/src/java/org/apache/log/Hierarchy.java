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
 * This class encapsulates a basic independent log hierarchy.
 * The hierarchy is essentially a safe wrapper around root logger.
 *
 *  @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Hierarchy
{
    ///The instance of default hierarchy
    private static final Hierarchy  c_hierarchy      = new Hierarchy();

    ///The root logger which contains all Loggers in this hierarchy 
    private Logger                  m_rootLogger;

    /**
     * Retrieve the default hierarchy.
     *
     * <p>In most cases the default LogHierarchy is the only
     * one used in an application. However when security is 
     * a concern or multiple independent applications will
     * be running in same JVM it is advantageous to create
     * new Hierarchies rather than reuse default.</p>
     *
     * @return the default Hierarchy
     */
    public static Hierarchy getDefaultHierarchy()
    {
        return c_hierarchy;
    }

    /**
     * Create a hierarchy object.
     * The default LogTarget writes to stdout.
     */
    public Hierarchy()
    {
        m_rootLogger = new Logger( this, "", null, null );
        setDefaultLogTarget( new DefaultOutputLogTarget() );
    }

    /**
     * Set the default log target for hierarchy.
     * This is the target inherited by loggers if no other target is specified.
     *
     * @param target the default target
     */
    public void setDefaultLogTarget( final LogTarget target )
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
    public Logger getLoggerFor( final String category )
    {
        return getRootLogger().getChildLogger( category );
    }

    /**
     * Logs an error message to error handler.
     * Default Error Handler is stderr.
     *
     * @param message a message to log
     * @param t a Throwable to log
     */
    public void log( final String message, final Throwable t )
    {
        //TODO: replace this with an error handler
        System.err.println( "Error: " + message );
        t.printStackTrace();
    }

    /**
     * Logs an error message to error handler.
     * Default Error Handler is stderr.
     *
     * @param message a message to log
     */
    public void log( final String message )
    {
        //TODO: replace this with an error handler
        System.err.println( "Error: " + message );
    }

    /**
     * Utility method to retrieve logger for hierarchy.
     * This method is intended for use by sub-classes
     * which can take responsibility for manipulating
     * Logger directly.
     *
     * @return the Logger
     */
    protected final Logger getRootLogger()
    {
        return m_rootLogger;
    }
}
