/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

import java.util.HashMap;

public class PUnitServiceManager
    implements ServiceManager
{
    private HashMap m_serviceMap = new HashMap();

    void addService( String name, Object object )
    {
        m_serviceMap.put(name, object);
    }

    public Object lookup( String key )
        throws ServiceException
    {
        return m_serviceMap.get(key);
    }

    public boolean hasService( String key )
    {
        return m_serviceMap.containsKey(key);
    }

    public void release( Object service )
    {

    }
}
