/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.SoftResourceLimitingPool;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.thread.ThreadControl;
import org.apache.excalibur.threadcontext.ThreadContext;

/**
 * This class is the public frontend for the thread pool code.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class DefaultThreadPool
    extends ThreadGroup
    implements ObjectFactory, Loggable, LogEnabled, Disposable, ThreadPool
{
    private final BasicThreadPool m_pool;
    private SoftResourceLimitingPool m_underlyingPool;

    public DefaultThreadPool( final int capacity )
        throws Exception
    {
        this( "Worker Pool", capacity );
    }

    public DefaultThreadPool( final String name, final int capacity )
        throws Exception
    {
        this( name, capacity, null );
    }

    public DefaultThreadPool( final String name,
                              final int capacity,
                              final ThreadContext context )
        throws Exception
    {
        super( name );
        m_underlyingPool = new SoftResourceLimitingPool( this, capacity );
        m_pool = new BasicThreadPool( this, name, m_underlyingPool, context );
    }

    public void setLogger( final org.apache.log.Logger logger )
    {
        enableLogging( new LogKitLogger( logger ) );
    }

    public void enableLogging( final Logger logger )
    {
        ContainerUtil.enableLogging( m_pool, logger );
    }

    public void dispose()
    {
        m_pool.dispose();
    }

    public Object newInstance()
    {
        return m_pool.newInstance();
    }

    public void decommission( final Object object )
    {
        m_pool.decommission( object );
    }

    public Class getCreatedClass()
    {
        return m_pool.getCreatedClass();
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
        return m_pool.execute( work );
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
        return m_pool.execute( work );
    }

    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    public ThreadControl execute( final org.apache.excalibur.thread.Executable work )
    {
        return m_pool.execute( work );
    }
}
