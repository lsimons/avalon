/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.thread.impl.AbstractThreadPool;
import org.apache.excalibur.thread.impl.WorkerThread;
import org.apache.excalibur.threadcontext.ThreadContext;

/**
 * This class extends the Thread class to add recyclable functionalities.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
class SimpleWorkerThread
    extends WorkerThread
    implements Poolable, LogEnabled
{
    private Logger m_logger;
    private ThreadContext m_context;

    /**
     * Allocates a new <code>Worker</code> object.
     */
    protected SimpleWorkerThread( final AbstractThreadPool pool,
                                  final ThreadGroup group,
                                  final String name,
                                  final ThreadContext context )
    {
        super( pool, group, name );
        m_context = context;
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    protected void postExecute()
    {
        super.postExecute();
        ThreadContext.setThreadContext( null );
    }

    protected void preExecute()
    {
        super.preExecute();
        ThreadContext.setThreadContext( m_context );
    }

    protected void debug( final String message )
    {
        if( false )
        {
            final String output = getName() + ": " + message;
            m_logger.debug( output );
            //System.out.println( output );
        }
    }
}
