/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.excalibur.event.Queue;

/**
 * Abstract base class for a queue implementation. It implements
 * blocking for dequeue.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class AbstractQueue extends AbstractSink 
    implements Queue, DequeueInterceptorSource, EnqueuePredicateSink
{
    /** The time out period after which blocked enqueues should return. */
    protected long m_timeout = 0;
    
    /** A debugging tag for the queue */
    private final String m_tag;
    
    private static final DequeueInterceptor NULL_INTERCEPTOR = 
        new DefaultDequeueInterceptor();
    
    /** The dequeue executable executed before and after dequeue operations */
    private DequeueInterceptor m_executable = null;

    //----------------------- AbstractQueue constructors
    /**
     * @see Object#Object()
     */
    public AbstractQueue()
    {
        super();
        m_tag = "Queue";
        m_executable = NULL_INTERCEPTOR;
    }
    
    /**
     * Creates a queue with the tag name attached for 
     * debugging.
     * @since Sep 23, 2002
     * 
     * @param tag
     *  A name for the queue for debugging
     */
    public AbstractQueue(String tag)
    {
        super();
        m_tag = tag;
        m_executable = NULL_INTERCEPTOR;
    }

    //----------------------- DequeueInterceptorSource implementation
    /**
     * @see DequeueInterceptorSource#getDequeueExecutable()
     */
    public DequeueInterceptor getDequeueInterceptor()
    {
        return m_executable;
    }

    /**
     * @see DequeueInterceptorSource#setDequeueExecutable(DequeueInterceptor)
     */
    public void setDequeueInterceptor(DequeueInterceptor executable)
    {
        if(null == executable)
        {
            m_executable = NULL_INTERCEPTOR;
        }
        else
        {
            m_executable = executable;
        }
    }

    //----------------------- Source implementation
    /**
     * @see Source#setTimeout(long)
     */
    public void setTimeout(final long millis)
    {
        if (millis > 0)
        {
            m_timeout = millis;
        }
        else
        {
            m_timeout = 0;
        }
    }

    //----------------------- Sink implementation
    /**
     * @see Sink#isFull()
     */
    public boolean isFull()
    {
        // There exists an upper bound && it is reached.
        return (maxSize() != -1) && (maxSize() - size() <= 0);
    }

    /**
     * @see Sink#canAccept()
     */
    public int canAccept()
    {
        return (maxSize() > 0) ? maxSize() - size() : maxSize();
    }
    
    //----------------------- overridden methods in Object
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return m_tag + " Queue";
    }


    //----------------------- AbstractQueue specific implementation
    /**
     * Blocks using the object as a lock for the specified
     * time out time ({@link #setTimeout(long)}).
     * @since May 15, 2002
     * 
     * @param lock
     *  the object to use as a lock for blocking.
     */
    protected void block(Object lock)
    {
        if (m_timeout > 0)
        {
            long start = System.currentTimeMillis();
            long end = start + m_timeout;

            while (start < end || size() > 0)
            {
                try
                {
                    lock.wait(m_timeout);
                }
                catch (InterruptedException ie)
                {
                    // ignore
                }
            }
        }
    }
}