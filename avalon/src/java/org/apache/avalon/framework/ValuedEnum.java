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
 * Basic enum class for type-safe enums with values. Should be used as an abstract base.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class ValuedEnum
    extends Enum
{
    protected final int        m_value;

    public ValuedEnum( final String name, final int value, final Map map )
    {
        super( name, map );
        m_value = value;
    }

    public ValuedEnum( final String name, final int value )
    {
        this( name, value, null );
    }

    public final int getValue()
    {
        return m_value;
    }

    public final boolean isEqualTo( final ValuedEnum enum )
    {
        return m_value == enum.m_value;
    }

    public final boolean isGreaterThan( final ValuedEnum enum )
    {
        return m_value > enum.m_value;
    }

    public final boolean isGreaterThanOrEqual( final ValuedEnum enum )
    {
        return m_value >= enum.m_value;
    }

    public final boolean isLessThan( final ValuedEnum enum )
    {
        return m_value < enum.m_value;
    }

    public final boolean isLessThanOrEqual( final ValuedEnum enum )
    {
        return m_value <= enum.m_value;
    }

    public String toString()
    {
        return getClass().getName() + "[" + m_name + "=" + m_value + "]";
    }
}

