/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.scheduler;

import java.util.*;

import org.apache.avalon.cornerstone.services.scheduler.Target;
import org.apache.avalon.cornerstone.services.scheduler.TimeScheduler;
import org.apache.avalon.cornerstone.services.scheduler.TimeTrigger;
import org.apache.avalon.cornerstone.services.scheduler.TriggerFailureListener;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Default implementation of TimeScheduler service.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.scheduler.TimeScheduler"
 * @phoenix:mx name="org.apache.avalon.cornerstone.blocks.scheduler.MonitorableTimeSchedulerMBean"
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 * @author Paul Hammant
 */
public class DefaultTimeScheduler
    extends AbstractLogEnabled
    implements TimeScheduler, Serviceable, Startable, Disposable, Runnable, MonitorableTimeSchedulerMBean
{
    private final Hashtable m_entries = new Hashtable();
    private final PriorityQueue m_priorityQueue =
        new SynchronizedPriorityQueue( new BinaryHeap() );
    private ThreadManager m_threadManager;
    private boolean m_running;
    private ArrayList m_triggerFailureListeners = new ArrayList();

    /**
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.threads.ThreadManager"
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_threadManager = (ThreadManager)serviceManager.lookup( ThreadManager.ROLE );
    }

    public void dispose()
    {
        m_entries.clear();
        m_priorityQueue.clear();
    }

    /**
     * Schedule a time based trigger.
     * Note that if a TimeTrigger already has same name then it is removed.
     *
     * @param name the name of the trigger
     * @param trigger the trigger
     * @param target the target
     */
    public synchronized void addTrigger( final String name,
                                         final TimeTrigger trigger,
                                         final Target target )
    {
        try
        {
            removeTrigger( name );
        }
        catch( final NoSuchElementException nse )
        {
        }

        final TimeScheduledEntry entry = new TimeScheduledEntry( name, trigger, target );
        m_entries.put( name, entry );
        final boolean added = rescheduleEntry( entry, false );

        if( !added ) return;

        try
        {
            if( entry == m_priorityQueue.peek() )
            {
                notifyAll();
            }
        }
        catch( final NoSuchElementException nse )
        {
            final String message =
                "Unexpected exception when peek() on priority queue for " +
                entry.getName();
            getLogger().warn( message, nse );
        }
    }

    /**
     * Remove a scheduled trigger by name.
     *
     * @param name the name of the trigger
     * @exception NoSuchElementException if no trigger exists with that name
     */
    public synchronized void removeTrigger( String name )
        throws NoSuchElementException
    {
        //use the kill-o-matic against any entry with same name
        final TimeScheduledEntry entry = getEntry( name );
        entry.invalidate();
        m_entries.remove( name );
    }

    /**
     * Force a trigger time to be recalculated.
     *
     * @param name the name of the trigger
     * @exception NoSuchElementException if no trigger exists with that name
     */
    public synchronized void resetTrigger( final String name )
        throws NoSuchElementException
    {
        final TimeScheduledEntry entry = getEntry( name );
        entry.getTimeTrigger().reset();
        rescheduleEntry( entry, true );
    }

    /**
     * Reschedule an entry.
     * if clone is true then invalidate old version and create a new entry to
     * insert into queue.
     *
     * @param timeEntry the entry
     * @param clone true if new entry is to be created
     * @return true if added to queue, false if not added
     */
    private synchronized boolean rescheduleEntry( final TimeScheduledEntry timeEntry,
                                                  final boolean clone )
    {
        TimeScheduledEntry entry = timeEntry;

        if( clone )
        {
            entry = new TimeScheduledEntry( timeEntry.getName(),
                                            timeEntry.getTimeTrigger(),
                                            timeEntry.getTarget() );
            timeEntry.invalidate();

            // remove old refernce to the entry..so that next time
            // somebody calls getEntry( name ), we will get the new valid entry.
            m_entries.remove( timeEntry.getName() );
            m_entries.put( timeEntry.getName(), entry );
        }

        //reschedule if appropriate
        final long next = entry.getTimeTrigger().getTimeAfter( System.currentTimeMillis() );

        if( 0 < next )
        {
            entry.setNextTime( next );
            m_priorityQueue.insert( entry );

            if( entry == m_priorityQueue.peek() )
            {
                notify();
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Retrieve entry from set.
     *
     * @param name the name of entry
     * @return the entry
     * @exception NoSuchElementException if no entry is found with that name
     */
    private TimeScheduledEntry getEntry( final String name )
        throws NoSuchElementException
    {
        //use the kill-o-matic against any entry with same name
        final TimeScheduledEntry entry = (TimeScheduledEntry)m_entries.get( name );
        if( null != entry )
        {
            return entry;
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    /**
     * Rune entry in a separate thread.
     *
     * @param entry the entry to run
     */
    private void runEntry( final TimeScheduledEntry entry )
    {
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                doRunEntry( entry );
            }
        };

        //this should suck threads from a named pool
        try
        {
            m_threadManager.getDefaultThreadPool().execute( runnable );
        }
        catch( final Exception e )
        {
            final String message = "Error executing trigger " + entry.getName();
            getLogger().warn( message, e );
        }
    }

    /**
     * Helper method delegated to to run in a separate thread.
     *
     * @param entry the entry to run
     */
    private void doRunEntry( final TimeScheduledEntry entry )
    {
        try
        {
            entry.getTarget().targetTriggered( entry.getName() );
        }
        catch( final Error e)
        {
            final String message = "Error occured executing trigger " + entry.getName();
            getLogger().error( message, e );
            notifyFailedTriggers(e);

        }
        catch( final Exception e )
        {
            final String message = "Exception occured executing trigger " + entry.getName();
            getLogger().warn( message, e );
            notifyFailedTriggers(e);
        }
    }

    public void start()
        throws Exception
    {
        //this should suck threads from a named pool
        m_threadManager.getDefaultThreadPool().execute( this );
    }

    public void stop()
    {
        m_running = false;
        synchronized( this )
        {
            notifyAll();
        }
    }

    /**
     * Entry point for thread that monitors entrys and triggers
     * entrys when necessary.
     */
    public void run()
    {
        m_running = true;

        while( m_running )
        {
            long duration = 0;

            if( !m_priorityQueue.isEmpty() )
            {
                TimeScheduledEntry entry = null;
                synchronized( this )
                {
                    entry = getNextEntry();
                    if( null == entry ) continue;

                    duration = entry.getNextTime() - System.currentTimeMillis();

                    if( duration < 0 )
                    {
                        //time to run job so remove it from priority queue
                        //and run it
                        m_priorityQueue.pop();

                        //Note that we need the pop to occur in a
                        //synchronized section while the runEntry
                        //does not need to be synchronized
                        //hence why there is to if statements
                        //structured in this ugly way
                    }
                }

                if( duration < 0 )
                {
                    runEntry( entry );
                    rescheduleEntry( entry, false );
                    continue;
                }
                else if( 0 == duration )
                {
                    //give a short duration that will sleep
                    // so that next loop will definetly be below 0.
                    //Can not act on zero else multiple runs could go through
                    //at once
                    duration = 1;
                }
            }

            //wait/sleep until monitor is signalled which occurs when
            //next jobs is likely to occur or when a new job gets added to
            //top of heap
            try
            {
                synchronized( this )
                {
                    wait( duration );
                }
            }
            catch( final InterruptedException ie )
            {
            }
        }
    }

    /**
     * Retrieve next valid entry. It will pop off any
     * invalid entrys until the heap is empty or a valid entry
     * is found.
     *
     * @return the next valid entry or null if none
     */
    private synchronized TimeScheduledEntry getNextEntry()
    {
        TimeScheduledEntry entry =
            (TimeScheduledEntry)m_priorityQueue.peek();

        //if job has been invalidated then remove it and continue
        while( !entry.isValid() )
        {
            m_priorityQueue.pop();

            if( m_priorityQueue.isEmpty() )
            {
                return null;
            }

            entry = (TimeScheduledEntry)m_priorityQueue.peek();
        }

        return entry;
    }

    /**
     * Add a trigger failure listener
     * @param listener The listener
     */
    public void addTriggerFailureListener(TriggerFailureListener listener)
    {
        m_triggerFailureListeners.add(listener);
    }

    /**
     * Remove a trigger failure listener
     * @param listener The listener
     */
    public void removeTriggerFailureListener(TriggerFailureListener listener)
    {
        m_triggerFailureListeners.remove(listener);
    }

    private void notifyFailedTriggers(Throwable t) {
        for (int i = 0; i < m_triggerFailureListeners.size(); i++)
        {
            TriggerFailureListener triggerFailureListener = (TriggerFailureListener) m_triggerFailureListeners.get(i);
            triggerFailureListener.triggerFailure(t);
        }

    }


    /**
     * Return a collection of the triggerable names.
     * @return
     */
    public synchronized Collection getEntries() {
        Collection coll = m_entries.keySet();
        Vector retval = new Vector();
        for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
            TimeScheduledEntry tse = (TimeScheduledEntry) m_entries.get((String) iterator.next());
            retval.add(tse.toString());
        }
        return retval;
    }


}

