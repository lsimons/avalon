/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.container.metainfo;

import org.apache.avalon.framework.Version;

/**
 * This descriptor defines the type of service offerend or required
 * by a component. The type corresponds to the class name of the
 * class/interface implemented by component. Associated with each
 * classname is a version object so that different versions of same
 * interface can be represented.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/06/04 04:13:52 $
 */
public final class ServiceDescriptor
{
    /**
     * The name of service class.
     */
    private final String m_name;

    /**
     * The version of service class.
     */
    private final Version m_version;

    /**
     * Construct a service with specified name and version.
     *
     * @param name the name of the service
     * @param version the version of service
     */
    public ServiceDescriptor( final String name, final Version version )
    {
        m_name = name;
        m_version = version;
    }

    /**
     * Return name of Service (which coresponds to the interface
     * name eg org.apache.block.WebServer)
     *
     * @return the name of the Service
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the version of interface
     *
     * @return the version of interface
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Determine if specified service will match this service.
     * To match a service has to have same name and must comply with version.
     *
     * @param other the other ServiceInfo
     * @return true if matches, false otherwise
     */
    public boolean matches( final ServiceDescriptor other )
    {
        return
            other.getName().equals( m_name ) &&
            other.getVersion().complies( m_version );
    }

    /**
     * Convert to a string of format name/version
     *
     * @return string describing service
     */
    public String toString()
    {
        return m_name + "/" + m_version;
    }
}
