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

package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.thread.ThreadControl;
import org.apache.excalibur.thread.impl.AbstractThreadPool;
import org.apache.excalibur.thread.impl.WorkerThread;
import org.apache.excalibur.threadcontext.ThreadContext;

/**
 * The ThreadPool that binds to Legacy Pooling implementation.
 *
 * @author <a href="mailto:avalon-dev@jakarta.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @deprecated Only used by deprecated components. Will remove in the future
 */
class BasicThreadPool
    extends AbstractThreadPool
    implements ObjectFactory, LogEnabled, Disposable, ThreadPool
{
    /**
     * The underlying pool.
     */
    private Pool m_pool;

    /**
     * The logger to use for debugging purposes.
     */
    private Logger m_logger;

    /**
     * The base ThreadContext that can be duplicated for
     * each thread.
     */
    private ThreadContext m_context;

    /**
     * Create a new ThreadPool with specified capacity.
     *
     * @param threadGroup the thread group used in pool
     * @param name the name of pool (used in naming threads)
     * @param pool the underling pool
     * @param context the thread context associated with pool (May be null).
     * @throws Exception if unable to create pool
     */
    public BasicThreadPool( final ThreadGroup threadGroup,
                            final String name,
                            final Pool pool,
                            final ThreadContext context )
        throws Exception
    {
        super( name, threadGroup );
        if( null == pool )
        {
            throw new NullPointerException( "pool" );
        }

        m_pool = pool;
        m_context = context;
    }

    /**
     * Setup Logging.
     *
     * @param logger the logger
     */
    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
        ContainerUtil.enableLogging( m_pool, logger );
    }

    /**
     * Dispose of underlying pool and cleanup resources.
     */
    public void dispose()
    {
        ContainerUtil.dispose( m_pool );
        m_pool = null;
    }

    /**
     * Create new Poolable instance.
     *
     * @return the new Poolable instance
     */
    public Object newInstance()
    {
        return createWorker();
    }

    /**
     * Overide newWorkerThread to provide a WorkerThread
     * that is Poolable and LogEnabled.
     *
     * @param name the name of WorkerThread
     * @return the created WorkerThread
     */
    protected WorkerThread newWorkerThread( final String name )
    {
        ThreadContext context = null;
        if( null != m_context )
        {
            context = m_context.duplicate();
        }
        final SimpleWorkerThread thread =
            new SimpleWorkerThread( this, getThreadGroup(), name, context );
        ContainerUtil.enableLogging( thread, m_logger );
        return thread;
    }

    public void decommission( final Object object )
    {
        if( object instanceof WorkerThread )
        {
            destroyWorker( (WorkerThread)object );
        }
    }

    /**
     * Return the class of poolable instance.
     *
     * @return the class of poolable instance.
     */
    public Class getCreatedClass()
    {
        return SimpleWorkerThread.class;
    }

    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    public ThreadControl execute( final Executable work )
    {
        return execute( new ExecutableExecuteable( work ) );
    }

    /**
     * Retrieve a worker thread from pool.
     *
     * @return the worker thread retrieved from pool
     */
    protected WorkerThread getWorker()
    {
        try
        {
            return (WorkerThread)m_pool.get();
        }
        catch( final Exception e )
        {
            final String message =
                "Unable to access thread pool due to " + e;
            throw new IllegalStateException( message );
        }
    }

    /**
     * Release worker back into pool.
     *
     * @param worker the worker (Should be a {@link SimpleWorkerThread}).
     */
    protected void releaseWorker( final WorkerThread worker )
    {
        worker.interrupted();
        m_pool.put( (SimpleWorkerThread)worker );
    }
}
