/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event;

/**
 * The default queue implementation is a variable size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 */
public abstract class AbstractQueue implements Queue
{
    // this object is immutable, so it can be safely shared
    protected final static QueueElement[] EMPTY_ARRAY = new QueueElement[ 0 ];
    protected long m_timeout = 0;

    /**
     * Default for canAccept()
     */
    public int canAccept()
    {
        return ( maxSize() > 0 ) ? maxSize() - size() : maxSize();
    }

    /**
     * Default maxSize to -1 which is unbounded
     */
    public int maxSize()
    {
        return -1;
    }

    /**
     * Default for isFull(). The method uses the maxSize() and size() methods
     * to determine whether the queue is full.
     */
    public boolean isFull()
    {
        return maxSize() != -1  /* There exists an upper bound... */
            && maxSize() - size() <= 0; /* ...and it is reached. */
    }

    /**
     * Set the timeout
     */
    public void setTimeout( final long millis )
    {
        if( millis > 0 )
        {
            m_timeout = millis;
        }
        else
        {
            m_timeout = 0;
        }
    }

    protected void block( Object lock )
    {
        if( m_timeout > 0 )
        {
            long start = System.currentTimeMillis();
            long end = start + m_timeout;

            while( start < end || size() > 0 )
            {
                try
                {
                    lock.wait( m_timeout );
                }
                catch( InterruptedException ie )
                {
                    // ignore
                }
            }
        }
    }
}
