/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.avalon.excalibur.collections.BucketMap;
import org.apache.avalon.excalibur.logger.LogKitManageable;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * Default component selector for Avalon's components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @version CVS $Revision: 1.4.2.4 $ $Date: 2002/05/23 18:34:54 $
 * @since 4.0
 */
public class ExcaliburComponentSelector
    extends AbstractDualLogEnabled
    implements Contextualizable,
    ComponentSelector,
    Composable,
    Configurable,
    Initializable,
    ThreadSafe,
    Disposable,
    RoleManageable,
    LogKitManageable
{
    /** The classloader used for this system. */
    private final ClassLoader m_loader;

    /** The ComponentSelector's name for logging purposes.
     */
    private static final String DEFAULT_NAME = "UnnamedSelector";

    /** The role name for this instance
     */
    private String m_rolename;

    /** The application context for components
     */
    protected Context m_context;

    /** The application context for components
     */
    private ComponentManager m_componentManager;

    /** Static configuraiton object.
     */
    private Configuration m_configuration;

    /** Static component handlers.
     */
    private BucketMap m_componentHandlers = new BucketMap();

    /** Dynamic component handlers mapping.
     */
    private BucketMap m_componentMapping = new BucketMap();

    /** Flag for if this is disposed or not.
     */
    private boolean m_disposed;

    /** Flag for if this is initialized or not.
     */
    private boolean m_initialized;

    /** The RoleManager to get hint shortcuts
     */
    private RoleManager m_roles;

    /** The RoleManager to get hint shortcuts
     */
    private LogkitLoggerManager m_logkit;

    /** Create the ComponentSelector */
    public ExcaliburComponentSelector()
    {
        this( Thread.currentThread().getContextClassLoader() );
    }

    /** Create the ComponentSelector with a Classloader */
    public ExcaliburComponentSelector( final ClassLoader loader )
    {
        if( loader == null )
        {
            m_loader = Thread.currentThread().getContextClassLoader();
        }
        else
        {
            m_loader = loader;
        }
    }

    /** Provide the application Context.
     */
    public void contextualize( final Context context )
    {
        if( null == m_context )
        {
            m_context = context;
        }
    }

    /** Compose the ComponentSelector so that we know what the parent ComponentManager is.
     */
    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        //HACK: Is this necessary???
        if( null == m_componentManager )
        {
            m_componentManager = componentManager;
        }
    }

    /** Properly initialize of the Child handlers.
     */
    public void initialize()
    {
        synchronized( this )
        {
            m_initialized = true;

            List keys = new ArrayList( m_componentHandlers.keySet() );

            for( int i = 0; i < keys.size(); i++ )
            {
                final Object key = keys.get( i );
                final ComponentHandler handler =
                    (ComponentHandler)m_componentHandlers.get( key );

                try
                {
                    handler.initialize();
                }
                catch( Exception e )
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "Caught an exception trying to initialize " +
                                           "of the component handler.", e );
                    }
                }

            }
        }
    }

    /**
     * Tests for existence of a component.
     */
    public boolean hasComponent( final Object hint )
    {
        if( !m_initialized ) return false;
        if( m_disposed ) return false;

        boolean exists = false;

        try
        {
            this.release( this.select( hint ) );
            exists = true;
        }
        catch( Throwable t )
        {
            // We can safely ignore all exceptions
        }

        return exists;
    }

    /**
     * Properly dispose of all the ComponentHandlers.
     */
    public void dispose()
    {
        synchronized( this )
        {
            Iterator keys = m_componentHandlers.keySet().iterator();
            List keyList = new ArrayList();

            while( keys.hasNext() )
            {
                Object key = keys.next();
                ComponentHandler handler =
                    (ComponentHandler)m_componentHandlers.get( key );

                handler.dispose();

                keyList.add( key );
            }

            keys = keyList.iterator();

            while( keys.hasNext() )
            {
                m_componentHandlers.remove( keys.next() );
            }

            keyList.clear();

            m_disposed = true;
        }
    }

    /**
     * Return an instance of a component based on a hint.  The Composable has already selected the
     * role, so the only part left it to make sure the Component is handled.
     */
    public Component select( final Object hint )
        throws ComponentException
    {
        if( !m_initialized )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Looking up component on an uninitialized ComponentManager: " + hint );
            }
        }

        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot select a Component from a disposed ComponentSelector" );
        }

        if( null == hint )
        {
            final String message = getName() + ": ComponentSelector Attempted to retrieve component with null hint.";
            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( message );
            }

            throw new ComponentException( message );
        }

        ComponentHandler handler = (ComponentHandler)m_componentHandlers.get( hint );

        // Retrieve the instance of the requested component
        if( null == handler )
        {
            final String message = getName() + ": ComponentSelector could not find the component for hint: " + hint;
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( message );
            }
            throw new ComponentException( message );
        }

        Component component = null;

        try
        {
            component = handler.get();
        }
        catch( final Exception e )
        {
            final String message = getName() + ": ComponentSelector could not access the Component for hint: " + hint;

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( message, e );
            }
            throw new ComponentException( message, e );
        }

        if( null == component )
        {
            final String message = getName() + ": ComponentSelector could not find the component for hint: " + hint;
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( message );
            }
            throw new ComponentException( message );
        }

        m_componentMapping.put( component, handler );
        return component;
    }

    /**
     * Default Configuration handler for ComponentSelector.
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "ComponentSelector setting up with root element: " +
                               m_configuration.getName() );
        }

        final String name = configuration.getName();
        if( name.equals( "component" ) )
        {
            m_rolename = m_configuration.getAttribute( "role" );
        }
        else
        {
            m_rolename = m_roles.getRoleForName( name );
        }

        Configuration[] instances = m_configuration.getChildren();

        for( int i = 0; i < instances.length; i++ )
        {
            final Object hint = instances[ i ].getAttribute( "name" ).trim();
            final String className;

            if( "component-instance".equals( instances[ i ].getName() ) )
            {
                className = (String)instances[ i ].getAttribute( "class" ).trim();
            }
            else
            {
                className = m_roles.getDefaultClassNameForHint( m_rolename,
                                                                instances[ i ].getName() );
            }

            try
            {
                final Class clazz = m_loader.loadClass( className );
                addComponent( hint, clazz, instances[ i ] );
            }
            catch( final ClassNotFoundException cnfe )
            {
                final String message =
                    "The component instance for '" + hint +
                    "' has an invalid class name (" + className + ").";
                if( getLogger().isErrorEnabled() )
                {
                    getLogger().error( message, cnfe );
                }

                throw new ConfigurationException( message, cnfe );
            }
            catch( final ComponentException ce )
            {
                if( getLogger().isErrorEnabled() )
                {
                    getLogger().error( "The component instance for '" + hint +
                                       "' is not valid.", ce );
                }

                throw new ConfigurationException( "Could not set up component", ce );
            }
            catch( final Exception e )
            {
                if( getLogger().isErrorEnabled() )
                {
                    getLogger().error( "Unexpected exception for hint: " + hint, e );
                }
                throw new ConfigurationException( "Unexpected exception", e );
            }
        }
    }

    /**
     * Configure the RoleManager
     */
    public void setRoleManager( final RoleManager roles )
    {
        if( null == m_roles )
        {
            m_roles = roles;
        }
    }


    /**
     * Configure the LogKitManager
     */
    public void setLogKitManager( final LogKitManager logkit )
    {
        if( null == m_logkit )
        {
            m_logkit = new LogkitLoggerManager( null, logkit );
        }
    }

    /**
     * Configure the LoggerManager.
     */
    public void setLoggerManager( final LoggerManager logkit )
    {
        if( null == m_logkit )
        {
            m_logkit = new LogkitLoggerManager( logkit, null );
        }
    }

    /**
     * Release the Component to the propper ComponentHandler.
     */
    public void release( final Component component )
    {
        if( null == component )
        {
            getLogger().warn( "Attempted to release a null component." );
            return;
        }

        final ComponentHandler handler =
            (ComponentHandler)m_componentMapping.get( component );

        if( null == handler )
        {
            getLogger().warn( "Attempted to release a " + component.getClass().getName() +
                              " but its handler could not be located." );
            return;
        }

        // ThreadSafe components will always be using a ThreadSafeComponentHandler,
        //  they will only have a single entry in the m_componentMapping map which
        //  should not be removed until the ComponentManager is disposed.  All
        //  other components have an entry for each instance which should be
        //  removed.
        if( !( handler instanceof ThreadSafeComponentHandler ) )
        {
            // Remove the component before calling put.  This is critical to avoid the
            //  problem where another thread calls put on the same component before
            //  remove can be called.
            m_componentMapping.remove( component );
        }

        try
        {
            handler.put( component );
        }
        catch( Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Error trying to release component", e );
            }
        }
    }

    /**
     * Obtain a new ComponentHandler for the specified component.  This method
     *  allows classes which extend the ExcaliburComponentSelector to use their
     *  own ComponentHandlers.
     *
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param componentManager The ComponentManager which will be managing
     *                         the Component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param logkitManager The current LogKitManager.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     */
    protected ComponentHandler getComponentHandler( final Class componentClass,
                                                    final Configuration configuration,
                                                    final ComponentManager componentManager,
                                                    final Context context,
                                                    final RoleManager roleManager,
                                                    final LogkitLoggerManager logkitManager )
        throws Exception
    {
        return ComponentHandler.getComponentHandler( componentClass,
                                                     configuration,
                                                     componentManager,
                                                     context,
                                                     roleManager,
                                                     logkitManager );
    }

    /**
     * Makes the ComponentHandlers available to subclasses.
     *
     * @return A reference to the componentHandler Map.
     */
    protected BucketMap getComponentHandlers()
    {
        return m_componentHandlers;
    }

    /** Add a new component to the manager.
     * @param hint the hint name for the new component.
     * @param component the class of this component.
     * @param configuration the configuration for this component.
     */
    public void addComponent( final Object hint,
                              final Class component,
                              final Configuration configuration )
        throws ComponentException
    {
        if( m_initialized )
        {
            throw new ComponentException( "Cannot add components to an initialized ComponentSelector", null );
        }

        try
        {
            final ComponentHandler handler = getComponentHandler( component,
                                                                  configuration,
                                                                  m_componentManager,
                                                                  m_context,
                                                                  m_roles,
                                                                  m_logkit );

            handler.setLogger( getLogkitLogger() );
            handler.enableLogging( getLogger() );
            handler.initialize();
            m_componentHandlers.put( hint, handler );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Adding " + component.getName() + " for " + hint.toString() );
            }
        }
        catch( final Exception e )
        {
            final String message =
                "Could not set up Component for hint: " + hint;
            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( message, e );
            }

            throw new ComponentException( message, e );
        }
    }

    /** Add a static instance of a component to the manager.
     * @param hint the hint for the component.
     * @param instance the instance of the component.
     */
    public void addComponentInstance( final Object hint, final Component instance )
    {
        if( m_initialized )
        {
            throw new IllegalStateException( "Cannot add components to an initialized ComponentSelector" );
        }

        try
        {
            final ComponentHandler handler =
                ComponentHandler.getComponentHandler( instance );
            handler.setLogger( getLogkitLogger() );
            handler.enableLogging( getLogger() );
            handler.initialize();
            m_componentHandlers.put( hint, handler );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Adding " + instance.getClass().getName() +
                                   " for " + hint.toString() );
            }
        }
        catch( final Exception e )
        {
            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( "Could not set up Component for hint: " + hint, e );
            }
        }
    }

    /**
     * Return this selector's configuration name or a default name if no such
     * configuration was provided. This accounts for the case when a static
     * component instance has been added through
     * <code>addComponentInstance</code> with no associated configuration
     */
    private String getName()
    {
        if( null != m_configuration &&
            !m_configuration.getName().equals( "" ) )
        {
            return m_configuration.getName();
        }

        return DEFAULT_NAME;
    }
}
