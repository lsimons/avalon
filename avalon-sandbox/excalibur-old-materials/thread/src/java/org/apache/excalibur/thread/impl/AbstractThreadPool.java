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
import org.apache.excalibur.thread.ThreadPool;

/**
 * This is the base class of all ThreadPools.
 * Sub-classes should implement the abstract methods to
 * retrieve and return Threads to the pool.
 *
 * @author <a href="mailto:avalon-dev@jakarta.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public abstract class AbstractThreadPool
    implements ThreadPool
{
    /**
     * The thread group associated with pool.
     */
    private final ThreadGroup m_threadGroup;

    /**
     * The name of the thread pool.
     * Used in naming threads.
     */
    private final String m_name;

    /**
     * A Running number that indicates the number
     * of threads created by pool. Starts at 0 and
     * increases.
     */
    private int m_level;

    /**
     * Create a ThreadPool with the specified name.
     *
     * @param name the name of thread pool (appears in thread group
     *             and thread names)
     * @throws Exception if unable to create pool
     */
    public AbstractThreadPool( final String name,
                               final ThreadGroup threadGroup )
        throws Exception
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == threadGroup )
        {
            throw new NullPointerException( "threadGroup" );
        }

        m_name = name;
        m_threadGroup = threadGroup;
    }

    /**
     * Destroy a worker thread by scheduling it for shutdown.
     *
     * @param thread the worker thread
     */
    protected void destroyWorker( final WorkerThread thread )
    {
        thread.dispose();
    }

    /**
     * Create a WorkerThread and start it up.
     *
     * @return the worker thread.
     */
    protected WorkerThread createWorker()
    {
        final String name = m_name + " Worker #" + m_level++;

        final WorkerThread worker = newWorkerThread( name );
        worker.setDaemon( true );
        worker.start();
        return worker;
    }

    /**
     * Create a new worker for pool.
     *
     * @param name the name of worker
     * @return the new WorkerThread
     */
    protected WorkerThread newWorkerThread( final String name )
    {
        return new WorkerThread( this, m_threadGroup, name );
    }

    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    public ThreadControl execute( final Runnable work )
    {
        return execute( new ExecutableRunnable( work ) );
    }

    /**
     * Execute some executable work in a thread.
     *
     * @param work the work
     * @return the ThreadControl
     */
    public ThreadControl execute( final Executable work )
    {
        final WorkerThread worker = getWorker();
        return worker.execute( work );
    }

    /**
     * Get the name used for thread pool.
     * (Used in naming threads).
     *
     * @return the thread pool name
     */
    protected String getName()
    {
        return m_name;
    }

    /**
     * Return the thread group that thread pool is associated with.
     *
     * @return the thread group that thread pool is associated with.
     */
    protected ThreadGroup getThreadGroup()
    {
        return m_threadGroup;
    }

    /**
     * Retrieve a worker thread from pool.
     *
     * @return the worker thread retrieved from pool
     */
    protected abstract WorkerThread getWorker();

    /**
     * Return the WorkerThread to the pool.
     *
     * @param worker the worker thread to put back in pool
     */
    protected abstract void releaseWorker( final WorkerThread worker );
}
