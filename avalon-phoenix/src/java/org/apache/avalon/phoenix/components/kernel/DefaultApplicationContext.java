/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.components.ContainerConstants;
import org.apache.avalon.phoenix.components.util.ResourceUtil;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.avalon.phoenix.interfaces.SystemManager;
import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;
import org.apache.avalon.phoenix.containerkit.registry.PartitionProfile;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.excalibur.threadcontext.impl.DefaultThreadContextPolicy;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
class DefaultApplicationContext
    extends AbstractLogEnabled
    implements ApplicationContext, Serviceable, Initializable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplicationContext.class );

    //Log Hierarchy for application
    private final Logger m_hierarchy;

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

    private final PartitionProfile m_profile;
    private final File m_workDirectory;
    private final File m_homeDirectory;

    /**
     * The map containing all the named loaders.
     */
    private final Map m_loaders;

    /**
     * The kernel associate with context
     */
    private Kernel m_kernel;

    protected DefaultApplicationContext( final PartitionProfile profile,
                                         final File homeDirectory,
                                         final File workDirectory,
                                         final ClassLoader classLoader,
                                         final Logger hierarchy,
                                         final Map loaders )
    {
        if( null == profile )
        {
            throw new NullPointerException( "profile" );
        }
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        if( null == hierarchy )
        {
            throw new NullPointerException( "hierarchy" );
        }
        if( null == workDirectory )
        {
            throw new NullPointerException( "workDirectory" );
        }
        if( null == homeDirectory )
        {
            throw new NullPointerException( "homeDirectory" );
        }
        m_profile = profile;
        m_classLoader = classLoader;
        m_hierarchy = hierarchy;
        m_workDirectory = workDirectory;
        m_homeDirectory = homeDirectory;
        m_loaders = loaders;

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
        m_kernel = (Kernel)serviceManager.lookup( Kernel.ROLE );
    }

    public void initialize()
        throws Exception
    {
        m_blockManager = getManagementContext();
    }

    public InputStream getResourceAsStream( final String name )
    {
        final File file =
            ResourceUtil.getFileForResource( name,
                                             getHomeDirectory(),
                                             m_workDirectory );
        if( !file.exists() )
        {
            return null;
        }
        else
        {
            try
            {
                return new FileInputStream( file );
            }
            catch( FileNotFoundException e )
            {
                //Should never happen
                return null;
            }
        }
    }

    public PartitionProfile getPartitionProfile()
    {
        return m_profile;
    }

    public ThreadContext getThreadContext()
    {
        return m_threadContext;
    }

    public void requestShutdown()
    {
        final Thread thread = new Thread( "AppShutdown" )
        {
            public void run()
            {
                schedulShutdown();
            }
        };
        thread.start();
    }

    private void schedulShutdown()
    {
        try
        {
            //Sleep for a little bit so that the
            //thread that requested this method can
            //return and do whatever it needs to be
            //done
            Thread.sleep( 2 );
            m_kernel.removeApplication( getName() );
        }
        catch( Exception e )
        {
            final String message =
                REZ.getString( "applicationcontext.error.noremove",
                               getName() );
            getLogger().error( message, e );
        }
    }

    public File getHomeDirectory()
    {
        return m_homeDirectory;
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
        return m_hierarchy.getChildLogger( category );
    }

    /**
     * Export specified object into management system.
     * The object is exported using specifed interface
     * and using the specified name.
     *
     * @param name the name of object to export
     * @param services the interface of object with which to export
     * @param object the actual object to export
     */
    public void exportObject( final String name,
                              final Class[] services,
                              final Object object )
        throws Exception
    {
        m_blockManager.register( name, object, services );
    }

    /**
     * Unexport specified object from management system.
     *
     * @param name the name of object to unexport
     */
    public void unexportObject( final String name )
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
            m_repository.getConfiguration( getName(),
                                           component );

        //no validation of listeners just yet..
        final PartitionMetaData partition =
            m_profile.getMetaData().getPartition( ContainerConstants.LISTENER_PARTITION );
        final boolean isListener = null != partition.getComponent( component );
        if( isListener )
        {
            return configuration;
        }
        else if( m_validator.isValid( getName(),
                                      component,
                                      configuration ) )
        {

            return configuration;
        }
        else
        {
            final String message =
                REZ.getString( "applicationcontext.error.invalidconfig",
                               component );
            throw new ConfigurationException( message );
        }
    }

    public ClassLoader getClassLoader( final String name )
        throws Exception
    {
        final ClassLoader classLoader = (ClassLoader)m_loaders.get(name);
        if( null == classLoader )
        {
            final String message =
                REZ.getString( "applicationcontext.error.noloader",
                               name );
            throw new Exception( message );
        }
        return classLoader;
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
        return appContext.getSubContext( getName(), "block" );
    }

    private String getName()
    {
        return m_profile.getMetaData().getName();
    }
}
