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
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class LogEvent
{    
    private String                   m_category;
    private String                   m_message;
    private Throwable                m_throwable;
    private long                     m_time;
    private Priority                 m_priority;
    private ContextStack             m_contextStack;

    public final Priority getPriority()
    {
        return m_priority;
    }

    public final void setPriority( final Priority priority )
    {
        m_priority = priority;
    }

    public final ContextStack getContextStack()
    {
        return m_contextStack;
    }

    public final void setContextStack( final ContextStack contextStack )
    {
        m_contextStack = contextStack;
    }

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
     * Get the time of the log event
     *
     * @return the time
     */
    public final long getTime()
    {
        return m_time;
    }
  
    /**
     * Mutator for property Category.
     *
     * @param category the category
     */
    public final void setCategory( final String category )
    {
        m_category = category;
    }
  
    /**
     * Mutator for property message.
     *
     * @param message the message
     */
    public final void setMessage( final String message )
    {
        m_message = message;
    }
  
    /**
     * Mutator for property Throwable.
     *
     * @param throwable the instance of Throwable
     */
    public final void setThrowable( final Throwable throwable )
    {
        m_throwable = throwable;
    }
  
    /**
     * Mutator for property time.
     *
     * @param time the time
     */
    public final void setTime( final long time )
    {
        m_time = time;
    }
}
