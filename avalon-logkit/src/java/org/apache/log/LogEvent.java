/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * This class encapsulates each individual log event.
 * LogEvents usually originate at a Logger and are routed
 * to LogTargets.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class LogEvent
{    
    //A Constant used when retrieving time relative to start of applicaiton start
    private final static long   START_TIME           = System.currentTimeMillis();

    ///The category that this LogEvent concerns. (Must not be null)
    private String                   m_category;

    ///The message to be logged. (Must not be null)
    private String                   m_message;

    ///The exception that caused LogEvent if any. (May be null)
    private Throwable                m_throwable;

    ///The time in millis that LogEvent occured
    private long                     m_time;

    ///The priority of LogEvent. (Must not be null)
    private Priority                 m_priority;

    ///The context stack associated with LogEvent.
    private ContextStack             m_contextStack;

    /**
     * Get Priority for LogEvent.
     *
     * @return the LogEvent Priority
     */
    public final Priority getPriority()
    {
        return m_priority;
    }

    /**
     * Set the priority of LogEvent.
     *
     * @param priority the new LogEvent priority
     */
    public final void setPriority( final Priority priority )
    {
        m_priority = priority;
    }

    /**
     * Get ContextStack associated with LogEvent
     *
     * @return the ContextStack
     */
    public final ContextStack getContextStack()
    {
        return m_contextStack;
    }

    /**
     * Set the ContextStack for this LogEvent.
     * Note that if this LogEvent ever changes threads, the 
     * ContextStack must be cloned.
     *
     * @param contextStack the context stack
     */
    public final void setContextStack( final ContextStack contextStack )
    {
        m_contextStack = contextStack;
    }

    /**
     * Get the category that LogEvent relates to.
     *
     * @return the name of category
     */
    public final String getCategory()
    {
        return m_category;
    }
  
    /**
     * Get the message associated with event.
     *
     * @return the message
     */
    public final String getMessage()
    {
        return m_message;
    }

    /**
     * Get throwabe instance associated with event.
     *
     * @return the Throwable
     */
    public final Throwable getThrowable()
    {
        return m_throwable;
    }
  
    /**
     * Get the absolute time of the log event.
     *
     * @return the absolute time
     */
    public final long getTime()
    {
        return m_time;
    }
  
    /**
     * Get the time of the log event relative to start of application.
     *
     * @return the time
     */
    public final long getRelativeTime()
    {
        return m_time - START_TIME;
    }
  
    /**
     * Set the LogEvent category.
     *
     * @param category the category
     */
    public final void setCategory( final String category )
    {
        m_category = category;
    }
  
    /**
     * Set the message for LogEvent.
     *
     * @param message the message
     */
    public final void setMessage( final String message )
    {
        m_message = message;
    }
  
    /**
     * Set the throwable for LogEvent.
     *
     * @param throwable the instance of Throwable
     */
    public final void setThrowable( final Throwable throwable )
    {
        m_throwable = throwable;
    }
  
    /**
     * Set the absolute time of LogEvent.
     *
     * @param time the time
     */
    public final void setTime( final long time )
    {
        m_time = time;
    }
}
