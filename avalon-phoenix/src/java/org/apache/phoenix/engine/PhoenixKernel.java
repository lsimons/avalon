/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.atlantis.AbstractKernel;
import org.apache.avalon.atlantis.SystemManager;
import org.apache.avalon.atlantis.Application;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.component.DefaultComponentManager;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.context.Contextualizable;
import org.apache.avalon.context.DefaultContext;
import org.apache.log.LogKit;

/**
 * The ServerKernel is the core of the Phoenix system.
 * The kernel is responsible for orchestrating low level services
 * such as loading, configuring and destroying blocks. It also
 * gives access to basic facilities such as scheduling sub-systems,
 * protected execution contexts, naming and directory services etc.
 *
 * Note that no facilities are available until after the Kernel has been
 * configured and initialized.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PhoenixKernel
    extends AbstractKernel
    implements Composable
{
    ///SystemManager provided by Embeddor
    private SystemManager          m_systemManager;

    public PhoenixKernel()
    {
        m_entryClass = ServerApplicationEntry.class;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_systemManager = (SystemManager)componentManager.
            lookup( "org.apache.avalon.atlantis.SystemManager" );
    }

    /**
     * Create a new application for kernel.
     *
     * @param name the name of application
     * @param entry the entry corresponding to application
     * @return the new Application
     * @exception ContainerException if an error occurs
     */
    protected Application createApplicationFor( final String name, final Entry entry )
        throws ContainerException
    {
        //It is here where you could return new EASServerApplication()
        //if you wanted to host multiple different types of apps
        return new DefaultServerApplication();
    }

    /**
     * Prepare an application before it is initialized.
     * Overide to provide functionality.
     * Usually used to setLogger(), contextualize, compose, configure.
     *
     * @param name the name of application
     * @param entry the application entry
     * @param application the application instance
     * @exception ContainerException if an error occurs
     */
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
                final DefaultContext context = new DefaultContext();
                context.put( SarContextResources.APP_NAME, name );
                context.put( SarContextResources.APP_HOME_DIR, saEntry.getHomeDirectory() );
                ((Contextualizable)application).contextualize( context );
            }

            if( application instanceof Composable )
            {
                final DefaultComponentManager componentManager = new DefaultComponentManager();
                componentManager.put( "org.apache.avalon.atlantis.SystemManager", m_systemManager );
                ((Composable)application).compose( componentManager );
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
