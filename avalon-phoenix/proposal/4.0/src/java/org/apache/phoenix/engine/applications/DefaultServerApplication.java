/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.applications;

import java.util.Iterator;

import org.apache.framework.context.Context;
import org.apache.framework.configuration.Configuration;

import org.apache.avalon.camelot.Container;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.Entry;

import org.apache.avalon.atlantis.applications.ServerApplication;

import org.apache.log.Logger;

/**
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class DefaultServerApplication implements ServerApplication
    // and thus implements Application, Contextualizable, Configurable,
    // Initializable, Startable, Stoppable, Disposable and Container
{
    public DefaultServerApplication()
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