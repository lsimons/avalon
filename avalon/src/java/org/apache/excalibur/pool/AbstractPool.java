/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

import java.util.List;
import java.util.ArrayList;
import org.apache.excalibur.concurrent.Lock;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.thread.ThreadSafe;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public abstract class AbstractPool
    extends AbstractLoggable
    implements Pool, ThreadSafe
{
    public final static int        DEFAULT_POOL_SIZE  = 8;
    protected final ObjectFactory  m_factory;
    protected List                 m_active           = new ArrayList();
    protected List                 m_ready            = new ArrayList();
    protected Lock                 m_mutex            = new Lock();
    protected boolean              m_initialized      = false;
    protected int                  m_count            = 0;

    /**
     * Create an AbstractPool.  The pool requires a factory, and can
     * optionally have a controller.
     */
    public AbstractPool( final ObjectFactory factory ) throws Exception
    {
        m_factory = factory;

        if( !(this instanceof Initializable) )
        {
            initialize();
        }
    }

    protected void initialize()
        throws Exception
    {
        this.m_mutex.lock();

        for( int i = 0; i < AbstractPool.DEFAULT_POOL_SIZE; i++ )
        {
            m_ready.add( m_factory.newInstance() );
            m_count++;
        }

        this.m_initialized = true;

        this.m_mutex.unlock();
    }

    public int size() {
        return m_count;
    }

    public abstract Poolable get() throws Exception;

    public abstract void put( final Poolable obj );
}
