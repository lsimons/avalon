/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.EventObject;

/**
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class CacheEvent
    extends EventObject
{
    public static final int ADDED = 1;
    public static final int REMOVED = 2;

    private Object m_key;
    private Object m_value;
    private int m_type;

    public CacheEvent( final Cache cache,
                       final Object key, final Object value, final int type )
    {
        super( cache );
        m_key = key;
        m_value = value;
        m_type = type;
    }

    public Object getKey()
    {
        return m_key;
    }

    public Object getValue()
    {
        return m_value;
    }

    public int getType()
    {
        return m_type;
    }
}
