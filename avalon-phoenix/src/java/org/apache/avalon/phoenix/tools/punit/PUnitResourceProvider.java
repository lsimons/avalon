package org.apache.avalon.phoenix.tools.punit;

import org.apache.excalibur.containerkit.lifecycle.ResourceProvider;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;

public class PUnitResourceProvider implements ResourceProvider
{
    public Object createObject(Object o) throws Exception
    {
        return null;
    }

    public Logger createLogger(Object o) throws Exception
    {
        return null;
    }

    public Context createContext(Object o) throws Exception
    {
        return null;
    }

    public ComponentManager createComponentManager(Object o) throws Exception
    {
        return null;
    }

    public ServiceManager createServiceManager(Object o) throws Exception
    {
        return new PUnitServiceManager();
    }

    public Configuration createConfiguration(Object o) throws Exception
    {
        return null;
    }

    public Parameters createParameters(Object o) throws Exception
    {
        return null;
    }
}
