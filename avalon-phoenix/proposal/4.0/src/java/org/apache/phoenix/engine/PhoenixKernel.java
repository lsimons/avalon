/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import java.util.Iterator;

import org.apache.framework.context.Context;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.lifecycle.Interruptable;

import org.apache.avalon.camelot.Container;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.Entry;

import org.apache.avalon.atlantis.applications.Application;
import org.apache.avalon.atlantis.core.ServerKernel;

import org.apache.log.Logger;

/**
 * This is the default Kernel for Phoenix. It uses Camelot for container/
 * deployer stuff.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class PhoenixKernel implements ServerKernel, Interruptable
    // and thus implements Application, Runnable, Initializable, Startable,
    // Stoppable, Disposable, Container, Component, Loggable, Kernel,
    // Contextualizable and ServerApplication
{
    public PhoenixKernel()
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
    /////////////////////////
    /// CONTAINER METHODS ///
    /////////////////////////
    public Application getApplication( String name ) throws ContainerException
    {
        return null;
    }
    public void add( String name, Entry entry ) throws ContainerException
    {
    }
    public void remove( String name ) throws ContainerException
    {
    }
    public Entry getEntry( String name ) throws ContainerException
    {
        return null;
    }
    public Iterator list()
    {
        return null;
    }
}
