/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.container.metainfo;

/**
 * A descriptor that describes dependency information for
 * a particular Component. This class contains information
 * about;
 * <ul>
 *   <li>role: the name component uses to look up dependency</li>
 *   <li>service: the class/interface that the dependency must provide</li>
 * </ul>
 *
 * <p>Note that in the future we may also add information relating to
 * constraints on dependency. ie The dependency must be configured in
 * particular fashion or must be able to provide certain facilities etc</p>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/06/04 04:13:52 $
 */
public final class DependencyDescriptor
{
    /**
     * The name the component uses to lookup dependency.
     */
    private final String m_role;

    /**
     * The service class/interface that the dependency must provide.
     */
    private final ServiceDescriptor m_service;

    /**
     * Constructor that has all parts as parameters.
     */
    public DependencyDescriptor( final String role, final ServiceDescriptor service )
    {
        m_role = role;
        m_service = service;
    }

    /**
     * Return the name the component uses to lookup dependency.
     *
     * @return the name the component uses to lookup dependency.
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Return the service class/interface that the dependency must provide.
     *
     * @return the service class/interface that the dependency must provide.
     */
    public ServiceDescriptor getService()
    {
        return m_service;
    }
}
