/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jetty;

import org.mortbay.jetty.servlet.WebApplicationHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.apache.avalon.framework.service.ServiceManager;

/**
 *
 * Override for Jetty's WebApplicationHandler allow custom servlet handling
 *
 *
 * @see <a href="http://jetty.mortbay.com/">Jetty Project Page</a>
 *
 * @author  Paul Hammant
 * @version 1.0
 */
public class SevakWebApplicationHandler extends WebApplicationHandler {

    ServiceManager m_serviceManager;

    public SevakWebApplicationHandler(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }

    public ServletHolder newServletHolder(String name, String servletClass, String forcedPath)
    {
        if(_nameMap.containsKey(name)) {
            throw new IllegalArgumentException("Named servlet already exists: " + name);
        } else {
            ServletHolder holder = new SevakServletHolder(m_serviceManager, this, name, servletClass, forcedPath);
            _nameMap.put(holder.getName(), holder);
            return holder;
        }

    }


}
