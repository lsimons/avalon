/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.thread.ThreadControl;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.threadcontext.ThreadContext;

/**
 * This class extends the Thread class to add recyclable functionalities.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
class WorkerThread
    extends Thread
    implements Poolable, LogEnabled
{
    private Logger m_logger;
    private Pool m_pool;

    private Executable m_work;
    private DefaultThreadControl m_threadControl;
    private ThreadContext m_context;
    private boolean m_alive;

    /**
     * Allocates a new <code>Worker</code> object.
     */
    protected WorkerThread( final ThreadGroup group,
                            final String name,
                            final Pool pool,
                            final ThreadContext context )
    {
        super( group, name );

        m_pool = pool;
        m_context = context;

        m_work = null;

        m_alive = true;

        setDaemon( false );
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * The main execution loop.
     */
    public final synchronized void run()
    {
        debug( "starting." );

        // Notify the pool this worker started running.
        //notifyAll();

        while( m_alive )
        {
            waitUntilCondition( true );

            debug( "running." );

            try
            {
                if( null != m_context ) ThreadContext.setThreadContext( m_context );
                m_work.execute();
                m_threadControl.finish( null );
            }
            catch( final ThreadDeath threadDeath )
            {
                debug( "thread has died." );
                m_threadControl.finish( threadDeath );
                // This is to let the thread death propagate to the runtime
                // enviroment to let it know it must kill this worker
                throw threadDeath;
            }
            catch( final Throwable throwable )
            {
                // Error thrown while working.
                debug( "error caught: " + throwable );
                m_threadControl.finish( throwable );
            }
            finally
            {
                debug( "done." );
                m_work = null;
                m_threadControl = null;
                if( null != m_context ) ThreadContext.setThreadContext( null );
            }

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
     * Set the <code>alive</code> variable to false causing the worker to die.
     * If the worker is stalled and a timeout generated this call, this method
     * does not change the state of the worker (that must be destroyed in other
     * ways).
     */
    public void dispose()
    {
        debug( "destroying." );
        m_alive = false;
        waitUntilCondition( false );
    }

    protected synchronized ThreadControl execute( final Executable work )
    {
        m_work = work;
        m_threadControl = new DefaultThreadControl( this );

        debug( "notifying this worker." );
        notify();

        return m_threadControl;
    }

    /**
     * Set the <code>Work</code> code this <code>Worker</code> must
     * execute and <i>notifies</i> its thread to do it.
     */
    protected synchronized void executeAndWait( final Executable work )
    {
        execute( work );
        waitUntilCondition( false );
    }

    private synchronized void waitUntilCondition( final boolean hasWork )
    {
        while( hasWork == ( null == m_work ) )
        {
            try
            {
                debug( "waiting." );
                wait();
                debug( "notified." );
            }
            catch( final InterruptedException ie )
            {
            }
        }
    }

    private void debug( final String message )
    {
        if( false )
        {
            final String output = getName() + ": " + message;
            m_logger.debug( output );
            //System.out.println( output );
        }
    }
}
