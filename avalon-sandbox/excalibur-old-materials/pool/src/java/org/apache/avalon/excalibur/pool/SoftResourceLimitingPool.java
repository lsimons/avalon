/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.0
 */
public class SoftResourceLimitingPool
    extends DefaultPool
    implements Resizable
{
    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory )
        throws Exception
    {
        this( factory, AbstractPool.DEFAULT_POOL_SIZE / 2 );
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min )
        throws Exception
    {
        this( factory, null, min, min * 2 );
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min,
                                     final int max ) throws Exception
    {
        this( factory, null, min, max );
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final PoolController controller,
                                     final int min,
                                     final int max )
        throws Exception
    {
        super( factory, controller, min, max );
    }

    public SoftResourceLimitingPool( final Class clazz, final int initial, final int maximum )
        throws NoSuchMethodException, Exception
    {
        this( new DefaultObjectFactory( clazz ), initial, maximum );
    }

    public SoftResourceLimitingPool( final Class clazz, final int initial )
        throws NoSuchMethodException, Exception
    {
        this( clazz, initial, initial );
    }

    public void initialize()
        throws Exception
    {
        this.grow( this.m_min );

        this.m_initialized = true;
    }

    public void grow( final int amount )
    {
        try
        {
            m_mutex.acquire();

            this.internalGrow( amount );
        }
        catch( final InterruptedException ie )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Interrupted while waiting on lock", ie );
            }
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not grow the pool properly, an exception was caught", e );
            }
        }
        finally
        {
            m_mutex.release();
        }
    }

    public void shrink( final int amount )
    {
        try
        {
            m_mutex.acquire();

            this.internalShrink( amount );
        }
        catch( final InterruptedException ie )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Interrupted while waiting on lock", ie );
            }
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not shrink the pool properly, an exception was caught", e );
            }
        }
        finally
        {
            m_mutex.release();
        }
    }
}
