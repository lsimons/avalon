/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import org.apache.avalon.phoenix.engine.blocks.BlockEntry;
import org.apache.avalon.framework.atlantis.AbstractKernel;
import org.apache.avalon.framework.atlantis.SystemManager;
import org.apache.avalon.framework.atlantis.Application;
import org.apache.avalon.framework.camelot.ContainerException;
import org.apache.avalon.framework.camelot.Entry;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.log.Hierarchy;

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

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_systemManager = (SystemManager)componentManager.lookup( SystemManager.ROLE );
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

        setupLogger( application, name );
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
                componentManager.put( SystemManager.ROLE, m_systemManager );
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

    /**
     * Make sure Entry is of correct type.
     *
     * @param name the name of entry
     * @param entry the entry
     * @exception ContainerException to stop removal of entry
     */
    protected final void preAdd( final String name, final Entry entry )
        throws ContainerException
    {
        if( !(entry instanceof ServerApplicationEntry) )
        {
            throw new ContainerException( "Only Entries of type ServerApplicationEntry " +
                                          "may be placed in container." );  
        }
    }

    protected void preStartEntry( final String name, final Entry entry )
        throws ContainerException
    {
        final ServerApplicationEntry saEntry = (ServerApplicationEntry)entry;
        final BlockEntry[] blockEntrys = saEntry.getBlockEntrys();
        final Application application = (Application)saEntry.getInstance();

        for( int i = 0; i < blockEntrys.length; i++ )
        {
            final String blockName = blockEntrys[ i ].getName();
            application.add( blockName, blockEntrys[ i ] );
        }
    }
}
