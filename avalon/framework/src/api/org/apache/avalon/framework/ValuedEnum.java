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
 * Basic enum class for type-safe enums with values. 
 * Should be used as an abstract base.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class ValuedEnum
    extends Enum
{
    private final int        m_value;

    /**
     * Constructor for enum so that it gets added to map at creation.
     * Adding to a map is useful for implementing find...() style methods.
     *
     * @param name the name of enum
     * @param value the value of enum
     * @param map the map to add enum to 
     */
    public ValuedEnum( final String name, final int value, final Map map )
    {
        super( name, map );
        m_value = value;
    }

    /**
     * Constructor for enum.
     *
     * @param name the name of enum
     * @param value the value of enum
     */
    public ValuedEnum( final String name, final int value )
    {
        this( name, value, null );
    }

    /**
     * Get value of enum.
     *
     * @return the enums value
     */
    public final int getValue()
    {
        return m_value;
    }

    /**
     * Test if enum is equal in value to other enum.
     *
     * @param other the other enum
     * @return true if equal
     */
    public final boolean isEqualTo( final ValuedEnum other )
    {
        return m_value == other.m_value;
    }

    /**
     * Test if enum is greater than in value to other enum.
     *
     * @param other the other enum
     * @return true if greater than
     */
    public final boolean isGreaterThan( final ValuedEnum other )
    {
        return m_value > other.m_value;
    }

    /**
     * Test if enum is greater than or equal in value to other enum.
     *
     * @param other the other enum
     * @return true if greater than or equal
     */
    public final boolean isGreaterThanOrEqual( final ValuedEnum other )
    {
        return m_value >= other.m_value;
    }

    /**
     * Test if enum is less than in value to other enum.
     *
     * @param other the other enum
     * @return true if less than
     */
    public final boolean isLessThan( final ValuedEnum other )
    {
        return m_value < other.m_value;
    }

    /**
     * Test if enum is less than or equal in value to other enum.
     *
     * @param other the other enum
     * @return true if less than or equal
     */
    public final boolean isLessThanOrEqual( final ValuedEnum other )
    {
        return m_value <= other.m_value;
    }

    /**
     * Overide toString method to produce human readable description.
     *
     * @return human readable description of enum
     */
    public String toString()
    {
        return getClass().getName() + "[" + getName() + "=" + m_value + "]";
    }
}

