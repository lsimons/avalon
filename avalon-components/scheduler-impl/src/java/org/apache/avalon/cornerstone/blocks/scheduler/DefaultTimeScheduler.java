/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Components", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.cornerstone.blocks.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.List;
import java.util.Map;

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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class DefaultTimeScheduler
    extends AbstractLogEnabled
    implements TimeScheduler, Serviceable, Startable, Disposable, Runnable, MonitorableTimeSchedulerMBean
{
    // ----------------------------------------------------------------------
    //  Properties
    // ----------------------------------------------------------------------
    private final Hashtable m_entries = new Hashtable();
    private final PriorityQueue m_priorityQueue =
        new SynchronizedPriorityQueue( new BinaryHeap() );
    private ThreadManager m_threadManager;
    private boolean m_running;
    private ArrayList m_triggerFailureListeners = new ArrayList();

    // ----------------------------------------------------------------------
    //  Getter/Setter methods
    // ----------------------------------------------------------------------
    //
    // LSD: these have been added in to allow subclasses of the
    // DefaultScheduler to override implementation behaviour.
    // You should *not* make these public in subclasses (hence
    // they are final); they're here for convenience implementation
    // only.

    protected final ThreadManager getThreadManager()
    {
        return m_threadManager;
    }

    protected final boolean isRunning()
    {
        return m_running;
    }

    protected final void setRunning( boolean running )
    {
        m_running = running;
    }

    protected final List getTriggerFailureListeners()
    {
        return m_triggerFailureListeners;
    }

    protected final Map getEntryMap()
    {
        return m_entries;
    }

    protected final PriorityQueue getPriorityQueue()
    {
        return m_priorityQueue;
    }

    // ----------------------------------------------------------------------
    //  Avalon Lifecycle
    // ----------------------------------------------------------------------
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_threadManager = (ThreadManager)serviceManager.lookup( ThreadManager.ROLE );
    }

    public void dispose()
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "disposal" );
        }
        m_entries.clear();
        m_priorityQueue.clear();
    }

    public void start()
        throws Exception
    {
        //this should suck threads from a named pool
        getThreadManager().getDefaultThreadPool().execute( this );
    }

    public void stop()
    {
        m_running = false;
        synchronized( this )
        {
            notifyAll();
        }
    }

    // ----------------------------------------------------------------------
    //  Work Interface: Runnable
    // ----------------------------------------------------------------------
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

            if( !getPriorityQueue().isEmpty() )
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
                        getPriorityQueue().pop();

                        //Note that we need the pop to occur in a
                        //synchronized section while the runEntry
                        //does not need to be synchronized
                        //hence why there is to if statements
                        //structured in this ugly way
                    }
                }

                if( duration < 0 )
                {
                    // runs and reschedules the entry
                    runEntry( entry );                    
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

    // ----------------------------------------------------------------------
    //  Work Interface: Time Scheduler
    // ----------------------------------------------------------------------
    /**
     * Add a trigger failure listener
     * @param listener The listener
     */
    public void addTriggerFailureListener( TriggerFailureListener listener )
    {
        getTriggerFailureListeners().add( listener );
    }

    /**
     * Remove a trigger failure listener
     * @param listener The listener
     */
    public void removeTriggerFailureListener( TriggerFailureListener listener )
    {
        getTriggerFailureListeners().remove( listener );
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
        getEntryMap().put( name, entry );
        final boolean added = rescheduleEntry( entry, false );

        if( !added ) return;

        try
        {
            if( entry == getPriorityQueue().peek() )
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
        getEntryMap().remove( name );
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

    // ----------------------------------------------------------------------
    //  Work Interface: MonitorableTimeSchedulerMBean
    // ----------------------------------------------------------------------

    /**
     * Return a collection of the triggerable names.
     * @return
     */
    public synchronized Collection getEntries()
    {
        Collection coll = getEntryMap().keySet();
        Vector retval = new Vector();
        for( Iterator iterator = coll.iterator(); iterator.hasNext(); )
        {
            TimeScheduledEntry tse = (TimeScheduledEntry)getEntryMap().get( iterator.next() );
            retval.add( tse.toString() );
        }
        return retval;
    }

    // ----------------------------------------------------------------------
    //  Helper methods
    // ----------------------------------------------------------------------

    /**
     * Reschedule an entry.
     * if clone is true then invalidate old version and create a new entry to
     * insert into queue.
     *
     * @param timeEntry the entry
     * @param clone true if new entry is to be created
     * @return true if added to queue, false if not added
     */
    protected synchronized boolean rescheduleEntry( final TimeScheduledEntry timeEntry,
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
            getEntryMap().remove( timeEntry.getName() );
            getEntryMap().put( timeEntry.getName(), entry );
        }

        //reschedule if appropriate
        final long next = entry.getTimeTrigger().getTimeAfter( System.currentTimeMillis() );

        if( 0 < next )
        {
            entry.setNextTime( next );
            getPriorityQueue().insert( entry );

            if( entry == getPriorityQueue().peek() )
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
    protected TimeScheduledEntry getEntry( final String name )
        throws NoSuchElementException
    {
        //use the kill-o-matic against any entry with same name
        final TimeScheduledEntry entry = (TimeScheduledEntry)getEntryMap().get( name );
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
     * Run entry in a separate thread and reschedule it.
     *
     * @param entry the entry to run
     */
    protected void runEntry( final TimeScheduledEntry entry )
    {
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                doRunEntry( entry );
                // Stefan Seifert:
                // rescheduleEntry( entry, false );
                //
                // and then don't reschedule at the end of runEntry
                // this will ensure long-running events are
                // queued
                //
                // LSD:
                // that might break other apps. No-can-do.
            }
        };

        //this should suck threads from a named pool
        try
        {
            getThreadManager().getDefaultThreadPool().execute( runnable );
        }
        catch( final Exception e )
        {
            final String message = "Error executing trigger " + entry.getName();
            getLogger().warn( message, e );
        }
        
				// reschedule entry
				rescheduleEntry( entry, false );
    }

    /**
     * Helper method delegated to to run in a separate thread.
     *
     * @param entry the entry to run
     */
    protected void doRunEntry( final TimeScheduledEntry entry )
    {
        try
        {
            entry.getTarget().targetTriggered( entry.getName() );
        }
        catch( final Error e )
        {
            final String message = "Error occured executing trigger " + entry.getName();
            getLogger().error( message, e );
            notifyFailedTriggers( e );

        }
        catch( final Exception e )
        {
            final String message = "Exception occured executing trigger " + entry.getName();
            getLogger().warn( message, e );
            notifyFailedTriggers( e );
        }
    }

    /**
     * Retrieve next valid entry. It will pop off any
     * invalid entrys until the heap is empty or a valid entry
     * is found.
     *
     * @return the next valid entry or null if none
     */
    protected synchronized TimeScheduledEntry getNextEntry()
    {
        TimeScheduledEntry entry =
            (TimeScheduledEntry)getPriorityQueue().peek();

        //if job has been invalidated then remove it and continue
        while( !entry.isValid() )
        {
            getPriorityQueue().pop();

            if( getPriorityQueue().isEmpty() )
            {
                return null;
            }

            entry = (TimeScheduledEntry)getPriorityQueue().peek();
        }

        return entry;
    }

    protected void notifyFailedTriggers( Throwable t )
    {
        for( int i = 0; i < getTriggerFailureListeners().size(); i++ )
        {
            TriggerFailureListener triggerFailureListener = (TriggerFailureListener)m_triggerFailureListeners.get( i );
            triggerFailureListener.triggerFailure( t );
        }

    }
}

