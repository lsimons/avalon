/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.container.AbstractContainer;
import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.excalibur.container.Entry;
import org.apache.avalon.excalibur.container.Locator;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.components.application.Application;

/**
 * This is the basic Kernel that supports functionality most kernels need.
 * It builds a DAG of blocks, can load/unload/reload blocks, can
 * configure/reconfigure blocks, can start/stop/initialize blocks, provide
 * contexts for blocks etc.
 *
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractKernel
    extends AbstractContainer
    implements Application
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractKernel.class );

    private boolean             m_autoStart;

    public void initialize()
        throws Exception
    {
        final String[] names = list();
        for( int i = 0; i < names.length; i++ )
        {
            final Entry entry = getEntry( names[ i ] );
            initializeEntry( names[ i ], entry );
        }
    }

    public void start()
        throws Exception
    {
        m_autoStart = true;
        final String[] names = list();
        for( int i = 0; i < names.length; i++ )
        {
            final Entry entry = getEntry( names[ i ] );
            startEntry( names[ i ], entry );
        }
    }

    public void stop()
        throws Exception
    {
        m_autoStart = false;
        final String[] names = list();
        for( int i = 0; i < names.length; i++ )
        {
            final Entry entry = getEntry( names[ i ] );
            stopEntry( names[ i ], entry );
        }
    }

    public void dispose()
    {
        final String[] names = list();
        for( int i = 0; i < names.length; i++ )
        {
            try
            {
                final Entry entry = getEntry( names[ i ] );
                disposeEntry( names[ i ], entry );
            }
            catch( final ContainerException ce )
            {
                final String message = REZ.getString( "kernel.error.entry.dispose", names[ i ] );
                getLogger().warn( message, ce );
            }
        }
    }


    /**
     * After being added to container, start the entry if kernel is started.
     *
     * @param name the name of entry
     * @param entry the entry
     */
    protected void postAdd( final String name, final Entry entry )
    {
        if( m_autoStart )
        {
            try { startEntry( name, entry ); }
            catch( final Exception e )
            {
                final String message = REZ.getString( "kernel.error.entry.start", name );
                getLogger().warn( message, e );
            }
        }
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

                final String message = REZ.getString( "kernel.error.entry.initialize", name );
                throw new ContainerException( message, t );
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
            final String message = REZ.getString( "kernel.error.entry.nostop", name );
            getLogger().warn( message );
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
