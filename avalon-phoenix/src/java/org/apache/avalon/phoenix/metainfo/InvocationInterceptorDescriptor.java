/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metainfo;

/**
 * @todo description
 *
 * @author ifedorenko
 */
public class InvocationInterceptorDescriptor
{
    private final String m_classname;

    public InvocationInterceptorDescriptor( final String classname )
    {
        m_classname = classname;
    }

    public String getClassname()
    {
        return m_classname;
    }
}
