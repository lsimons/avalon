/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.applications;

import org.apache.avalon.atlantis.applications.ServerApplication;
import org.apache.avalon.atlantis.applications.ServerApplicationFactory;

import org.apache.framework.context.Context;
import org.apache.framework.component.ComponentManager;
import org.apache.framework.configuration.Configuration;

/**
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class DefaultServerApplicationFactory implements ServerApplicationFactory
{
    public DefaultServerApplicationFactory()
    {
    }
    public ServerApplication getApplication()
    {
        return null;
    }
    public ServerApplication getApplication( Context context )
    {
        return null;
    }
    public ServerApplication getApplication( Context context,
        ComponentManager componentManager )
    {
        return null;
    }
    public ServerApplication getApplication( Context context,
        ComponentManager componentManager,
        Configuration configuration )
    {
        return null;
    }
}