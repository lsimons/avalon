/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.thread.ThreadControl;

/**
 * Default implementation of ThreadControl interface.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
final class DefaultThreadControl
    implements ThreadControl
{
    ///Thread that this control is associated with
    private Thread m_thread;

    ///Throwable that caused thread to terminate
    private Throwable m_throwable;

    /**
     * Construct thread control for a specific thread.
     *
     * @param thread the thread to control
     */
    protected DefaultThreadControl( final Thread thread )
    {
        m_thread = thread;
    }

    /**
     * Wait for specified time for thread to complete it's work.
     *
     * @param milliSeconds the duration in milliseconds to wait until the thread has finished work
     * @exception IllegalStateException if isValid() == false
     * @exception InterruptedException if another thread has interrupted the current thread.
     *            The interrupted status of the current thread is cleared when this exception
     *            is thrown.
     */
    public synchronized void join( final long milliSeconds )
        throws IllegalStateException, InterruptedException
    {
        //final long start = System.currentTimeMillis();
        wait( milliSeconds );
        /*
          if( !isFinished() )
          {
          final long now = System.currentTimeMillis();
          if( start + milliSeconds > now )
          {
          final long remaining = milliSeconds - (now - start);
          join( remaining );
          }
          }
        */
    }

    /**
     * Call Thread.interrupt() on thread being controlled.
     *
     * @exception IllegalStateException if isValid() == false
     * @exception SecurityException if caller does not have permission to call interupt()
     */
    public synchronized void interupt()
        throws IllegalStateException, SecurityException
    {
        if( !isFinished() )
        {
            m_thread.interrupt();
        }
    }

    /**
     * Determine if thread has finished execution
     *
     * @return true if thread is finished, false otherwise
     */
    public synchronized boolean isFinished()
    {
        return ( null == m_thread );
    }

    /**
     * Retrieve throwable that caused thread to cease execution.
     * Only valid when true == isFinished()
     *
     * @return the throwable that caused thread to finish execution
     */
    public Throwable getThrowable()
    {
        return m_throwable;
    }

    /**
     * Method called by thread to release control.
     *
     * @param throwable Throwable that caused thread to complete (may be null)
     */
    protected synchronized void finish( final Throwable throwable )
    {
        m_thread = null;
        m_throwable = throwable;
        notifyAll();
    }
}
