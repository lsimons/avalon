/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool;

/**
 * This class implements a counting semaphore, also known as a
 * Dijkstra semaphore.  A semaphore is used to control access to
 * resources.  A counting semaphore has a count associated with it and
 * each acquire() call reduces the count.  A thread that tries to
 * acquire() a semaphore with a zero count blocks until someone else
 * calls release(), which increases the count.
 *
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/07 06:04:21 $
 * @since 4.0
 */
class Mutex
{
    private long m_tokens;

    /**
     * Creates a semaphore with the specified number of tokens, which
     * determines the maximum number of acquisitions to allow.
     *
     * @param tokens the maximum number of acquisitions to allow
     */
    public Mutex()
    {
        m_tokens = 1;
    }

    public synchronized void acquire()
        throws InterruptedException
    {
        //TODO: check for interuption outside sync block?
        if( Thread.interrupted() ) throw new InterruptedException();

        //While there is no more tokens left wait
        while( 0 >= m_tokens )
        {
            wait();
        }
        m_tokens--;
    }

    public synchronized void release()
    {
        m_tokens++;
        notify();
    }

    public synchronized boolean attempt( final long msecs )
        throws InterruptedException
    {
        if( Thread.interrupted() ) throw new InterruptedException();

        if( m_tokens > 0 )
        {
            m_tokens--;
            return true;
        }
        else
        {
            final long start = System.currentTimeMillis();
            long wait = msecs;

            while( wait > 0 )
            {
                wait( wait );

                if( m_tokens > 0 )
                {
                    m_tokens--;
                    return true;
                }
                else
                {
                    wait = msecs - ( System.currentTimeMillis() - start );
                }
            }

            return false;
        }
    }
}
