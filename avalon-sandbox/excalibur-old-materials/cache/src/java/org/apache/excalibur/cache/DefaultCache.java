/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

/**
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class DefaultCache
    extends AbstractCache
{
    private ReplacementPolicy m_policy;
    private CacheStore m_store;

    public DefaultCache( final ReplacementPolicy policy,
                         final CacheStore store )
    {
        m_policy = policy;
        m_store = store;
    }

    public int capacity()
    {
        return m_store.capacity();
    }

    public int size()
    {
        return m_store.size();
    }

    public Object put( final Object key, final Object value )
    {
        Object oldValue;

        synchronized ( m_store )
        {
            if ( containsKey( key ) )
            {
                oldValue = remove( key );
            }
            else
            {
                oldValue = null;
            }

            if ( m_store.isFull() )
            {
                remove( m_policy.selectVictim() );
            }

            m_store.put( key, value );
            m_policy.add( key );
            notifyAdded( key, value );
        }

        return oldValue;
    }

    public Object get( final Object key )
    {
        Object value;
        
        synchronized ( m_store )
        {
            value = m_store.get( key );
            if ( validate( key, value ) )
            {
                m_policy.hit( key );
            }
            else
            {
                remove( key );
                value = null;
            }
        }

        return value;
    }

    public Object remove( final Object key )
    {
        Object value;
        
        synchronized ( m_store )
        {
            value = m_store.remove( key );
            m_policy.remove( key );
            notifyRemoved( key, value );
        }

        return value;
    }

    public boolean containsKey( final Object key )
    {
        boolean contains;

        synchronized ( m_store )
        {
            contains = m_store.containsKey( key );
        }

        return contains;
    }

    public void clear()
    {
        synchronized ( m_store )
        {
            final Object[] keys = m_store.keys();
            for ( int i = 0; i < keys.length; i++ )
            {
                remove( keys[ i ] );
            }
        }
    }
}
