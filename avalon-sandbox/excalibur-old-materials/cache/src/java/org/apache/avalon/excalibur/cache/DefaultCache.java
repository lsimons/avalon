/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

/**
 * Default <code>Cache</code> implementation.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
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
        if ( null == key || null == value )
        {
            return null;
        }

        final Object oldValue = remove( key );

        if( m_store.isFull() )
        {
            remove( m_policy.selectVictim() );
        }

        m_store.put( key, value );
        m_policy.add( key );
        notifyAdded( key, value );

        return oldValue;
    }

    public Object get( final Object key )
    {
        if ( null == key )
        {
            return null;
        }

        final Object value = m_store.get( key );
        m_policy.hit( key );

        return value;
    }

    public Object remove( final Object key )
    {
        if ( null == key )
        {
            return null;
        }

        Object value = null;
        if( m_store.containsKey( key ) )
        {
            value = m_store.remove( key );
            m_policy.remove( key );
            notifyRemoved( key, value );
        }

        return value;
    }

    public void clear()
    {
        final Object[] keys = m_store.keys();
        for( int i = 0; i < keys.length; i++ )
        {
            remove( keys[ i ] );
        }
    }
}
