/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.util.HashMap;
import java.io.File;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.components.application.DefaultApplication;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ApplicationMBean;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.KernelMBean;
import org.apache.avalon.phoenix.interfaces.SystemManager;
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
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:leosimons@apache.org">Leo Simons</a>
 */
public class DefaultKernel
    extends AbstractLogEnabled
    implements Kernel, KernelMBean, Initializable, Serviceable, Disposable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultKernel.class );

    ///SystemManager provided by Embeddor
    private SystemManager m_systemManager;

    private SystemManager m_applicationManager;

    ///Configuration Repository
    private ConfigurationRepository m_repository;

    //Configuration Validator
    private ConfigurationValidator m_validator;

    private HashMap m_entries = new HashMap();

    private boolean m_addInvalidApplications;

    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_systemManager = (SystemManager)serviceManager.lookup( SystemManager.ROLE );
        m_repository = (ConfigurationRepository)serviceManager.
            lookup( ConfigurationRepository.ROLE );
        m_validator = (ConfigurationValidator)serviceManager.lookup( ConfigurationValidator.ROLE );
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        m_addInvalidApplications =
            configuration.getChild( "add-invalid-applications" ).getValueAsBoolean( false );
    }

    public void initialize()
        throws Exception
    {
        m_applicationManager =
            m_systemManager.getSubContext( null, "application" );
    }

    public void dispose()
    {
        final String[] names = getApplicationNames();
        for( int i = 0; i < names.length; i++ )
        {
            try
            {
                final SarEntry entry = (SarEntry)m_entries.get( names[ i ] );
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
        return (String[])m_entries.keySet().toArray( new String[ 0 ] );
    }

    public Application getApplication( final String name )
    {
        final SarEntry entry = (SarEntry)m_entries.get( name );
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
        //lock for application startup and shutdown
        synchronized( entry )
        {
            final String name = entry.getMetaData().getName();

            Application application = entry.getApplication();
            if( null == application )
            {
                try
                {
                    final Application newApp = new DefaultApplication();
                    final Logger childLogger =
                        getLogger().getChildLogger( name );
                    ContainerUtil.enableLogging( newApp, childLogger );

                    final ApplicationContext context =
                        createApplicationContext( entry );
                    newApp.setApplicationContext( context );

                    ContainerUtil.initialize( newApp );

                    application = newApp;
                }
                catch( final Throwable t )
                {
                    //Initialization failed so clean entry
                    //so invalid instance is not used
                    entry.setApplication( null );

                    final String message =
                        REZ.getString( "kernel.error.entry.initialize",
                                       entry.getMetaData().getName() );
                    throw new CascadingException( message, t );
                }

                try
                {
                    ContainerUtil.start( application );
                }
                catch( final Throwable t )
                {
                    final String message =
                        REZ.getString( "kernel.error.entry.start", entry.getMetaData().getName() );

                    if( m_addInvalidApplications )
                    {
                        getLogger().warn( message, t );
                    }
                    else
                    {
                        //Initialization failed so clean entry
                        //so invalid instance is not used
                        entry.setApplication( null );

                        throw new CascadingException( message, t );
                    }
                }

                entry.setApplication( application );

                // manage application
                try
                {
                    m_applicationManager.register( name,
                                                   application,
                                                   new Class[]{ApplicationMBean.class} );
                }
                catch( final Throwable t )
                {
                    final String message =
                        REZ.getString( "kernel.error.entry.manage", name );
                    throw new CascadingException( message, t );
                }
            }
        }
    }

    private void shutdown( final SarEntry entry )
        throws Exception
    {
        //lock for application startup and shutdown
        synchronized( entry )
        {
            final Application application = entry.getApplication();
            if( null != application )
            {
                entry.setApplication( null );
                ContainerUtil.shutdown( application );
            }
            else
            {
                final String message =
                    REZ.getString( "kernel.error.entry.nostop",
                                   entry.getMetaData().getName() );
                getLogger().warn( message );
            }
        }
    }

    public void addApplication( final SarMetaData metaData,
                                final File workDirectory,
                                final ClassLoader classLoader,
                                final Logger logger,
                                final Configuration server )
        throws Exception
    {
        final String name = metaData.getName();
        final SarEntry entry =
            new SarEntry( metaData, workDirectory, classLoader, logger, server );
        m_entries.put( name, entry );

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
        final SarMetaData metaData = entry.getMetaData();
        final String name = metaData.getName();

        final DefaultApplicationContext context =
            new DefaultApplicationContext( metaData,
                                           entry.getWorkDirectory(),
                                           entry.getClassLoader(),
                                           entry.getLogger() );

        ContainerUtil.enableLogging( context, createContextLogger( name ) );
        ContainerUtil.service( context, createServiceManager() );
        ContainerUtil.initialize( context );
        return context;
    }

    /**
     * Create a logger for specified ApplicationContext.
     *
     * @param name the name of application name
     * @return the Logger for context
     */
    private Logger createContextLogger( final String name )
    {
        final String loggerName = name + ".frame";
        final Logger childLogger =
            getLogger().getChildLogger( loggerName );
        return childLogger;
    }

    private ServiceManager createServiceManager()
    {
        final DefaultServiceManager componentManager = new DefaultServiceManager();
        componentManager.put( SystemManager.ROLE, m_systemManager );
        componentManager.put( ConfigurationRepository.ROLE, m_repository );
        componentManager.put( ConfigurationValidator.ROLE, m_validator );
        componentManager.put( Kernel.ROLE, this );
        componentManager.makeReadOnly();
        return componentManager;
    }

    public void removeApplication( String name )
        throws Exception
    {
        final SarEntry entry = (SarEntry)m_entries.remove( name );
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
                m_applicationManager.unregister( name );
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
