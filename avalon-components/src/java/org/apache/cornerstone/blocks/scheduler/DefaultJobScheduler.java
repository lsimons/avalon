/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.scheduler;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.phoenix.Block;
import org.apache.cornerstone.services.scheduler.Job;
import org.apache.cornerstone.services.scheduler.JobScheduler;
import org.apache.avalon.util.BinaryHeap;
import org.apache.avalon.util.lang.ThreadManager;

/**
 * Default implementation of JobScheduling service.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated This class is going to be merged with org.apache.cornerstone.blocks.eventserver.*
 */
public class DefaultJobScheduler
    extends AbstractLoggable
    implements Block, JobScheduler, Initializable, Runnable
{
    protected final class JobRunner
        extends AbstractLoggable
        implements Runnable
    {
        protected final String              m_name;
        protected final Runnable            m_runnable;

        private JobRunner( final String name, final Runnable runnable )
        {
            m_name = name;
            m_runnable = runnable;
        }

        public void run()
        {
            try
            {
                if( m_runnable instanceof Initializable )
                {
                    ((Initializable)m_runnable).init();
                }
                    
                m_runnable.run();

                if( m_runnable instanceof Disposable )
                {
                    ((Disposable)m_runnable).dispose();
                }
            }
            catch( final Throwable throwable )
            {
                this.getLogger().warn( "Error occured executing scheduled task " + m_name,
                                       throwable );
            }
        }
        
    }
    protected final Object               MONITOR      = new Object();

    protected boolean                    m_running;
    protected HashMap                    m_jobs;
    protected BinaryHeap                 m_priorityQueue;
    
    public void init()
    {
        m_jobs = new HashMap();
        m_priorityQueue = new BinaryHeap();
    }

    public void destroy()
    {
        m_running = false;
        synchronized( MONITOR ) { MONITOR.notify(); }
        m_jobs = null;
        m_priorityQueue = null;
    }

    public void addJob( final String name, final Job job )
    {
        removeJob( name );

        final JobEntry jobEntry = new JobEntry( name, job );
        m_jobs.put( name, job );
        m_priorityQueue.insert( jobEntry );

        if( job == m_priorityQueue.peek() )
        {
            synchronized( MONITOR ) { MONITOR.notify(); }
        }
    }

    public void removeJob( final String name )
    {
        //use the kill-o-matic against any job with same name
        final JobEntry jobEntry = (JobEntry)m_jobs.get( name );
        if( null != jobEntry ) jobEntry.invalidate();
    }

    public Job getJob( final String name )
    {
        final JobEntry jobEntry = (JobEntry)m_jobs.get( name );

        if( null != jobEntry ) return jobEntry.getJob();
        else return null;
    }    

    /**
     * Helper method to configure a task if it is required.
     *
     * @param name the name of job
     * @param runnable the task
     * @param configuration the configuration (can be null if no configuration available)
     */
    protected void doConfigure( final String name, 
                                final Runnable runnable, 
                                final Configuration configuration )
        throws ConfigurationException
    {
        if( runnable instanceof Configurable )
        {
            if( null == configuration )
            {
                getLogger().warn( "Configurable task " + name + " has no configuration." );
            }
            else
            {
                ((Configurable)runnable).configure( configuration );
            }
        }
        else if( null != configuration )
        {
            getLogger().warn( "Non-configurable task " + name + " has a configuration." );
        }
    }

    /**
     * Actually run a job.
     *
     * @param jobEntry the jobEntry to run
     */
    protected void runJob( final JobEntry jobEntry )
    {
        final Job job = jobEntry.getJob();
        final Runnable runnable = job.getRunnable();
        final Configuration configuration = job.getConfiguration();
        
        try
        {
            doConfigure( jobEntry.getName(), runnable, configuration );
        }
        catch( final ConfigurationException ce )
        {
            getLogger().warn( "Failed to configure job " + jobEntry.getName(), ce );
            return;
        }
        
        try
        {
            final JobRunner runner = new JobRunner( jobEntry.getName(), runnable );
            runner.setLogger( getLogger() );
            ThreadManager.getWorkerPool("job-scheduler").execute( runner );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Execution occured while running job " + jobEntry.getName(), e );
        }
    }

    /**
     * Main execution loop the detects when a job is to run 
     * and starts it appropriate times.
     */
    public void run()
    {
        m_running = true;

        while( m_running )
        {
            long duration = 0;

            if( !m_priorityQueue.isEmpty() )
            {
                final JobEntry jobEntry = 
                    (JobEntry)m_priorityQueue.peek();
                duration = jobEntry.getNextTime() - System.currentTimeMillis();

                if( duration < 0 )
                {
                    //time to run job so remove it from priority queue
                    //and run it
                    m_priorityQueue.pop();

                    //if job has been invalidated then remove it and continue
                    if( !jobEntry.isValid() ) continue;

                    runJob( jobEntry );

                    //reschedule if appropriate
                    final long next = 
                        jobEntry.getJob().getTimeAfter( System.currentTimeMillis() );

                    if( 0 < next )
                    {
                        jobEntry.setNextTime( next );
                        m_priorityQueue.insert( jobEntry );
                    }

                    continue;
                }
            }

            //wait/sleep until monitor is signalled which occurs when 
            //next jobs is likely to occur or when a new job gets added to 
            //top of heap
            try { synchronized( MONITOR ) { MONITOR.wait( duration ); } }
            catch( final InterruptedException ie ) { }
        }
    }
}

