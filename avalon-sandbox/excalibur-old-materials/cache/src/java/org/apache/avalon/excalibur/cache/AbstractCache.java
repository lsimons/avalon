/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public abstract class AbstractCache
    implements Cache
{
    protected CacheValidator m_validator;
    protected ArrayList m_listeners;

    public AbstractCache()
    {
        this( null );
    }

    public AbstractCache( final CacheValidator validator )
    {
        setValidator( validator );
        m_listeners = new ArrayList();
    }

    public void setValidator( final CacheValidator validator )
    {
        m_validator = validator;
    }

    protected boolean validate( final Object key, final Object value )
    {
        if ( null == m_validator )
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
        synchronized ( m_listeners )
        {
            m_listeners.add( listener );
        }
    }

    public void removeListener( final CacheListener listener )
    {
        synchronized ( m_listeners )
        {
            m_listeners.remove( listener );
        }
    }

    protected void notifyAdded( final Object key, final Object value )
    {
        final CacheEvent event = new CacheEvent( this, key, value );

        ArrayList listeners;
        synchronized ( m_listeners )
        {
            listeners = (ArrayList)m_listeners.clone();
        }

        final int s = listeners.size();
        for ( int i = 0; i < s; i++ )
        {
            ((CacheListener)listeners.get( i )).added( event );
        }
    }

    protected void notifyRemoved( final Object key, final Object value )
    {
        final CacheEvent event = new CacheEvent( this, key, value );

        ArrayList listeners;
        synchronized ( m_listeners )
        {
            listeners = (ArrayList)m_listeners.clone();
        }

        final int s = listeners.size();
        for ( int i = 0; i < s; i++ )
        {
            ((CacheListener)listeners.get( i )).removed( event );
        }
    }
}
