/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.excalibur.containerkit.lifecycle.ResourceProvider;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;

public class PUnitResourceProvider
    implements ResourceProvider
{

    private PUnitServiceManager m_pUnitServiceManager;
    private PUnitComponentManager m_pUnitComponentManager;
    private Configuration m_configuration;

    public PUnitResourceProvider(PUnitServiceManager pUnitServiceManager, Configuration configuration)
    {
        m_pUnitServiceManager = pUnitServiceManager;
        m_pUnitComponentManager = new PUnitComponentManager(pUnitServiceManager);
        m_configuration = configuration;
    }

    public Object createObject(Object o) throws Exception
    {
        return o;
    }

    public Logger createLogger(Object o) throws Exception
    {
        // should be queryable mock logger ?
        return new ConsoleLogger();
    }

    public Context createContext(Object o) throws Exception
    {
        return new DefaultContext();
    }

    public ComponentManager createComponentManager(Object o) throws Exception
    {
        return m_pUnitComponentManager;
    }

    public ServiceManager createServiceManager(Object o) throws Exception
    {
        return m_pUnitServiceManager;
    }

    public Configuration createConfiguration(Object o) throws Exception
    {
        return m_configuration;
    }

    public Parameters createParameters(Object o) throws Exception
    {
        return null;
    }
}
