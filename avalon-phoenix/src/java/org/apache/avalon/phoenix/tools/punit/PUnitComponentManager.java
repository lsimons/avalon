/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

/**
 * PUnitComponentManager
 * @author Paul Hammant
 */
public class PUnitComponentManager
    implements ComponentManager
{
    private ServiceManager m_serviceManager;

    /**
     * PUnitComponentManager
     * @param serviceManager The service manager to delegate to
     */
    public PUnitComponentManager(ServiceManager serviceManager)
    {
        this.m_serviceManager = serviceManager;
    }

    /**
     * Lookup a comp
     * @param key The key
     * @return The comp
     * @throws ComponentException If a problem
     */
    public Component lookup( String key )
        throws ComponentException
    {
        try
        {
            return (Component) m_serviceManager.lookup(key);
        }
        catch (ServiceException e)
        {
            throw new ComponentException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Has Component
     * @param key The key
     * @return true/false
     */
    public boolean hasComponent( String key )
    {
        return m_serviceManager.hasService(key);
    }

    /**
     * Release the component
     * @param component
     */
    public void release( Component component )
    {
       m_serviceManager.release(component);
    }
}
