/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.concurrent;

/**
 * A thread barrier blocks all threads hitting it until a pre-defined number
 * of threads arrive at the barrier. This is useful for implementing release
 * consistent concurrency where you don't want to take the performance penalty
 * of providing mutual exclusion to shared resources
 *
 * @author <a href="mailto:kranga@sapient.com">Karthik Rangaraju</a>
 */
public class ThreadBarrier
{
    private int m_threshold;
    private int m_count;

    /**
     * Initializes a thread barrier object with a given thread count
     * @param pCount is the number of threads that need to block on
     * barrierSynchronize() before they will be allowed to pass through
     * @see #barrierSynchronize()
     */
    public ThreadBarrier( int count )
    {
        m_threshold = count;
        m_count = 0;
    }

    /**
     * This method blocks all threads calling it until the threshold number of
     * threads have called it. It then releases all threads blocked by it
     * @throws InterruptedException if any thread blocked during the call is
     * interrupted
     */
    public void barrierSynchronize()
        throws InterruptedException
    {
        synchronized ( this )
        {
            if ( m_count != m_threshold - 1 )
            {
                m_count++;
                wait();
            }
            else
            {
                m_count = 0;
                notifyAll();
            }
        }
    }

}
