/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.phoenix.components.application.DefaultServerApplication;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.frame.DefaultApplicationFrame;
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
    extends AbstractLoggable
    implements Kernel, Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultKernel.class );

    ///SystemManager provided by Embeddor
    private SystemManager            m_systemManager;

    ///Configuration Repository
    private ConfigurationRepository  m_repository;

    private HashMap                  m_entrys = new HashMap();

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_systemManager = (SystemManager)componentManager.lookup( SystemManager.ROLE );
        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
    }

    public void initialize()
        throws Exception
    {
    }

    public void dispose()
    {
        final String[] names = getApplicationNames();
        for( int i = 0; i < names.length; i++ )
        {
            try
            {
                final SarEntry entry = (SarEntry)m_entrys.get( names[ i ] );
                shutdown( entry );
            }
            catch( final Exception e )
            {
                final String message = REZ.getString( "kernel.error.entry.dispose", names[ i ] );
                getLogger().warn( message, e );
            }
        }
    }

    public String[] getApplicationNames()
    {
        return (String[])m_entrys.keySet().toArray( new String[ 0 ] );
    }

    public Application getApplication( final String name )
    {
        final SarEntry entry = (SarEntry)m_entrys.get( name );
        if( null == entry ) return null;
        else return entry.getApplication();
    }

    /**
     * Create and initialize the application instance if it is not already initialized.
     *
     * @param name the name of application
     * @param entry the entry for application
     * @exception ContainerException if an error occurs
     */
    private void startup( final SarEntry entry )
        throws Exception
    {
        final String name = entry.getMetaData().getName();

        Application application = entry.getApplication();
        if( null == application )
        {
            try
            {
                application = new DefaultServerApplication();

                setupLogger( application, name );

                if( application instanceof Composable )
                {
                    final ComponentManager componentManager = createComponentManager();
                    ((Composable)application).compose( componentManager );
                }

                final ApplicationFrame frame = createApplicationFrame( entry );
                application.setup( frame );

                application.initialize();
                application.start();

                entry.setApplication( application );
            }
            catch( final Throwable t )
            {
                //Initialization failed so clean entry
                //so invalid instance is not used
                entry.setApplication( null );

                final String message =
                    REZ.getString( "kernel.error.entry.initialize", entry.getMetaData().getName() );
                throw new Exception( message/*, t*/ );
            }
        }
    }

    private void shutdown( final SarEntry entry )
        throws Exception
    {
        final Application application = entry.getApplication();
        if( null != application )
        {
            entry.setApplication( null );
            application.stop();
            application.dispose();
        }
        else
        {
            final String message =
                REZ.getString( "kernel.error.entry.nostop", entry.getMetaData().getName() );
            getLogger().warn( message );
        }
    }

    public void addApplication( final SarMetaData metaData,
                                final ClassLoader classLoader,
                                final Configuration server )
        throws Exception
    {
        final String name = metaData.getName();
        final SarEntry entry = new SarEntry( metaData, classLoader, server );
        m_entrys.put( name, entry );

        try { startup( (SarEntry)entry ); }
        catch( final Exception e )
        {
            final String message = REZ.getString( "kernel.error.entry.start", name );
            getLogger().warn( message, e );
        }
    }

    private ApplicationFrame createApplicationFrame( final SarEntry entry )
        throws Exception
    {
        final DefaultApplicationFrame frame =
            new DefaultApplicationFrame( entry.getClassLoader(), entry.getMetaData() );

        setupLogger( entry.getApplication(), entry.getMetaData().getName() + ".frame" );

        if( frame instanceof Composable )
        {
            final ComponentManager componentManager = createComponentManager();
            ((Composable)frame).compose( componentManager );
        }

        frame.configure( entry.getConfiguration() );
        frame.initialize();
        return frame;
    }

    private ComponentManager createComponentManager()
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        //componentManager.put( SystemManager.ROLE, m_systemManager );
        componentManager.put( ConfigurationRepository.ROLE, m_repository );
        componentManager.makeReadOnly();
        return componentManager;
    }
}
