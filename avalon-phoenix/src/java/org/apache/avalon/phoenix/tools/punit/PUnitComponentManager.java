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

public class PUnitComponentManager
    implements ComponentManager
{
    private ServiceManager m_serviceManager;

    public PUnitComponentManager(ServiceManager serviceManager)
    {
        this.m_serviceManager = serviceManager;
    }

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

    public boolean hasComponent( String key )
    {
        return m_serviceManager.hasService(key);
    }

    public void release( Component component )
    {

    }
}
