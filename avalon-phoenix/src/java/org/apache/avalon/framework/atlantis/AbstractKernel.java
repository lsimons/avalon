/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.atlantis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import org.apache.avalon.framework.camelot.AbstractContainer;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.camelot.ContainerException;
import org.apache.avalon.framework.camelot.Entry;
import org.apache.avalon.framework.camelot.FactoryException;
import org.apache.avalon.framework.camelot.Locator;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;

/**
 * This is the basic Kernel that supports functionality most kernels need.
 * It builds a DAG of blocks, can load/unload/reload blocks, can
 * configure/reconfigure blocks, can start/stop/initialize blocks, provide
 * contexts for blocks etc.
 *
 * When extending this the developer must set the value of m_entryClass and m_applicationClass.
 * ie.
 * m_entryClass = ServerApplicationEntry.class;
 * m_applicationClass = ServerApplication.class;
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractKernel
    extends AbstractContainer
    implements Kernel
{
    private boolean             m_initialised;

    public void initialize()
        throws Exception
    {
        final Iterator names = list();
        while( names.hasNext() )
        {
            final String name = (String)names.next();
            final Entry entry = getEntry( name );
            initializeEntry( name, entry );
        }
    }

    public void start()
        throws Exception
    {
        final Iterator names = list();
        while( names.hasNext() )
        {
            final String name = (String)names.next();
            final Entry entry = getEntry( name );
            startEntry( name, entry );
        }
    }

    public void stop()
        throws Exception
    {
        final Iterator names = list();
        while( names.hasNext() )
        {
            final String name = (String)names.next();
            final Entry entry = getEntry( name );
            stopEntry( name, entry );
        }
    }

    public void dispose()
    {
        m_initialised = false;

        final Iterator names = list();
        while( names.hasNext() )
        {
            final String name = (String)names.next();

            try
            {
                final Entry entry = getEntry( name );
                disposeEntry( name, entry );
            }
            catch( final ContainerException ce )
            {
                getLogger().warn( "Error disposing entry " + name, ce );
            }
        }
    }

    /**
     * Retrieve Application from container.
     * The Application that is returned must be initialized
     * and prepared for manipulation.
     *
     * @param name the name of application
     * @return the application
     * @exception ContainerException if an error occurs
     */
    public Application getApplication( String name )
        throws ContainerException
    {
        final Entry entry = getEntry( name );

        initializeEntry( name, entry );

        return (Application)entry.getInstance();
    }

    /**
     * Create and initialize the application instance if it is not already initialized.
     *
     * @param name the name of application
     * @param entry the entry for application
     * @exception ContainerException if an error occurs
     */
    private void initializeEntry( final String name, final Entry entry )
        throws ContainerException
    {
        Application application = (Application)entry.getInstance();

        if( null == application )
        {
            //Give sub-class chance to do some validation
            //by overiding preInitialize
            preInitializeEntry( name, entry );

            application = createApplicationFor( name, entry );

            try
            {
                entry.setInstance( application );

                //Give sub-class chance to prepare entry
                //This performs process required before the application
                //is ready to be initialized
                prepareApplication( name, entry );

                application.initialize();
            }
            catch( final Throwable t )
            {
                //Initialization failed so clean entry
                //so invalid instance is not used
                entry.setInstance( null );

                throw new ContainerException( "Failed to initialize application", t );
            }

            //Give sub-class chance to do something post
            //initialisation
            postInitializeEntry( name, entry );
        }
    }

    private void startEntry( final String name, final Entry entry )
        throws Exception
    {
        Application application = (Application)entry.getInstance();
        if( null == application )
        {
            initializeEntry( name, entry );
            application = (Application)entry.getInstance();
        }

        preStartEntry( name, entry );
        application.start();
        postStartEntry( name, entry );
    }

    private void stopEntry( final String name, final Entry entry )
        throws Exception
    {
        final Application application = (Application)entry.getInstance();
        if( null != application )
        {
            application.stop();
        }
        else
        {
            getLogger().warn( "Failed to stop application " + name +
                              " as it is not initialized/started" );
        }
    }

    private void disposeEntry( final String name, final Entry entry )
        throws ContainerException
    {
        final Application application = (Application)entry.getInstance();

        if( null != application )
        {
            preDisposeEntry( name, entry );
            entry.setInstance( null );
            application.dispose();
            postDisposeEntry( name, entry );
        }
    }

    /**
     * This method is called before an entry is initialized.
     * Overide to do something.
     *
     * @param name the name of the entry
     * @param entry the entry
     * @exception ContainerException if an error occurs
     */
    protected void preInitializeEntry( final String name, final Entry entry )
        throws ContainerException
    {
    }

    /**
     * This method is called after an entry is initialized.
     * Overide to do something.
     *
     * @param name the name of the entry
     * @param entry the entry
     */
    protected void postInitializeEntry( final String name, final Entry entry )
    {
    }

    /**
     * This method is called before an entry is initialized.
     * Overide to do something.
     *
     * @param name the name of the entry
     * @param entry the entry
     * @exception ContainerException if an error occurs
     */
    protected void preStartEntry( final String name, final Entry entry )
        throws ContainerException
    {
    }

    /**
     * This method is called after an entry is startd.
     * Overide to do something.
     *
     * @param name the name of the entry
     * @param entry the entry
     */
    protected void postStartEntry( final String name, final Entry entry )
    {
    }

    /**
     * This method is called before an entry is disposed.
     * Overide to do something.
     *
     * @param name the name of the entry
     * @param entry the entry
     * @exception ContainerException if an error occurs
     */
    protected void preDisposeEntry( final String name, final Entry entry )
        throws ContainerException
    {
    }

    /**
     * This method is called after an entry is disposed.
     * Overide to do something.
     *
     * @param name the name of the entry
     * @param entry the entry
     */
    protected void postDisposeEntry( final String name, final Entry entry )
    {
    }

    /**
     * Prepare an application before it is initialized.
     * Overide to provide functionality.
     * Usually used to setLogger(), contextualize, compose, configure.
     *
     * @param name the name of application
     * @param entry the application entry
     * @exception ContainerException if an error occurs
     */
    protected void prepareApplication( final String name, final Entry entry )
        throws ContainerException
    {
    }

    /**
     * Create a new application for kernel.
     *
     * @param name the name of application
     * @param entry the entry corresponding to application
     * @return the new Application
     * @exception ContainerException if an error occurs
     */
    protected abstract Application createApplicationFor( String name, Entry entry )
        throws ContainerException;
}
