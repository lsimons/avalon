/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * A thread safe version of the Cache.
 * Provide synchronized wrapper methods for all them methods
 * defined in the Cache interface.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class SynchronizedCache
    implements ThreadSafe, Cache
{
    private Cache m_cache;

    public SynchronizedCache( final Cache cache )
    {
        m_cache = cache;
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
        synchronized ( m_cache )
        {
            return m_cache.capacity();
        }
    }
   
    public int size()
    {
        synchronized ( m_cache )
        {
            return m_cache.size();
        }
    }

    public Object put( final Object key, final Object value )
    {
        synchronized ( m_cache )
        {
            return m_cache.put( key, value );
        }
    }

    public Object get( final Object key )
    {
        synchronized ( m_cache )
        {
            return m_cache.get( key );
        }
    }

    public Object remove( Object key )
    {
        synchronized ( m_cache )
        {
            return m_cache.remove( key );
        }
    }

    public boolean containsKey( final Object key )
    {
        synchronized ( m_cache )
        {
            return m_cache.containsKey( key );
        }
    }

    public void clear()
    {
        synchronized ( m_cache )
        {
            m_cache.clear();
        }
    }
}
