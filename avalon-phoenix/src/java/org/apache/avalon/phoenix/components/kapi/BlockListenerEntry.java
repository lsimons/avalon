/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kapi;

/**
 * This is an entry describing a BlockListener.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class BlockListenerEntry
{
    private final String        m_name;
    private final String        m_classname;

    public BlockListenerEntry( final String name, final String classname )
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
