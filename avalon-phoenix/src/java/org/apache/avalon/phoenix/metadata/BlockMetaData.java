/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metadata;

/**
 * This is the structure describing each block.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockMetaData
{
    private final String           m_name;
    private final String           m_classname;
    private final RoleMetaData[]   m_roles;

    public BlockMetaData( final String name,
                          final String classname,
                          final RoleMetaData[] roles )
    {
        m_name = name;
        m_classname = classname;
        m_roles = roles;
    }

    public String getName()
    {
        return m_name;
    }

    public String getClassname()
    {
        return m_classname;
    }

    public RoleMetaData getRole( final String role )
    {
        for( int i = 0; i < m_roles.length; i++ )
        {
            if( m_roles[ i ].getRole().equals( role ) )
            {
                return m_roles[ i ];
            }
        }

        return null;
    }

    public RoleMetaData[] getRoles()
    {
        return m_roles;
    }
}
