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
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;

/**
 * Default component manager for Avalon's components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/04/10 05:38:43 $
 * @since 4.0
 */
public class ExcaliburComponentManager
    extends AbstractLoggable
    implements ComponentManager,
    Configurable,
    Contextualizable,
    Initializable,
    Disposable,
    RoleManageable,
    LogKitManageable
{
    /** The parent ComponentManager */
    private final ComponentManager m_parentManager;

    /** The classloader used for this system. */
    private final ClassLoader m_loader;

    /** The application context for components */
    private Context m_context;

    /** Static component mapping handlers. */
    private final BucketMap m_componentMapping = new BucketMap();

    /** Used to map roles to ComponentHandlers. */
    private final BucketMap m_componentHandlers = new BucketMap();

    /** RoleInfos. */
    private RoleManager m_roles;

    /** LogKitManager. */
    private LogKitManager m_logkit;

    /** Is the Manager disposed or not? */
    private boolean m_disposed;

    /** Is the Manager initialized? */
    private boolean m_initialized;

    /** Create the ComponentManager */
    public ExcaliburComponentManager()
    {
        this( null, Thread.currentThread().getContextClassLoader() );
    }

    /** Create the ComponentManager with a Classloader */
    public ExcaliburComponentManager( final ClassLoader loader )
    {
        this( null, loader );
    }

    /** Create the ComponentManager with a Classloader and parent ComponentManager */
    public ExcaliburComponentManager( final ComponentManager manager, final ClassLoader loader )
    {
        if( null == loader )
        {
            m_loader = Thread.currentThread().getContextClassLoader();
        }
        else
        {
            m_loader = loader;
        }

        m_parentManager = manager;
    }

    /** Create the ComponentManager with a parent ComponentManager */
    public ExcaliburComponentManager( final ComponentManager manager )
    {
        this( manager, Thread.currentThread().getContextClassLoader() );
    }

    /** Set up the Component's Context.
     */
    public void contextualize( final Context context )
    {
        if( null == m_context )
        {
            m_context = context;
        }
    }

    /**
     * Tests for existence of a component.  Please note that this test is for
     * <strong>existing</strong> components, and a component will not be created
     * to satisfy the request.
     */
    public boolean hasComponent( final String role )
    {
        if( !m_initialized ) return false;
        if( m_disposed ) return false;

        boolean exists = m_componentHandlers.containsKey( role );

        if( !exists && null != m_parentManager )
        {
            exists = m_parentManager.hasComponent( role );
        }

        return exists;
    }

    /** Properly initialize of the Child handlers.
     */
    public void initialize()
        throws Exception
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
                    if( getLogger().isErrorEnabled() )
                    {
                        getLogger().error( "Caught an exception trying to initialize " +
                                           "the component handler.", e );
                    }
                }

            }
        }
    }

    /** Properly dispose of the Child handlers.
     */
    public void dispose()
    {
        synchronized( this )
        {
            boolean forceDisposal = false;

            final List disposed = new ArrayList();

            while( m_componentHandlers.size() > 0 )
            {
                for( Iterator iterator = m_componentHandlers.keySet().iterator();
                     iterator.hasNext(); )
                {
                    final Object role = iterator.next();

                    final ComponentHandler handler =
                        (ComponentHandler)m_componentHandlers.get( role );

                    if( forceDisposal || handler.canBeDisposed() )
                    {
                        if( forceDisposal && getLogger().isWarnEnabled() )
                        {
                            getLogger().warn
                                ( "disposing of handler for unreleased component"
                                  + " (role: " + role + ")" );
                        }

                        handler.dispose();
                        disposed.add( role );
                    }
                }

                if( disposed.size() > 0 )
                {
                    removeDisposedHandlers( disposed );
                }
                else
                {   // no more disposable handlers!
                    forceDisposal = true;
                }
            }

            m_disposed = true;
        }
    }

    private void removeDisposedHandlers( List disposed )
    {

        for( Iterator iterator = disposed.iterator(); iterator.hasNext(); )
        {
            m_componentHandlers.remove( iterator.next() );
        }

        disposed.clear();
    }

    /**
     * Return an instance of a component based on a Role.  The Role is usually the Interface's
     * Fully Qualified Name(FQN)--unless there are multiple Components for the same Role.  In that
     * case, the Role's FQN is appended with "Selector", and we return a ComponentSelector.
     */
    public Component lookup( final String role )
        throws ComponentException
    {
        if( !m_initialized )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Looking up component on an uninitialized ComponentManager: " + role );
            }
        }

        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot lookup components on a disposed ComponentManager" );
        }

        if( null == role )
        {
            final String message =
                "ComponentManager Attempted to retrieve component with null role.";

            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( message );
            }
            throw new ComponentException( message );
        }

        ComponentHandler handler = (ComponentHandler)m_componentHandlers.get( role );

        // Retrieve the instance of the requested component
        if( null == handler )
        {
            if( m_parentManager != null )
            {
                try
                {
                    return m_parentManager.lookup( role );
                }
                catch( Exception e )
                {
                    // ignore.  If the exception is thrown, we try to
                    // create the component next
                }
            }

            if( null != m_roles )
            {
                final String className = m_roles.getDefaultClassNameForRole( role );

                if( null != className )
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "Could not find ComponentHandler, attempting to create one for role: " + role );
                    }

                    try
                    {
                        final Class componentClass = m_loader.loadClass( className );

                        final Configuration configuration = new DefaultConfiguration( "", "-" );

                        handler = getComponentHandler( componentClass,
                                                       configuration,
                                                       m_context,
                                                       m_roles,
                                                       m_logkit );

                        handler.setLogger( getLogger() );
                        handler.initialize();
                    }
                    catch( final Exception e )
                    {
                        final String message = "Could not find component";
                        if( getLogger().isDebugEnabled() )
                        {
                            getLogger().debug( message + " for role: " + role, e );
                        }
                        throw new ComponentException( message, e );
                    }

                    m_componentHandlers.put( role, handler );
                }
            }
            else
            {
                getLogger().debug( "Component requested without a RoleManager set.\nThat means this ComponentManager was not configured." );
            }
        }

        if( null == handler )
        {
            final String message = "Could not find component";
            if( getLogger().isErrorEnabled() )
            {
                getLogger().debug( message + " for role: " + role );
            }
            throw new ComponentException( message );
        }

        Component component = null;

        try
        {
            component = handler.get();
        }
        catch( final IllegalStateException ise )
        {
            try
            {
                handler.initialize();
                component = handler.get();
            }
            catch( final Exception e )
            {
                final String message = "Could not access the Component";
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( message + " for role: " + role, e );
                }

                throw new ComponentException( message, e );
            }
        }
        catch( final Exception e )
        {
            final String message = "Could not access the Component";
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( message + " for role: " + role, e );
            }

            throw new ComponentException( message, e );
        }

        // Add a mapping between the component and its handler.
        //  In the case of a ThreadSafeComponentHandler, the same component will be mapped
        //  multiple times but because each put will overwrite the last, this is not a
        //  problem.  Checking to see if the put has already been done would be slower.
        m_componentMapping.put( component, handler );

        return component;
    }

    /**
     * Configure the ComponentManager.
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == m_roles )
        {
            DefaultRoleManager role_info = new DefaultRoleManager();
            role_info.setLogger( getLogger() );
            role_info.configure( configuration );
            m_roles = role_info;
            getLogger().debug( "No RoleManager given, deriving one from configuration" );
        }

        // Set components

        final Configuration[] configurations = configuration.getChildren();

        for( int i = 0; i < configurations.length; i++ )
        {
            String type = configurations[ i ].getName();

            if( !type.equals( "role" ) )
            {
                String role = configurations[ i ].getAttribute( "role", "" );
                String className = configurations[ i ].getAttribute( "class", "" );

                if( role.equals( "" ) )
                {
                    role = m_roles.getRoleForName( type );
                }

                if( null != role && !role.equals( "" ) )
                {
                    if( className.equals( "" ) )
                    {
                        className = m_roles.getDefaultClassNameForRole( role );
                    }

                    try
                    {
                        if( getLogger().isDebugEnabled() )
                        {
                            getLogger().debug( "Adding component (" + role + " = " +
                                               className + ")" );
                        }

                        final Class clazz = m_loader.loadClass( className );
                        addComponent( role, clazz, configurations[ i ] );
                    }
                    catch( final ClassNotFoundException cnfe )
                    {
                        final String message = "Could not get class ";

                        if( getLogger().isErrorEnabled() )
                        {
                            getLogger().error( message + className + " for role " + role +
                                               " on configuration element " + configurations[ i ].getName(), cnfe );
                        }

                        throw new ConfigurationException( message, cnfe );
                    }
                    catch( final ComponentException ce )
                    {
                        final String message = "Bad component ";

                        if( getLogger().isErrorEnabled() )
                        {
                            getLogger().error( message + className + " for role " + role +
                                               " on configuration element " + configurations[ i ].getName(), ce );
                        }

                        throw new ConfigurationException( message, ce );
                    }
                    catch( final Exception e )
                    {
                        if( getLogger().isErrorEnabled() )
                        {
                            getLogger().error( "Unexpected exception for hint: " + role, e );
                        }
                        throw new ConfigurationException( "Unexpected exception", e );
                    }
                }
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
            m_logkit = logkit;
        }
    }

    /**
     * Release a Component.  This implementation makes sure it has a handle on the propper
     * ComponentHandler, and let's the ComponentHandler take care of the actual work.
     */
    public void release( final Component component )
    {
        if( null == component )
        {
            getLogger().warn( "Attempted to release a null component." );
            return;
        }

        // The m_componentMapping BucketMap itself is threadsafe, and because the same component
        //  will never be released by more than one thread, this method does not need any
        //  synchronization around the access to the map.

        final ComponentHandler handler =
            (ComponentHandler)m_componentMapping.get( component );

        if( null != handler )
        {
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
                    getLogger().debug( "Error trying to release component.", e );
                }
            }
        }
        else if( null != m_parentManager )
        {
            m_parentManager.release( component );
        }
        else
        {
            getLogger().warn( "Attempted to release a " + component.getClass().getName() +
                " but its handler could not be located." );
        }
    }

    /**
     * Obtain a new ComponentHandler for the specified component.  This method
     *  allows classes which extend the ExcaliburComponentManager to use their
     *  own ComponentHandlers.
     *
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param logkitManager The current LogKitManager.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     */
    protected ComponentHandler getComponentHandler( final Class componentClass,
                                                    final Configuration configuration,
                                                    final Context context,
                                                    final RoleManager roleManager,
                                                    final LogKitManager logkitManager )
        throws Exception
    {
        return ComponentHandler.getComponentHandler( componentClass,
                                                     configuration,
                                                     this,
                                                     context,
                                                     roleManager,
                                                     logkitManager );
    }

    /**
     * Makes the ComponentHandlers available to subclasses.
     *
     * @return A collection of the reference to the componentHandler Map.
     */
    protected BucketMap getComponentHandlers()
    {
        return m_componentHandlers;
    }
    
    /**
     * Add a new component to the manager.
     *
     * @param role the role name for the new component.
     * @param component the class of this component.
     * @param configuration the configuration for this component.
     */
    public void addComponent( final String role,
                              final Class component,
                              final Configuration configuration )
        throws ComponentException
    {
        if( m_initialized )
        {
            throw new ComponentException( "Cannot add components to an initialized ComponentManager", null );
        }

        try
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Attempting to get Handler for: " + role );
            }

            final ComponentHandler handler = getComponentHandler( component,
                                                                  configuration,
                                                                  m_context,
                                                                  m_roles,
                                                                  m_logkit );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Handler type = " + handler.getClass().getName() );
            }

            handler.setLogger( getLogger() );
            m_componentHandlers.put( role, handler );
        }
        catch( final Exception e )
        {
            throw new ComponentException( "Could not set up Component for role: " +
                                          role, e );
        }
    }

    /** Add a static instance of a component to the manager.
     * @param role the role name for the component.
     * @param instance the instance of the component.
     */
    public void addComponentInstance( final String role, final Component instance )
    {
        if( m_initialized )
        {
            throw new IllegalStateException( "Cannot add components to an initialized ComponentManager" );
        }

        try
        {
            ComponentHandler handler =
                ComponentHandler.getComponentHandler( instance );
            handler.setLogger( getLogger() );
            m_componentHandlers.put( role, handler );
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not set up Component for role: " + role, e );
            }
        }
    }
}
