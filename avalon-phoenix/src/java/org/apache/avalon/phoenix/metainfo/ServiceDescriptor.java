/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metainfo;

import org.apache.avalon.framework.Version;

/**
 * This class describes the meta info of a service offered by a Block.
 * Each service is defined by an interface name and the version of that
 * interface.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class ServiceDescriptor
{
    private final Version m_version;
    private final String m_name;

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
     * Return the version of interface
     *
     * @return the version of interface
     */
    public Version getVersion()
    {
        return m_version;
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
