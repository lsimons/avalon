/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.scheduler;

import org.apache.phoenix.Service;

/**
 * This service provides a way to regularly schedule jobs.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface JobScheduler
    extends Service
{
    /**
     * Add a job to be scheduled to run.
     * Note that if a job already has same name then it is removed.
     *
     * @param name the name of job
     * @param job the job
     */
    void addJob( String name, Job job );

    /**
     * Remove a job scheduled to be run by name.
     *
     * @param name the name of the job
     */
    void removeJob( String name );

    /**
     * Retrieve a job by name
     *
     * @param name the name of job
     * @return the Job
     */
    Job getJob( String name );
}
