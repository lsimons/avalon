/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * This class encapsulates each individual log entry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class LogEntry
{
    protected Category                 m_category;
    protected ContextStack             m_context;
    protected String                   m_message;
    protected Throwable                m_throwable;
    protected long                     m_time;
    protected Priority.Enum            m_priority;

    public Priority.Enum getPriority()
    {
        return m_priority;
    }

    public void setPriority( final Priority.Enum priority )
    {
        m_priority = priority;
    }

    public Category getCategory()
    {
        return m_category;
    }
  
    /**
     * Describe 'getContextStack' method here.
     *
     * @return a value of type 'ContextStack'
     */
    public ContextStack getContextStack()
    {
        return m_context;
    }
  
    /**
     * Get the message associated with entry.
     *
     * @return the message
     */
    public String getMessage()
    {
        return m_message;
    }

    /**
     * Get throwabe instance associated with entry.
     *
     * @return the Throwable
     */
    public Throwable getThrowable()
    {
        return m_throwable;
    }
  
    /**
     * Get the time of the log entry
     *
     * @return the time
     */
    public long getTime()
    {
        return m_time;
    }
  
    /**
     * Mutator for property Category.
     *
     * @param category the category
     */
    public void setCategory( final Category category )
    {
        m_category = category;
    }
  
    /**
     * Mutator for property contextStack.
     *
     * @param context the context stack
     */
    public void setContextStack( final ContextStack context )
    {
        m_context = context;
    }
  
    /**
     * Mutator for property message.
     *
     * @param message the message
     */
    public void setMessage( final String message )
    {
        m_message = message;
    }
  
    /**
     * Mutator for property Throwable.
     *
     * @param throwable the instance of Throwable
     */
    public void setThrowable( final Throwable throwable )
    {
        m_throwable = throwable;
    }
  
    /**
     * Mutator for property time.
     *
     * @param time the time
     */
    public void setTime( final long time )
    {
        m_time = time;
    }
  
    /**
     * Default Constructor.
     *
     */
    public LogEntry() 
    {
        super();
    }
}
