/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kernel;

import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.excalibur.container.Entry;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.phoenix.components.application.DefaultServerApplication;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.kapi.SarEntry;
import org.apache.avalon.phoenix.components.manager.SystemManager;
import org.apache.avalon.phoenix.metadata.SarMetaData;

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
public class DefaultKernel
    extends AbstractKernel
    implements Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultKernel.class );

    ///SystemManager provided by Embeddor
    private SystemManager            m_systemManager;

    ///Configuration Repository
    private ConfigurationRepository  m_repository;

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_systemManager = (SystemManager)componentManager.lookup( SystemManager.ROLE );
        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
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
        final SarEntry saEntry = (SarEntry)entry;
        final SarMetaData metaData = saEntry.getMetaData();

        setupLogger( application, name );
        try
        {
            if( application instanceof Contextualizable )
            {
                final DefaultContext context = new DefaultContext();
                context.put( "app.name", name );
                context.put( "app.home", metaData.getHomeDirectory() );
                context.makeReadOnly();
                ((Contextualizable)application).contextualize( context );
            }

            if( application instanceof Composable )
            {
                final DefaultComponentManager componentManager = new DefaultComponentManager();
                componentManager.put( SystemManager.ROLE, m_systemManager );
                componentManager.put( ConfigurationRepository.ROLE, m_repository );
                componentManager.makeReadOnly();
                ((Composable)application).compose( componentManager );
            }

            application.setup( metaData, saEntry.getClassLoader() );

            if( application instanceof Configurable )
            {
                ((Configurable)application).configure( saEntry.getConfiguration() );
            }
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "kernel.error.entry.prepare", name );
            throw new ContainerException( message, e );
        }
    }
}
