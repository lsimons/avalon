/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities;

import javax.management.MBeanServer;

import org.apache.framework.context.Context;
import org.apache.framework.configuration.Configuration;

import org.apache.phoenix.facilities.Manager;

import org.apache.log.Logger;

/**
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class ManagerImpl implements Manager
{
    public ManagerImpl()
    {
    }
    public ManagerImpl( MBeanServer mBeanServer )
    {
    }

    /////////////////////////
    /// LIFECYCLE METHODS ///
    /////////////////////////
    public void setLogger( Logger logger )
    {
    }
    public void contextualize( Context context )
    {
    }
    public void configure( Configuration configuration )
    {
    }
    public void init()
    {
    }
    public void start()
    {
    }
    public void run()
    {
    }
    public void suspend()
    {
    }
    public void resume()
    {
    }
    public void stop()
    {
    }
    public void dispose()
    {
    }
}