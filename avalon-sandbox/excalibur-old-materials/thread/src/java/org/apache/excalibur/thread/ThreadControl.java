/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.thread;

/**
 * This interface defines the method through which Threads can be controller.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface ThreadControl
    extends org.apache.avalon.excalibur.thread.ThreadControl
{
    /**
     * Wait for specified time for thread to complete it's work.
     *
     * @param milliSeconds the duration in milliseconds to wait until the thread has finished work
     * @throws IllegalStateException if isValid() == false
     * @throws InterruptedException if another thread has interrupted the current thread.
     *            The interrupted status of the current thread is cleared when this exception
     *            is thrown.
     */
    void join( long milliSeconds )
        throws IllegalStateException, InterruptedException;

    /**
     * Call Thread.interupt() on thread being controlled.
     *
     * @throws IllegalStateException if isValid() == false
     * @throws SecurityException if caller does not have permission to call interupt()
     */
    void interupt()
        throws IllegalStateException, SecurityException;

    /**
     * Determine if thread has finished execution
     *
     * @return true if thread is finished, false otherwise
     */
    boolean isFinished();

    /**
     * Retrieve throwable that caused thread to cease execution.
     * Only valid when true == isFinished()
     *
     * @return the throwable that caused thread to finish execution
     */
    Throwable getThrowable();
}
