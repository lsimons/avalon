/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.ArrayList;

/**
 * An abstract superclass for cache implementations.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @author <a href="mailto:anryoshi@users.sf.net">Antti Koivunen</a>
 */
public abstract class AbstractCache
    implements Cache
{
    protected ArrayList m_listeners;

    public AbstractCache()
    {
        m_listeners = new ArrayList();
    }

    public void addListener( final CacheListener listener )
    {
        m_listeners.add( listener );
    }

    public void removeListener( final CacheListener listener )
    {
        m_listeners.remove( listener );
    }

    protected void notifyAdded( final Object key, final Object value )
    {
        CacheEvent event = null;

        final int s = m_listeners.size();
        for( int i = 0; i < s; i++ )
        {
            if( event == null )
            {
                event = new CacheEvent( this, key, value );
            }
            ( (CacheListener)m_listeners.get( i ) ).added( event );
        }
    }

    protected void notifyRemoved( final Object key, final Object value )
    {
        CacheEvent event = null;

        final int s = m_listeners.size();
        for( int i = 0; i < s; i++ )
        {
            if( event == null )
            {
                event = new CacheEvent( this, key, value );
            }
            ( (CacheListener)m_listeners.get( i ) ).removed( event );
        }
    }
}
