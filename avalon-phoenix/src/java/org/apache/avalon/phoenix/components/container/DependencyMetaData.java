/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.container;

/**
 * The DependencyMetaData is the mapping of a component as a dependency
 * of another component. Each component declares dependencies (via ComponentInfo)
 * and for each dependency there must be a coressponding DependencyMetaData which
 * has a matching role. The name value in DependencyMetaData object must refer
 * to another Component that implements Service as specified in DependencyInfo.
 *
 * <p>Note that it is invalid to have circular dependencies.</p>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/06/04 07:40:21 $
 */
public final class DependencyMetaData
{
    /**
     * The name that the client component will use to access dependency.
     */
    private final String m_role;

    /**
     * the name of the Component that will provide the dependency.
     */
    private final String m_name;

    /**
     * Create MetaData with specified name and role.
     *
     * @param role the name client uses to access component
     * @param name the name of provider
     */
    public DependencyMetaData( final String role, final String name )
    {
        m_role = role;
        m_name = name;
    }

    /**
     * Return the name that the client component will use to access dependency.
     *
     * @return the name that the client component will use to access dependency.
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Return the name of the Component that will provide the dependency.
     *
     * @return the name of the Component that will provide the dependency.
     */
    public String getName()
    {
        return m_name;
    }
}
