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
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.DeployerMBean;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.avalon.phoenix.interfaces.EmbeddorMBean;
import org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.KernelMBean;
import org.apache.avalon.phoenix.interfaces.LogManager;

/**
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
final class ManagementRegistration
{
    private static final Map c_map = new HashMap();

    public static final ManagementRegistration KERNEL =
        new ManagementRegistration( Kernel.ROLE,
                                    "Kernel",
                                    new Class[]{KernelMBean.class} );
    public static final ManagementRegistration EXTENSION_MANAGER =
        new ManagementRegistration( PackageRepository.ROLE,
                                    "ExtensionManager",
                                    new Class[]{ExtensionManagerMBean.class} );
    public static final ManagementRegistration EMBEDDOR =
        new ManagementRegistration( Embeddor.ROLE, "Embeddor", new Class[]{EmbeddorMBean.class} );
    public static final ManagementRegistration DEPLOYER =
        new ManagementRegistration( Deployer.ROLE, "Deployer", new Class[]{DeployerMBean.class} );
    public static final ManagementRegistration LOG_MANAGER =
        new ManagementRegistration( LogManager.ROLE, "LogManager", new Class[]{} );
    public static final ManagementRegistration CONFIGURATION_REPOSITORY =
        new ManagementRegistration( ConfigurationRepository.ROLE,
                                    "ConfigurationManager",
                                    new Class[]{} );
    //TODO: Need information for SystemManager?

    private String m_role;
    private String m_name;
    private Class[] m_interfaces;

    private ManagementRegistration( final String role, final String name, final Class[] interfaces )
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

    public static ManagementRegistration getManagementInfoForRole( final String role )
    {
        return (ManagementRegistration)c_map.get( role );
    }
}
