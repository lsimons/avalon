/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.Map;
import java.util.HashMap;

/**
 * General timeout cache validator.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class TimeoutValidator
    implements CacheValidator, CacheListener
{
    private long    m_timeout;
    private Map     m_timestamps;

    public TimeoutValidator( final long timeout )
    {
        if ( 0 >= timeout )
        {
            throw new IllegalArgumentException( "Timeout must be greatter than 0" );
        }

        m_timeout = timeout;

        m_timestamps = new HashMap();
    }

    public boolean validate( final Object key, final Object value )
    {
        final long timestamp = ((Long)m_timestamps.get( key )).longValue();
        if ( ( System.currentTimeMillis() - timestamp ) > m_timeout )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void added( final CacheEvent event )
    {
        m_timestamps.put( event.getKey(),
                          new Long( System.currentTimeMillis() ) );
    }

    public void removed( final CacheEvent event )
    {
        m_timestamps.remove( event.getKey() );
    }
}
