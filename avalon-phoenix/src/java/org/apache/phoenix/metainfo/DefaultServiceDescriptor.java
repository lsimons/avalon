/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.metainfo;

import org.apache.avalon.Version;

/**
 * This is default implementaion of ServiceInfo interface.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class DefaultServiceDescriptor 
    implements ServiceDescriptor 
{
    protected final Version                    m_version;
    protected final String                     m_name;

    public DefaultServiceDescriptor( final String name, final Version version )
    {
        m_name = name;
        m_version = version;
    }

    /**
     * Return version of interface
     *
     * @return the version of interface
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Return name of Service (which coresponds to the interface 
     * name eg org.apache.block.Logger)
     *
     * @return the name of the Service
     */
    public String getName()
    {
        return m_name;
    }

    
    /**
     * Determine if other service will match this service.
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
