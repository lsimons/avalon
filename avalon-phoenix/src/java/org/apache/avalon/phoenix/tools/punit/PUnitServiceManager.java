package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

public class PUnitServiceManager implements ServiceManager
{
    public Object lookup(String s) throws ServiceException
    {
        return null;
    }

    public boolean hasService(String s)
    {
        return false;
    }

    public void release(Object o)
    {
    }
}
