/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.scheduler;

import org.apache.avalon.configuration.Configuration;

/**
 * This is the interface to hold a particular CronJob.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Job
{
    /**
     * Retrieve the next time at which this task needs to be run.
     *
     * @return the time at which the task needs to be run
     */
    long getTimeAfter( long time );

    /**
     * Retrieve task to execute at each time slice.
     *
     * @return the executable
     */
    Runnable getRunnable(); 

    /**
     * Retrieve configuraiton data to use to configure task.
     *
     * @return configuration data
     */
    Configuration getConfiguration(); 
}
