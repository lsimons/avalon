/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.metainfo;

import org.apache.avalon.util.Version;

/**
 * This is implementation of DependencyDescriptor.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultDependencyDescriptor 
    implements DependencyDescriptor
{
    protected final String              m_role;
    protected final ServiceDescriptor   m_service;

    /**
     * Constructor that has all parts as parameters.
     */
    public DefaultDependencyDescriptor( final String role, final ServiceDescriptor service )
    {
        m_role = role;
        m_service = service;
    }

    /**
     * Return role of dependency.
     *
     * The role is what is used by block implementor to 
     * aquire dependency in ComponentManager.
     *
     * @return the name of the dependency
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Return Service dependency provides.
     *
     * @return the service dependency provides
     */
    public ServiceDescriptor getService()
    {
        return m_service;
    }
}
