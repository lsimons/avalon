/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.catalina;

import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.LifecycleException;

/**
 * Tomcat ClassLoader Hack to make Catalina run over Avalon/Phoenix .
 *
 * @author  Vinay Chandran<vinayc77@yahoo.com>
 * @version 1.0
 */

public class CustomWebappLoader extends WebappLoader
{

    private boolean m_started = false;

    /**
      * Start this component, initializing our associated class loader.
      *
      * @exception org.apache.catalina.LifecycleException if a lifecycle error occurs
      */
    public void start() throws LifecycleException
    {
        //Prevent the LifecycleException by preventing any further calls made 
        // to the base implementation to ' start' again
        if (m_started)
        {
            return;
        }
        m_started = true;
        super.start();

    }

    /**
     *  Constructor 
     * @param classLoader ClassLoader
     */
    public CustomWebappLoader(ClassLoader classLoader)
    {
        super(classLoader);
    }
}
