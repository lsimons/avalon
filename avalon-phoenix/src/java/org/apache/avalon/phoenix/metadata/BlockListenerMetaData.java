/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metadata;

/**
 * This describs a BlockListener.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class BlockListenerMetaData
{
    private final String m_name;
    private final String m_classname;

    public BlockListenerMetaData( final String name, final String classname )
    {
        m_name = name;
        m_classname = classname;
    }

    public String getClassname()
    {
        return m_classname;
    }

    public String getName()
    {
        return m_name;
    }
}
