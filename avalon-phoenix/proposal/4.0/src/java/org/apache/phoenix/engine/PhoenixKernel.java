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
import org.apache.framework.context.Contextualizable;
import org.apache.framework.context.ContextException;
import org.apache.framework.component.Composer;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.Configurable;
import org.apache.framework.configuration.ConfigurationException;
import org.apache.framework.lifecycle.InitializationException;
import org.apache.framework.lifecycle.StartException;
import org.apache.framework.lifecycle.StopException;

import org.apache.avalon.camelot.Container;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.Entry;

import org.apache.avalon.atlantis.applications.Application;
import org.apache.avalon.atlantis.core.ServerKernel;
import org.apache.avalon.atlantis.core.AbstractKernel;
import org.apache.avalon.atlantis.facilities.Manager;

import org.apache.phoenix.engine.applications.DefaultServerApplication;
import org.apache.phoenix.engine.applications.ServerApplicationEntry;

import org.apache.log.Logger;
import org.apache.log.LogKit;

import org.apache.jmx.introspector.DynamicMBeanFactory;

/**
 * This is the default ServerKernel for Phoenix. It uses Camelot for
 * container/deployer stuff.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class PhoenixKernel extends AbstractKernel implements ServerKernel
    // and thus implements Application, Runnable, Initializable, Startable,
    // Stoppable, Disposable, Container, Component, Loggable, Kernel,
    // Contextualizable and ServerApplication
    // and thus extends AbstractContainer and AbstractLoggable
{
    private Logger logger = null;
    private Manager manager = null;

    public PhoenixKernel()
    {
        m_entryClass = ServerApplicationEntry.class;
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
    public void configure( Configuration configuration ) throws ConfigurationException
    {
    }
    public void init() throws InitializationException
    {
        super.init();
    }
    public void start() throws StartException
    {
        super.start();
    }
    public void stop() throws StopException
    {
        super.stop();
    }
    public void dispose()
    {
        super.dispose();
    }
    /////////////////////////
    /// CONTAINER METHODS ///
    /////////////////////////
    public Application getApplication( String name ) throws ContainerException
    {
        return super.getApplication( name );
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
            if( this.logger != null )
                this.logger.error( "There was an error adding "+name+" to the JMX Manager", e );
        }

        super.add( name, entry );

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

        super.remove( name );
    }
    public Entry getEntry( String name ) throws ContainerException
    {
        return super.getEntry( name );
    }
    public Iterator list()
    {
        return super.list();
    }

    //////////////////////
    /// KERNEL METHODS ///
    //////////////////////
    protected Application createApplicationFor( String name, Entry entry )
        throws ContainerException
    {
        //It is here where you could return new EASServerApplication()
        //if you wanted to host multiple different types of apps
        return new DefaultServerApplication();
    }
    protected void prepareApplication( final String name, final Entry entry )
        throws ContainerException
    {
        final Application application = (Application)entry.getInstance();
        final ServerApplicationEntry saEntry = (ServerApplicationEntry)entry;

        setupLogger( application, LogKit.getLoggerFor( name ) );

        try
        {
            if( application instanceof Contextualizable )
            {
                ((Contextualizable)application).contextualize( saEntry.getContext() );
            }
            if( application instanceof Composer )
            {
                ((Composer)application).compose( saEntry.getComponentManager() );
            }
            if( application instanceof Configurable )
            {
                ((Configurable)application).configure( saEntry.getConfiguration() );
            }
        }
        catch( final Exception e )
        {
            throw new ContainerException( "Error preparing Application", e );
        }
    }
}
