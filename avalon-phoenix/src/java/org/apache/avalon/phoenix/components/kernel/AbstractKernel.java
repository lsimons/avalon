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
import org.apache.avalon.phoenix.components.application.DefaultServerApplication;

/**
 * This is the basic Kernel that supports functionality most kernels need.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractKernel
    extends AbstractContainer
    implements Kernel
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
            final SarEntry entry = (SarEntry)getEntry( names[ i ] );
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
            final SarEntry entry = (SarEntry)getEntry( names[ i ] );
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
            final SarEntry entry = (SarEntry)getEntry( names[ i ] );
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
                final SarEntry entry = (SarEntry)getEntry( names[ i ] );
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
            try { startEntry( name, (SarEntry)entry ); }
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
    private void initializeEntry( final String name, final SarEntry entry )
        throws ContainerException
    {
        Application application = entry.getApplication();

        if( null == application )
        {
            application = new DefaultServerApplication();

            try
            {
                entry.setApplication( application );

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
                entry.setApplication( null );

                final String message = REZ.getString( "kernel.error.entry.initialize", name );
                throw new ContainerException( message, t );
            }
        }
    }

    private void startEntry( final String name, final SarEntry entry )
        throws Exception
    {
        Application application = entry.getApplication();
        if( null == application )
        {
            initializeEntry( name, entry );
            application = entry.getApplication();
        }

        application.start();
    }

    private void stopEntry( final String name, final SarEntry entry )
        throws Exception
    {
        final Application application = entry.getApplication();
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

    private void disposeEntry( final String name, final SarEntry entry )
        throws ContainerException
    {
        final Application application = entry.getApplication();

        if( null != application )
        {
            entry.setApplication( null );
            application.dispose();
        }
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
    protected void prepareApplication( final String name, final SarEntry entry )
        throws ContainerException
    {
    }
}
