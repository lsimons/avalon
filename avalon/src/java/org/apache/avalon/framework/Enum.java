/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework;

import java.util.Map;

/**
 * Basic enum class for type-safe enums. Should be used as an abstract base.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class Enum
{
    private final String        m_name;

    public Enum( final String name )
    {
        this( name, null );
    }

    public Enum( final String name, final Map map )
    {
        m_name = name;
        if( null != map )
        {
            map.put( name, this );
        }
    }

    public final String getName()
    {
        return m_name;
    }

    public String toString()
    {
        return getClass().getName() + "[" + m_name + "]";
    }
}
