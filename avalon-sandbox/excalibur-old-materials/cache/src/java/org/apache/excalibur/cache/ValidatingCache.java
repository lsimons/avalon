/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache;

/**
 * Validating cache proxy.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @author <a href="mailto:anryoshi@user.sourceforge.net">Antti Koivunen"</a>
 */
public final class ValidatingCache
    implements Cache
{
    private Cache m_cache;
    private CacheValidator m_validator;

    /**
     * Cache with validator.
     *
     * @param cache
     * @param validator object validator
     */
    public ValidatingCache( final Cache cache, final CacheValidator validator )
    {
        m_cache = cache;
        m_validator = validator;
    }

    /**
     * Validate cached item.
     *
     * @param key the key of cached item
     * @param value the value of cached item
     * @return true if cached item is valid otherwise false
     */
    private boolean validate( final Object key, final Object value )
    {
        if( null == m_validator )
        {
            return true;
        }
        else
        {
            return m_validator.validate( key, value );
        }
    }

    public void addListener( final CacheListener listener )
    {
        m_cache.addListener( listener );
    }

    public void removeListener( final CacheListener listener )
    {
        m_cache.removeListener( listener );
    }

    public int capacity()
    {
        return m_cache.capacity();
    }

    public int size()
    {
        return m_cache.size();
    }

    public Object put( final Object key, final Object value )
    {
        return m_cache.put( key, value );
    }

    public Object get( final Object key )
    {
        Object value = m_cache.get( key );

        if( !validate( key, value ) )
        {
            remove( key );
            value = null;
        }

        return value;
    }

    public Object remove( final Object key )
    {
        return m_cache.remove( key );
    }

    public void clear()
    {
        m_cache.clear();
    }
}
