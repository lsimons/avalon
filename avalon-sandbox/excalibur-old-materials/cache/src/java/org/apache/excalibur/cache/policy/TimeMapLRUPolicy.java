/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.policy;

import java.util.HashMap;
import java.util.TreeMap;
import org.apache.avalon.excalibur.cache.ReplacementPolicy;

/**
 * TimeMapLRU(Least Recently Used) replacement policy.
 * Use a TreeMap with logical time to perform LRU <code>selectVictim</code> operations.
 * On large cache this implementation should be really faster (since it uses a
 * log(n) treemap plus an hashmap) than the current LRU implem that is working with a List.
 *
 * @author <a href="alag@users.sourceforge.net">Alexis Agahi</a>
 */
public class TimeMapLRUPolicy
    implements ReplacementPolicy
{

    protected TreeMap m_timeToKeyMap;
    protected HashMap m_keyToTimeMap;

    private long m_time;
    private Object m_lock;

    public TimeMapLRUPolicy()
    {
        m_timeToKeyMap = new TreeMap();
        m_keyToTimeMap = new HashMap();
        m_time = 0;
        m_lock = new Object();
    }

    public void hit( final Object key )
    {
        remove( key );
        add( key );
    }

    public void add( final Object key )
    {
        Long time = getTime();
        m_timeToKeyMap.put( time, key );
        m_keyToTimeMap.put( key, time );
    }

    public void remove( final Object key )
    {
        Long time = (Long)m_keyToTimeMap.remove( key );
        if( null != time )
        {
            m_timeToKeyMap.remove( time );
        }
    }

    public Object selectVictim()
    {
        Object time = m_timeToKeyMap.firstKey();
        Object key = m_timeToKeyMap.get( time );
        return key;
    }

    private Long getTime()
    {
        synchronized( m_lock )
        {
            return new Long( m_time++ );
        }
    }
}
