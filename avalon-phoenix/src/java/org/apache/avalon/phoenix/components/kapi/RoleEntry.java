/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kapi;

/**
 * This is the structure describing the instances of roles provided to each block.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class RoleEntry
{
    private final String        m_name;
    private final String        m_role;

    public RoleEntry( final String name, final String role )
    {
        m_name = name;
        m_role = role;
    }

    public String getRole()
    {
        return m_role;
    }

    public String getName()
    {
        return m_name;
    }
}
