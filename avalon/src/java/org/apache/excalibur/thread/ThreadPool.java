/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.thread;

/**
 * This class is the public frontend for the thread pool code.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ThreadPool
{
    /**
     * Run work in separate thread.
     *
     * @param work the work to be executed.
     * @exception Exception if an error occurs
     */
    void execute( final Runnable work )
        throws Exception;

    /**
     * Run work in separate thread at a particular priority.
     *
     * @param work the work to be executed.
     * @param priority the priority
     * @exception Exception if an error occurs
     */
    void execute( final Runnable work, final int priority )
        throws Exception;

    /**
     * Run work in separate thread.
     * Wait till work is complete before returning.
     *
     * @param work the work to be executed.
     * @exception Exception if an error occurs
     */
    void executeAndWait( final Runnable work )
        throws Exception;

    /**
     * Run work in separate thread at a particular priority.
     * Wait till work is complete before returning.
     *
     * @param work the work to be executed.
     * @param priority the priority
     * @exception Exception if an error occurs
     */
    void executeAndWait( final Runnable work, final int priority )
        throws Exception;
}
