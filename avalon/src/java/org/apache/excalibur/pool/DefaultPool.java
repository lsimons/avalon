/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultPool
    extends SingleThreadedPool
{
    public final static int           DEFAULT_POOL_SIZE           = 8;

    public DefaultPool( final ObjectFactory factory,
                        final PoolController controller )
        throws Exception
    {
        super( factory, controller, DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE );
    }

    public DefaultPool( final ObjectFactory factory )
        throws Exception
    {
        this( factory, null );
    }

    public DefaultPool( final Class clazz, final int initial, final int maximum )
        throws NoSuchMethodException, Exception
    {
        super( new DefaultObjectFactory( clazz ), null, initial, maximum );
    }

    public DefaultPool( final Class clazz, final int initial )
        throws NoSuchMethodException, Exception
    {
        this( clazz, initial, initial );
    }
}
