/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities;

import org.apache.avalon.framework.atlantis.Facility;
import org.apache.avalon.excalibur.thread.ThreadPool;

/**
 * This facility manages the policy for an application instance.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ThreadManager
    extends Facility
{
    /**
     * Retrieve thread pool by name.
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
