/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metadata;

/**
 * This is the structure describing the instances of roles provided to each block.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class RoleMetaData
{
    private final String        m_name;
    private final String        m_interface;

    public RoleMetaData( final String name, final String interfaceName )
    {
        m_name = name;
        m_interface = interfaceName;
    }

    public String getInterface()
    {
        return m_interface;
    }

    public String getName()
    {
        return m_name;
    }
}
