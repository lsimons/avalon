/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.jmx.introspector;

import java.lang.reflect.Method;
import javax.management.MBeanOperationInfo;

class OperationEntry
{
    private MBeanOperationInfo m_info;
    private Method m_method;

    protected OperationEntry( final MBeanOperationInfo info,
                              final Method method )
    {
        m_info = info;
        m_method = method;
    }

    public MBeanOperationInfo getInfo()
    {
        return m_info;
    }

    public Method getMethod()
    {
        return m_method;
    }
}

