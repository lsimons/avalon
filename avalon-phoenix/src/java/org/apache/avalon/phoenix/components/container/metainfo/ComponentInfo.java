/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.container.metainfo;

/**
 * This class contains the meta information about a particular
 * component type. It describes;
 *
 * <ul>
 *   <li>Human presentable meta data such as name, version, description etc
 *   useful when assembling the system.</li>
 *   <li>the services that this component type is capable of providing</li>
 *   <li>the services that this component type requires to operate (and the
 *   names via which services are accessed)</li>
 * </ul>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/06/04 04:13:52 $
 */
public class ComponentInfo
{
    private final ComponentDescriptor m_descriptor;
    private final ServiceDescriptor[] m_services;
    private final DependencyDescriptor[] m_dependencies;

    /**
     * Basic constructor that takes as parameters all parts.
     */
    public ComponentInfo( final ComponentDescriptor descriptor,
                          final ServiceDescriptor[] services,
                          final DependencyDescriptor[] dependencies )
    {
        m_descriptor = descriptor;
        m_services = services;
        m_dependencies = dependencies;
    }

    /**
     * Return meta information that is generallly only required by administration tools.
     *
     * It should be loaded on demand and not always present in memory.
     *
     * @return the ComponentDescriptor
     */
    public ComponentDescriptor getComponentDescriptor()
    {
        return m_descriptor;
    }

    /**
     * Return the set of Services that this Component is capable of providing.
     *
     * @return the set of Services that this Component is capable of providing.
     */
    public ServiceDescriptor[] getServices()
    {
        return m_services;
    }

    /**
     * Return the set of Dependencies that this Component requires to operate.
     *
     * @return the set of Dependencies that this Component requires to operate.
     */
    public DependencyDescriptor[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Retrieve a dependency with a particular role.
     *
     * @param role the role
     * @return the dependency or null if it does not exist
     */
    public DependencyDescriptor getDependency( final String role )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getRole().equals( role ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }
}
