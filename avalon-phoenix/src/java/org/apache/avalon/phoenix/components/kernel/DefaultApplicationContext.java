/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.util.HashMap;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.avalon.phoenix.interfaces.SystemManager;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.excalibur.threadcontext.impl.DefaultThreadContextPolicy;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
class DefaultApplicationContext
    extends AbstractLogEnabled
    implements ApplicationContext, Serviceable, Initializable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplicationContext.class );

    //Log Hierarchy for application
    private final Hierarchy m_hierarchy;

    ///ClassLoader for application
    private final ClassLoader m_classLoader;

    ///ThreadContext for application
    private final ThreadContext m_threadContext;

    //Repository of configuration data to access
    private ConfigurationRepository m_repository;

    //Validator to validate configuration against
    private ConfigurationValidator m_validator;

    ///Place to expose Management beans
    private SystemManager m_systemManager;
    private SystemManager m_blockManager;

    private final SarMetaData m_metaData;

    protected DefaultApplicationContext( final SarMetaData metaData,
                                         final ClassLoader classLoader,
                                         final Hierarchy hierarchy )
    {
        m_metaData = metaData;
        m_classLoader = classLoader;
        m_hierarchy = hierarchy;

        final DefaultThreadContextPolicy policy = new DefaultThreadContextPolicy();
        final HashMap map = new HashMap( 1 );
        map.put( DefaultThreadContextPolicy.CLASSLOADER, m_classLoader );
        m_threadContext = new ThreadContext( policy, map );
    }

    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_repository = (ConfigurationRepository)serviceManager.
            lookup( ConfigurationRepository.ROLE );
        m_systemManager = (SystemManager)serviceManager.
            lookup( SystemManager.ROLE );
        m_validator = (ConfigurationValidator)serviceManager.
            lookup( ConfigurationValidator.ROLE );
    }

    public void initialize()
        throws Exception
    {
        m_blockManager = getManagementContext();
    }

    public SarMetaData getMetaData()
    {
        return m_metaData;
    }

    public ThreadContext getThreadContext()
    {
        return m_threadContext;
    }

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param category the logger category
     * @return the Logger
     */
    public Logger getLogger( final String category )
    {
        return m_hierarchy.getLoggerFor( category );
    }

    /**
     * Export specified object into management system.
     * The object is exported using specifed interface
     * and using the specified name.
     *
     * @param name the name of object to export
     * @param service the interface of object with which to export
     * @param object the actual object to export
     */
    public void exportObject( final String name,
                              final Class service,
                              final Object object )
        throws Exception
    {
        m_blockManager.register( name, object, new Class[]{service} );
    }

    /**
     * Unexport specified object from management system.
     *
     * @param name the name of object to unexport
     * @param service the interface of object with which to unexport
     */
    public void unexportObject( final String name, final Class service )
        throws Exception
    {
        m_blockManager.unregister( name );
    }

    /**
     * Get the Configuration for specified component.
     *
     * @param component the component
     * @return the Configuration
     */
    public Configuration getConfiguration( final String component )
      throws ConfigurationException
    {
        final Configuration configuration =
            m_repository.getConfiguration( m_metaData.getName(),
                                           component );

        //no validation of listeners just yet..
        if( hasBlockListener( component, this.m_metaData.getListeners() ) )
        {
            return configuration;
        }
        else if( m_validator.isValid( m_metaData.getName(), component, configuration ) )
        {

            return configuration;
        }
        else
        {
            throw new ConfigurationException( REZ.getString( "applicationcontext.error.invalidconfig", component ) );
        }
    }

    /**
     * Return true if specified array contains entry with specified name.
     *
     * @param name the blocks name
     * @param listeners the set of BlockListenerMetaData objects to search
     * @return true if block present, false otherwise
     */
    private boolean hasBlockListener( final String name,
                                      final BlockListenerMetaData[] listeners )
    {
        for( int i = 0; i < listeners.length; i++ )
        {
            if( listeners[i].getName().equals( name ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     *  Returns the local SystemManager where the blocks should be registered
     *  for management.
     *
     *  TODO: context should probably be passed in by reference from the kernel
     */
    private SystemManager getManagementContext()
        throws ManagerException
    {
        final SystemManager appContext =
            m_systemManager.getSubContext( null, "application" );
        return appContext.getSubContext( m_metaData.getName(), "block" );
    }
}
