/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.thread;

import org.apache.avalon.Loggable;
import org.apache.avalon.Poolable;
import org.apache.excalibur.pool.SoftResourceLimitingPool;
import org.apache.log.Logger;

/**
 * This class extends the Thread class to add recyclable functionalities.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class WorkerThread
    extends Thread
    implements Poolable, Loggable
{
    protected final static boolean  DEBUG          = false;

    protected Logger                    m_logger;
    protected ThreadPool                m_threadPool;
    protected SoftResourceLimitingPool  m_pool;

    protected Runnable                  m_work;
    protected boolean                   m_alive;

    /**
     * Allocates a new <code>Worker</code> object.
     */
    protected WorkerThread( final ThreadGroup group,
                            final ThreadPool threadPool,
                            final SoftResourceLimitingPool pool,
                            final String name )
    {
        super( group, name );

        m_threadPool = threadPool;
        m_pool = pool;

        m_work = null;
        m_alive = true;
        setDaemon( false );
    }

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * The main execution loop.
     */
    public final synchronized void run()
    {
        ThreadContext.setCurrentThreadPool( m_threadPool );

        if( DEBUG ) m_logger.info( getName() + ": starting." );

        // Notify the pool this worker started running.
        //notifyAll();

        while( m_alive )
        {
            waitUntilCondition( true );

            if( DEBUG ) m_logger.debug( getName() + ": running." );

            try
            {
                m_work.run();
            }
            catch( final ThreadDeath td )
            {
                if ( DEBUG ) m_logger.debug( getName() + ": thread has died." );

                // This is to let the thread death propagate to the runtime
                // enviroment to let it know it must kill this worker
                throw td;
            }
            catch( final Throwable t )
            {
                // Error thrown while working.
                if( DEBUG ) m_logger.debug( getName() + ": error caught: " + t );
                // XXX: what should we do when this happens?
            }

            if( DEBUG ) m_logger.debug( getName() + ": done." );

            m_work = null;

            //should this be just notify or notifyAll ???
            //It seems to resource intensive option to use notify()
            //notifyAll();
            notify();

            // recycle ourselves
            if( null != m_pool )
            {
                m_pool.put( this );
            }
            else
            {
                m_alive = false;
            }
        }
    }

    /**
     * Set the <code>Work</code> code this <code>Worker</code> must
     * execute and <i>notifies</i> its thread to do it.
     */
    protected synchronized void executeAndWait( final Runnable work )
    {
        execute( work );
        waitUntilCondition( false );
    }

    protected synchronized void waitUntilCondition( final boolean hasWork )
    {
        while( hasWork == (null == m_work) )
        {
            try
            {
                if( DEBUG ) m_logger.debug( getName() + ": waiting." );
                wait();
                if( DEBUG ) m_logger.debug( getName() + ": notified." );
            }
            catch( final InterruptedException ie ) {}
        }
    }

    protected synchronized void execute( final Runnable work )
    {
        if( DEBUG ) m_logger.debug( getName() + ": notifying this worker." );
        m_work = work;
        notify();
    }

    /**
     * Set the <code>alive</code> variable to false causing the worker to die.
     * If the worker is stalled and a timeout generated this call, this method
     * does not change the state of the worker (that must be destroyed in other
     * ways).
     */
    public void dispose()
    {
        if( DEBUG ) m_logger.debug( getName() + ": destroying." );
        m_alive = false;
    }
}
