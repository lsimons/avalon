/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class MemoryCacheStore
    extends AbstractCacheStore
{
    private HashMap m_entries;
    private int m_capacity;

    public MemoryCacheStore( final int capacity )
    {
        if ( capacity < 1 ) throw new IllegalArgumentException( "Specified capacity must be at least 1" );

        m_capacity = capacity;
        m_entries = new HashMap( m_capacity );
    }

    public int capacity()
    {
        return m_capacity;
    }

    public int size()
    {
        return m_entries.size();
    }

    public Object put( final Object key, final Object value )
    {
        return m_entries.put( key, value );
    }

    public Object get( final Object key )
    {
        return m_entries.get( key );
    }

    public Object remove( final Object key )
    {
        return m_entries.remove( key );
    }

    public boolean containsKey( final Object key )
    {
        return m_entries.containsKey( key );
    }

    public Object[] keys()
    {
        return m_entries.keySet().toArray();
    }
}
