/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

import java.util.Stack;
import java.util.Vector;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.thread.ThreadSafe;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class AbstractPool
    extends AbstractLoggable
    implements Pool, ThreadSafe
{
    protected final ObjectFactory  m_factory;
    protected final int            m_min;
    protected int                  m_max;
    protected int                  m_currentCount  = 0;
    protected Vector               m_active        = new Vector();
    protected Stack                m_ready         = new Stack();

    /**
     * Create an AbstractPool.  The pool requires a factory, and can
     * optionally have a controller.
     */
    public AbstractPool( final ObjectFactory factory,
                         final int min,
                         final int max ) throws Exception
    {
        m_factory = factory;
        int t_max = max;
        int t_min = min;

        if( min < 0 )
        {
            if( null != getLogger() )
            {
                getLogger().warn( "Minumum number of poolables specified is " +
                                  "less than 0, using 0" );
            }

            t_min = 0;
        }
        else
        {
            t_min = min;
        }

        if( ( max < min ) || ( max < 1 ) )
        {
            if( null != getLogger() )
            {
                getLogger().warn( "Maximum number of poolables specified must be at " +
                                  "least 1 and must be greater than the minumum number " +
                                  "of connections" );
            }
            t_max = ( min > 1 ) ? min : 1;
        }
        else
        {
            t_max = max;
        }

        m_max = t_max;
        m_min = t_min;

        if( !(this instanceof Initializable) )
        {
            initialize();
        }
    }

    protected void initialize()
        throws Exception
    {
        for( int i = 0; i < m_min; i++ )
        {
            m_ready.push( m_factory.newInstance() );
            m_currentCount++;
        }
    }

    public int size() {
        int count = this.m_currentCount;
        return count;
    }

    public synchronized Poolable get()
        throws Exception
    {
        Poolable obj = null;

        if( 0 == m_ready.size() )
        {
            obj = (Poolable)m_factory.newInstance();
            m_currentCount++;
        }
        else
        {
            obj = (Poolable)m_ready.pop();
        }

        m_active.addElement( obj );

        if( null != getLogger() )
        {
            getLogger().debug( m_factory.getCreatedClass().getName() + ": requested from the pool." );
        }

        return obj;
    }

    public synchronized void put( final Poolable obj )
    {
        m_active.removeElement( obj );
        m_ready.push( obj );

        if( null != getLogger() )
        {
            getLogger().debug( m_factory.getCreatedClass().getName() + ": returned to the pool." );
        }
    }
}
