/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * The LogKit provides the access to static methods to
 * manipulate the logging sub-system
 *
 *  @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class LogKit
{
    /**
     * Get the Current ContextStack.
     * This returns a ContextStack associated with current thread. If the
     * thread doesn't have a ContextStack associated with it then a new
     * ContextStack is created with the name of thread as base context.
     *
     * @return the current ContextStack
     */
    public static ContextStack getCurrentContext()
    {
        return ContextStack.getCurrentContext();
    }

    /**
     * Retrieve a logger for named category.
     *
     * @param category the context
     * @return the Logger
     */
    public static Logger getLoggerFor( final String category )
    {
        return Hierarchy.getDefaultHierarchy().getLoggerFor( category );
    }

    /**
     * Retrieve a Priority.Enum value for the String Priority level.
     *
     * @param priority the priority
     * @return the descriptive string
     * @deprecated Use Priority.getPriorityForName() instead
     */
    public static Priority getPriorityForName( final String priority )
    {
        return Priority.getPriorityForName( priority );
    }

    /**
     * Loga an error message and exception to stderr.
     */
    public static void log( final String message, final Throwable t )
    {
        Hierarchy.getDefaultHierarchy().log( message, t );
    }

    /**
     * Logs an error message to stderr.
     */
    public static void log( final String message )
    {
        Hierarchy.getDefaultHierarchy().log( message );
    }

    /**
     * Sets the default LogTarget for the default logger.
     */
    public static void setDefaultLogTarget( final LogTarget defaultLogTarget )
    {
        Hierarchy.getDefaultHierarchy().setDefaultLogTarget( defaultLogTarget );
    }

    /**
     * Constructor hidden to prevent instantiation of this static object
     */
    private LogKit()
    {
    }
}
