/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metainfo;

import org.apache.avalon.framework.container.Info;

/**
 * This descrbes information about the block that is used by administration tools and kernel.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockInfo
    implements Info
{
    private final BlockDescriptor          m_descriptor;
    private final ServiceDescriptor[]      m_services;
    private final DependencyDescriptor[]   m_dependencies;

    /**
     * Basic constructor that takes as parameters all parts.
     */
    public BlockInfo( final BlockDescriptor descriptor, 
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
     * @return the BlockDescriptor
     */
    public BlockDescriptor getBlockDescriptor()
    {
        return m_descriptor;
    }
    
    /**
     * This returns a list of Services that this block exports.
     *
     * @return an array of Services (can be null)
     */
    public ServiceDescriptor[] getServices()
    {
        return m_services;
    }

    /**
     * Return an array of Service dependencies that this Block depends upon.
     *
     * @return an array of Service dependencies (may be null) 
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
