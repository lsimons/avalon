/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.container;

import org.apache.excalibur.containerkit.ComponentInfo;

/**
 * Each Component delcared in the application is represented by
 * a ComponentMetaData. Note that this does not necessarily imply
 * that there is only one instance of actual Component. The
 * ComponentMetaData could represent a pool of components, a single
 * component or a component prototype that is reused to create
 * new Components as needed.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/06/04 07:40:21 $
 */
public class ComponentMetaData
{
    /**
     * The name of the Component. This is an abstract name
     * used during assembly.
     */
    private final String m_name;

    /**
     * The resolution of any dependencies required by
     * the component.
     */
    private final DependencyMetaData[] m_dependencies;

    /**
     * The info object for component.
     */
    private final ComponentInfo m_info;

    /**
     * Create a ComponentMetaData.
     *
     * @param name the name of component
     * @param dependencies the meta data for any dependencies
     * @param info the info for component
     */
    public ComponentMetaData( final String name,
                              final DependencyMetaData[] dependencies,
                              final ComponentInfo info )
    {
        m_name = name;
        m_dependencies = dependencies;
        m_info = info;
    }

    /**
     * Return the name of component.
     *
     * @return the name of the component.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Utility method to return the classname of component.
     * This is equivelent to
     * <tt>getComponentInfo().getComponentDescriptor().getClassname()</tt>.
     *
     * @return the classname of the component.
     */
    public String getClassname()
    {
        return getComponentInfo().getComponentDescriptor().getClassname();
    }

    /**
     * Return the info for component.
     *
     * @return the info for component.
     */
    public ComponentInfo getComponentInfo()
    {
        return m_info;
    }

    /**
     * Return the dependency metadata for component.
     *
     * @return the dependency metadata for component.
     */
    public DependencyMetaData[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Return the dependency metadata for component with specified role.
     *
     * @return the dependency metadata for component with specified role.
     */
    public DependencyMetaData getDependency( final String role )
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
