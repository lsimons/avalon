/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This is a implementation of  <code>Pool</code> that is thread safe.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.0
 */
public class HardResourceLimitingPool
    extends SoftResourceLimitingPool
    implements ThreadSafe, Initializable
{
    public HardResourceLimitingPool( final ObjectFactory factory, final PoolController controller )
        throws Exception
    {
        this( factory, controller, DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE );
    }

    public HardResourceLimitingPool( final ObjectFactory factory, final PoolController controller, int max )
        throws Exception
    {
        this( factory, controller, controller.grow(), max );
    }

    public HardResourceLimitingPool( final ObjectFactory factory, final PoolController controller, int initial, int max )
        throws Exception
    {
        super( factory, controller, initial, max );
    }

    public HardResourceLimitingPool( final ObjectFactory factory )
        throws Exception
    {
        this( factory, null );
    }

    public HardResourceLimitingPool( final ObjectFactory factory,
                                     final int initial,
                                     final int maximum )
        throws Exception
    {
        this( factory, null, initial, maximum );
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

    public void initialize()
    {
        try
        {
            super.initialize();
        }
        catch( final Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Caught init exception", e );
            }
        }
    }

    protected Poolable newPoolable() throws Exception
    {
        if( this.size() < m_max )
        {
            return super.newPoolable();
        }

        throw new InstantiationException( "Ran out of resources to instantiate" );
    }

    protected void internalGrow( int amount )
        throws Exception
    {
        super.internalGrow( ( ( this.size() + amount ) < m_max ) ? amount : m_max - this.size() );
    }
}
