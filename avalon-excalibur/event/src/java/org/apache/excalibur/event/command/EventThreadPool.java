/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.excalibur.event.command;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.excalibur.mpool.BlockingFixedSizePool;
import org.apache.excalibur.mpool.ObjectFactory;
import org.apache.excalibur.thread.ThreadPool;
import org.apache.excalibur.thread.impl.AbstractThreadPool;
import org.apache.excalibur.thread.impl.WorkerThread;

/**
 * This class is the public frontend for the thread pool code.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class EventThreadPool
    extends AbstractThreadPool
    implements ObjectFactory, Disposable, ThreadPool
{
    private BlockingFixedSizePool m_pool;

    public EventThreadPool( final int capacity )
        throws Exception
    {
        this( "Worker Pool", capacity );
    }

    public EventThreadPool( final String name, final int capacity )
        throws Exception
    {
        this( name, capacity, 1000 );
    }

    public EventThreadPool( final String name,
                            final int capacity,
                            final int timeout )
        throws Exception
    {
        super( name, new ThreadGroup( name ) );
        m_pool = new BlockingFixedSizePool( this, capacity, timeout );
        m_pool.initialize();
    }

    public void dispose()
    {
        m_pool.dispose();
        m_pool = null;
    }

    public Object newInstance()
    {
        return createWorker();
    }

    public void dispose( final Object object )
    {
        if( object instanceof WorkerThread )
        {
            destroyWorker( (WorkerThread)object );
        }
    }

    public Class getCreatedClass()
    {
        return WorkerThread.class;
    }

    /**
     * Retrieve a worker thread from pool.
     *
     * @return the worker thread retrieved from pool
     */
    protected WorkerThread getWorker()
    {
        final WorkerThread thread = (WorkerThread)m_pool.acquire();
        if( null == thread )
        {
            final String message =
                "Unable to access thread pool due to timeout exceeded";
            throw new IllegalStateException( message );
        }

        return thread;
    }

    protected void releaseWorker( final WorkerThread worker )
    {
        try
        {
            m_pool.release( worker );
        }
        catch( Throwable e )
        {
            // trying to figure out why a NullPointer exeception can occur ...
            final String error = "Unexpected condition while releasing worker: " + worker );
            e.printStackTrace();
        }
    }
}
