/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.embeddor;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.extension.PackageRepository;
import org.apache.avalon.phoenix.interfaces.*;

/**
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
final class ManagementInfo
{
    private static final Map c_map = new HashMap();

    public static final ManagementInfo KERNEL =
        new ManagementInfo( Kernel.ROLE, "Kernel", new Class[] { KernelMBean.class } );
    public static final ManagementInfo EXTENSION_MANAGER =
        new ManagementInfo( PackageRepository.ROLE, "ExtensionManager", new Class[] { ExtensionManagerMBean.class } );
    public static final ManagementInfo EMBEDDOR =
        new ManagementInfo( Embeddor.ROLE, "Embeddor", new Class[] { EmbeddorMBean.class } );
    public static final ManagementInfo DEPLOYER =
        new ManagementInfo( Deployer.ROLE, "Deployer", new Class[] { DeployerMBean.class } );
    public static final ManagementInfo LOG_MANAGER =
        new ManagementInfo( LogManager.ROLE, "LogManager", new Class[] {} );
    public static final ManagementInfo CONFIGURATION_REPOSITORY =
        new ManagementInfo( ConfigurationRepository.ROLE, "ConfigurationManager", new Class[] {} );
    //TODO: Need information for SystemManager?
    
    private String m_role;
    private String m_name;
    private Class[] m_interfaces;

    private ManagementInfo( final String role, final String name, final Class[] interfaces )
    {
        m_role = role;
        m_name = name;
        m_interfaces = interfaces;

        c_map.put( m_role, this );
    }

    public String getRole()
    {
        return m_role;
    }

    public String getName()
    {
        return m_name;
    }

    public Class[] getInterfaces()
    {
        return m_interfaces;
    }

    public static ManagementInfo getManagementInfoForRole( final String role )
    {
        return (ManagementInfo)c_map.get( role );
    }
}
