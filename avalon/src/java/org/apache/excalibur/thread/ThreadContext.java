/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.thread;

/**
 * To deal with *current* ThreadContext.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class ThreadContext
{
    private final static RuntimePermission      c_permission =
        new RuntimePermission( "ThreadContext.setCurrentThreadPool" );
    private final static InheritableThreadLocal c_context    = new InheritableThreadLocal();

    /**
     * Retrieve thread pool associated with current thread
     *
     * @return a thread pool
     */
    public static ThreadPool getCurrentThreadPool()
    {
        return (ThreadPool)c_context.get();
    }

    /**
     * Set the thread pool that will be returned by getCurrentThreadPool() in this thread
     * and decendent threads.
     *
     * @param threadPool the new thread pool
     * @exception SecurityException if the caller does not have permission to set thread pool
     */
    public static void setCurrentThreadPool( final ThreadPool threadPool )
        throws SecurityException
    {
        final SecurityManager securityManager = System.getSecurityManager();

        if( null != securityManager )
        {
            securityManager.checkPermission( c_permission );
        }

        c_context.set( threadPool );
    }
}
