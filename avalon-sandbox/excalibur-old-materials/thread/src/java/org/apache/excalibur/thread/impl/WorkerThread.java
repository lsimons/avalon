/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
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

package org.apache.excalibur.thread.impl;

import org.apache.excalibur.thread.Executable;
import org.apache.excalibur.thread.ThreadControl;

/**
 * This class extends the Thread class to add recyclable functionalities.
 *
 * @author <a href="mailto:avalon-dev@jakarta.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class WorkerThread
    extends Thread
{
    /**
     * The work currentlyy associated with worker (May be null).
     */
    private Executable m_work;

    /**
     * The thread control associated with current work.
     * Should be null if work is null.
     */
    private DefaultThreadControl m_threadControl;

    /**
     * True if this thread is alive and not scheduled for shutdown.
     */
    private boolean m_alive;

    /**
     * The name of thread.
     */
    private final String m_name;

    /**
     * The thread pool this thread is associated with.
     */
    private final AbstractThreadPool m_pool;

    /**
     * Allocates a new <code>Worker</code> object.
     */
    protected WorkerThread( final AbstractThreadPool pool,
                            final ThreadGroup group,
                            final String name )
    {
        super( group, "" );
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == pool )
        {
            throw new NullPointerException( "pool" );
        }

        m_name = name;
        m_work = null;
        m_alive = true;
        m_pool = pool;

        setDaemon( false );
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
                preExecute();
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
                postExecute();
            }

            //should this be just notify or notifyAll ???
            //It seems to resource intensive option to use notify()
            //notifyAll();
            notify();

            // recycle ourselves
            recycleThread();
        }
    }

    /**
     * Implement this method to replace thread back into pool.
     */
    protected void recycleThread()
    {
        if( m_alive )
        {
            m_pool.releaseWorker( this );
        }
    }

    /**
     * Overide this method to execute something after
     * each bit of "work".
     */
    protected void postExecute()
    {
    }

    /**
     * Overide this method to execute something before
     * each bit of "work".
     */
    protected void preExecute()
    {
        //TODO: Thread name setting should reuse the
        //ThreadContext code if ThreadContext used.
        Thread.currentThread().setName( m_name );
    }

    /**
     * Set the <tt>alive</tt> variable to false causing the worker to die.
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

    /**
     * Set the <tt>Work</tt> code this <tt>Worker</tt> must
     * execute and <i>notifies</i> its thread to do it.
     */
    protected synchronized ThreadControl execute( final Executable work )
    {
        m_work = work;
        m_threadControl = new DefaultThreadControl( this );

        debug( "notifying this worker." );
        notify();

        return m_threadControl;
    }

    /**
     * Set the <tt>Work</tt> code this <tt>Worker</tt> must
     * execute and <i>notifies</i> its thread to do it. Wait
     * until the executable has finished before returning.
     */
    protected synchronized void executeAndWait( final Executable work )
    {
        execute( work );
        waitUntilCondition( false );
    }

    /**
     * Wait until the worker either has work or doesn't have work.
     *
     * @param hasWork true if waiting till work is present, false otherwise
     */
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

    /**
     * Write a debug message.
     * A Noop oin this implementation. Subclasses can overide
     * to actually do some logging.
     *
     * @param message the message to write out
     */
    protected void debug( final String message )
    {
        if( false )
        {
            final String output = getName() + ": " + message;
            System.out.println( output );
        }
    }
}
