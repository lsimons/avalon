/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.threads;

import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.phoenix.Service;

/**
 * Manage a set of ThreadPools.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ThreadManager
    extends Service
{
    String ROLE = "org.apache.avalon.cornerstone.services.thread.ThreadManager";

    /**
     * Retrieve a thread pool by name.
     *
     * @param name the name of thread pool
     * @return the threadpool
     * @exception IllegalArgumentException if the name of thread pool is
     *            invalid or named pool does not exist
     */
    ThreadPool getThreadPool( String name )
        throws IllegalArgumentException;

    /**
     * Retrieve the default thread pool.
     *
     * @return the thread pool
     */
    ThreadPool getDefaultThreadPool();
}
