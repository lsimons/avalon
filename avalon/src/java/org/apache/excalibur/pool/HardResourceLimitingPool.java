/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

import org.apache.avalon.activity.Initializable;
import org.apache.avalon.thread.ThreadSafe;

/**
 * This is a implementation of  <code>Pool</code> that is thread safe.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class HardResourceLimitingPool
    extends SoftResourceLimitingPool
    implements ThreadSafe, Initializable
{
    public final static int           DEFAULT_POOL_SIZE           = 8;

    public HardResourceLimitingPool( final ObjectFactory factory, final PoolController controller )
        throws Exception
    {
        super( factory, controller, DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE );
    }

    public HardResourceLimitingPool( final ObjectFactory factory )
        throws Exception
    {
        this( factory, null );
    }

    public HardResourceLimitingPool( final ObjectFactory factory,
                                     final int initial,
                                     final int maximum  )
        throws Exception
    {
        super( factory, null, initial, maximum );
    }

    public HardResourceLimitingPool( final ObjectFactory factory, final int initial )
        throws Exception
    {
        this( factory, initial, initial );
    }

    public HardResourceLimitingPool( final Class clazz, final int initial, final int maximum )
        throws NoSuchMethodException, Exception
    {
        this( new DefaultObjectFactory( clazz ), initial, maximum );
    }

    public HardResourceLimitingPool( final Class clazz, final int initial )
        throws NoSuchMethodException, Exception
    {
        this( clazz, initial, initial );
    }

    public void initialize() {
        try {
            super.initialize();
        } catch (Exception e) {
            getLogger().debug("Caught init exception", e);
        }
    }

    protected final Poolable newPoolable() throws Exception
    {
        if (m_count < m_max)
        {
            m_count++;
            return (Poolable) this.m_factory.newInstance();
        }

        throw new InstantiationException("Cannot create more than " +
                                         m_max + " instances for the pool.");
    }
}
