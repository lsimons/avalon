/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.scheduler;

import org.apache.cornerstone.services.scheduler.Job;

/**
 * Class use internally to package to hold job entries.
 *
 * @author <a href="mailto:donaldp@mad.scientist.com">Peter Donald</a>
 */
final class JobEntry
    implements Comparable
{
    protected final String   m_name;
    protected final Job      m_job;

    //cached version of time from Job class
    protected long           m_time;
    protected boolean        m_isValid;

    /**
     * Constructor that constructs a JobEntry with a name and a job to do.
     *
     * @param name the name of Job
     * @param job the job
     */
    JobEntry( final String name, final Job job )
    {
        m_name = name;
        m_job = job;
        m_time = job.getTimeAfter( System.currentTimeMillis() );
        m_isValid = true;
    }

    /**
     * Return name of job.
     *
     * @return the name of job
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the job indicated by this entry.
     *
     * @return the Job
     */
    public Job getJob()
    {
        return m_job;
    }

    /**
     * Determine if this entry is valid
     *
     * @return true if job is valid, false otherwise
     */
    public boolean isValid()
    {
        return m_isValid;
    }

    /**
     * Invalidate job
     */
    public void invalidate()
    {
        m_isValid = false;
    }

    /**
     * Retrieve cached time when job should run next.
     *
     * @return the time in milliseconds when job should run
     */
    public long getNextTime()
    {
        return m_time;
    }

    /**
     * Set cached time in milliseconds when job should run
     *
     * @param time the time
     */
    public void setNextTime( long time )
    {
        m_time = time;
    }

    /**
     * Implement comparable interface used to help sort jobs.
     * Jobs are compared based on next time to run
     *
     * @param object the other job
     * @return -'ve value if other job occurs before this job
     */
    public int compareTo( final Object object )
    {
        final JobEntry other = (JobEntry)object;
        return (int)(other.m_time - m_time);
    }
}

