/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache;

import java.util.EventObject;

/**
 * This is the class used to deliver notifications about <code>Cache</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class CacheEvent
    extends EventObject
{
    private Object m_key;
    private Object m_value;

    /**
     * Construct the <code>CacheEvent</code>.
     *
     * @param cache the source of event
     * @param key the key
     * @param value the value
     */
    public CacheEvent( final Cache cache,
                       final Object key, final Object value )
    {
        super( cache );
        m_key = key;
        m_value = value;
    }

    /**
     * Retrieve the key.
     *
     * @return the key
     */
    public Object getKey()
    {
        return m_key;
    }

    /**
     * Retrieve the value.
     *
     * @return the value
     */
    public Object getValue()
    {
        return m_value;
    }
}
