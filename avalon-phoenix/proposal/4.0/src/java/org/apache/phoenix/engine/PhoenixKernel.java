/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import java.util.Iterator;

import javax.management.DynamicMBean;
import javax.management.ObjectName;

import org.apache.framework.context.Context;
import org.apache.framework.context.ContextException;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.lifecycle.Interruptable;

import org.apache.avalon.camelot.Container;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.Entry;

import org.apache.avalon.atlantis.applications.Application;
import org.apache.avalon.atlantis.core.ServerKernel;
import org.apache.avalon.atlantis.facilities.Manager;

import org.apache.log.Logger;

import org.apache.jmx.introspector.DynamicMBeanFactory;

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
    private Logger logger = null;
    private Manager manager = null;

    public PhoenixKernel()
    {
    }
    /////////////////////////
    /// LIFECYCLE METHODS ///
    /////////////////////////
    public void setLogger( Logger logger )
    {
        this.logger = logger;
    }
    /**
     * TODO: this is temporary until facilities are supplied through the container
     * methods.
     *
     * The supplied Context should contain:
     * <ul>
     * <li><b>facilities.manager</b>, a reference to a Manager Facility</li>
     * </ul>
     */
    public void contextualize( Context context ) throws ContextException
    {
        try { this.manager = (Manager)context.get( "facilities.manager" ); }
        catch( Exception e ) { throw new ContextException( "Invalid context - no Manager Facility supplied!" ); }
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

        // JMX REGISTRATION OF APPLICATION
        try
        {
        DynamicMBean mBean = DynamicMBeanFactory.create( entry.getInstance() );
        this.manager.getMBeanServer().registerMBean( mBean, new ObjectName( name ) );
        }
        catch( Exception e )
        {
            this.logger.error( "There was an error adding "+name+" to the JMX Manager", e );
        }

    }
    public void remove( String name ) throws ContainerException
    {
        // JMX DEREGISTRATION OF APPLICATION
        try
        {
        this.manager.getMBeanServer().unregisterMBean( new ObjectName( name ) );
        }
        catch( Exception e )
        {
            this.logger.error( "There was an error removing "+name+" from the JMX Manager", e );
        }
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
