/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import java.lang.reflect.Method;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;

class AttributeEntry
{
    private MBeanAttributeInfo m_info;
    private Method m_readMethod;
    private Method m_writeMethod;

    protected AttributeEntry( final String name,
                              final String description,
                              final Method readMethod,
                              final Method writeMethod )
        throws IntrospectionException
    {
        m_info = new MBeanAttributeInfo( name, description, readMethod, writeMethod );
        m_readMethod = readMethod;
        m_writeMethod = writeMethod;
    }

    public MBeanAttributeInfo getInfo()
    {
        return m_info;
    }

    public Method getReadMethod()
    {
        return m_readMethod;
    }

    public Method getWriteMethod()
    {
        return m_writeMethod;
    }
}

