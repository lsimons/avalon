/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jetty;

import org.mortbay.jetty.servlet.ServletHolder;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

/**
 *
 * Override for Jetty's ServletHolder allow custom servlet handling
 *
 *
 * @see <a href="http://jetty.mortbay.com/">Jetty Project Page</a>
 *
 * @author  Paul Hammant
 * @version 1.0
 */
public class SevakServletHolder extends ServletHolder
{
    private ServiceManager m_serviceManager;

    public SevakServletHolder(ServiceManager serviceManager, SevakWebApplicationHandler handler, String name, String className, String forcedPath)
    {
        // this constructor public or protected...
        super(handler, name, className, forcedPath);
        m_serviceManager = serviceManager;
    }

    public synchronized Object newInstance() throws InstantiationException, IllegalAccessException {
        if(_class == null)
            throw new InstantiationException("No class for " + this);
        else {
            Object instance = _class.newInstance();
            if (instance instanceof Serviceable) {
                try
                {
                    ((Serviceable) instance).service(m_serviceManager);
                }
                catch (ServiceException e)
                {
                    throw new InstantiationException("Service Exception for servlet "
                            + _class.getName() + ":" + e.getMessage());
                }
            }
            return instance;
        }
    }


}
