/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.components.application.DefaultApplication;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ApplicationMBean;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.KernelMBean;
import org.apache.avalon.phoenix.interfaces.SystemManager;
import org.apache.avalon.phoenix.metadata.SarMetaData;
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
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:leosimons@apache.org">Leo Simons</a>
 */
public class DefaultKernel
    extends AbstractLogEnabled
    implements Kernel, KernelMBean, Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultKernel.class );

    ///SystemManager provided by Embeddor
    private SystemManager m_systemManager;

    ///Configuration Repository
    private ConfigurationRepository m_repository;

    private HashMap m_entrys = new HashMap();

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
        if( null == entry )
        {
            return null;
        }
        else
        {
            return entry.getApplication();
        }
    }

    /**
     * Create and initialize the application instance if it is not already initialized.
     *
     * @param entry the entry for application
     * @throws Exception if an error occurs
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
                application = new DefaultApplication( entry.getMetaData() );
                setupLogger( application, name );

                final ApplicationContext context = createApplicationContext( entry );
                application.setApplicationContext( context );

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
                throw new CascadingException( message, t );
            }

            // manage application
            try
            {
                m_systemManager.register( name + ",type=Application", application, new Class[]{ApplicationMBean.class} );
            }
            catch( final Throwable t )
            {
                final String message =
                    REZ.getString( "kernel.error.entry.manage", name );
                throw new CascadingException( message, t );
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
                                final Hierarchy hierarchy,
                                final Configuration server )
        throws Exception
    {
        final String name = metaData.getName();
        final SarEntry entry = new SarEntry( metaData, classLoader, hierarchy, server );
        m_entrys.put( name, entry );

        try
        {
            startup( (SarEntry)entry );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "kernel.error.entry.start", name );
            getLogger().warn( message, e );
            throw e;
        }
    }

    private ApplicationContext createApplicationContext( final SarEntry entry )
        throws Exception
    {
        final DefaultApplicationContext context =
            new DefaultApplicationContext( entry.getMetaData(),
                                           entry.getClassLoader(),
                                           entry.getHierarchy() );

        setupLogger( context, entry.getMetaData().getName() + ".frame" );

            final ComponentManager componentManager = createComponentManager();
            ( (Composable)context ).compose( componentManager );
        context.configure( entry.getConfiguration() );
        return context;
    }

    private ComponentManager createComponentManager()
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        componentManager.put( SystemManager.ROLE, m_systemManager );
        componentManager.put( ConfigurationRepository.ROLE, m_repository );
        componentManager.makeReadOnly();
        return componentManager;
    }

    public void removeApplication( String name )
        throws Exception
    {
        final SarEntry entry = (SarEntry)m_entrys.remove( name );
        if( null == entry )
        {
            final String message =
                REZ.getString( "kernel.error.entry.initialize", name );
            throw new Exception( message );
        }
        else
        {
            // un-manage application
            try
            {
                m_systemManager.unregister( name + ",type=Application" );
            }
            catch( final Throwable t )
            {
                final String message =
                    REZ.getString( "kernel.error.entry.unmanage", name );
                throw new CascadingException( message, t );
            }

            shutdown( entry );
        }
    }
}
