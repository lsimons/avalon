/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avalon.activity.Disposable;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.component.Component;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.configuration.DefaultConfiguration;
import org.apache.avalon.context.Context;
import org.apache.avalon.context.Contextualizable;
import org.apache.avalon.logger.AbstractLoggable;

/**
 * Default component manager for Avalon's components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @version CVS $Revision: 1.3 $ $Date: 2001/04/25 14:24:38 $
 */
public class DefaultComponentManager
    extends AbstractLoggable
    implements ComponentManager, Configurable, Contextualizable, Disposable
{
    /** The application context for components
     */
    private Context      m_context;

    /** Static component mapping handlers.
     */
    private Map          m_componentMapping;

    /** Static component handlers.
     */
    private Map          m_componentHandlers;

    /** RoleInfos.
     */
    private RoleManager  m_roles;

    /** Is the Manager disposed or not? */
    private boolean      m_disposed;

    public DefaultComponentManager()
    {
        // Setup the maps.
        m_componentHandlers = Collections.synchronizedMap( new HashMap() );
        m_componentMapping = Collections.synchronizedMap( new HashMap() );
    }

    /** Set up the Component's Context.
     */
    public void contextualize( final Context context )
    {
        //HACK: Is this really needed ??? (Isn't a symtom of fault elsewhere in system)
        if( null == m_context )
        {
            m_context = context;
        }
    }

    /** Properly dispose of the Child handlers.
     */
    public synchronized void dispose( )
    {
        m_disposed = true;

        Iterator keys = m_componentHandlers.keySet().iterator();
        final List keyList = new ArrayList();

        while( keys.hasNext() )
        {
            final Object key = keys.next();
            final ComponentHandler handler =
                (ComponentHandler)m_componentHandlers.get( key );

            try
            {
                handler.dispose();
            }
            catch (Exception e)
            {
                getLogger().debug("Caught an exception trying to dispose of the component handler.", e);
            }

            keyList.add( key );
        }

        keys = keyList.iterator();

        while( keys.hasNext() )
        {
            m_componentHandlers.remove( keys.next() );
        }

        keyList.clear();
    }

    /**
     * Return an instance of a component based on a Role.  The Role is usually the Interface's
     * Fully Qualified Name(FQN)--unless there are multiple Components for the same Role.  In that
     * case, the Role's FQN is appended with "Selector", and we return a ComponentSelector.
     */
    public Component lookup( final String role )
        throws ComponentException
    {

        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot lookup components " +
                                             "on a disposed ComponentManager" );
        }

        if( null == role )
        {
            final String message =
                "ComponentManager Attempted to retrieve component with null role.";
            getLogger().error( message );
            throw new ComponentException( message );
        }

        ComponentHandler handler = (ComponentHandler)m_componentHandlers.get( role );

        // Retrieve the instance of the requested component
        if( null == handler )
        {
            getLogger().debug( "Could not find ComponentHandler, " +
                               "attempting to create one for role: " + role );

            try
            {
                final String className = m_roles.getDefaultClassNameForRole( role );
                final Class componentClass =
                    getClass().getClassLoader().loadClass( className );

                final Configuration configuration = new DefaultConfiguration( "", "-" );

                handler =
                    ComponentHandler.getComponentHandler( componentClass,
                                                          configuration,
                                                          this,
                                                          m_context,
                                                          m_roles );

                handler.setLogger( getLogger() );
                handler.initialize();
            }
            catch( final Exception e )
            {
                final String message =
                    "ComponentManager Could not find component for role: " + role;
                getLogger().error( message, e );
                throw new ComponentException( message, e );
            }

            m_componentHandlers.put( role, handler );
        }

        Component component = null;

        try
        {
            component = handler.get();

            if( component instanceof DefaultComponentSelector )
            {
                ((DefaultComponentSelector)component).setRoleManager( m_roles );
            }
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
                final String message = "Could not access the Component for role: " + role;
                throw new ComponentException( message, e );
            }
        }
        catch( final Exception e )
        {
            final String message = "Could not access the Component for role: " + role;
            throw new ComponentException( message, e );
        }

        m_componentMapping.put(component, handler);
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
        }

        // Set components

        final Configuration[] configurations = configuration.getChildren();

        for( int i = 0; i < configurations.length; i++ )
        {
            String type = configurations[i].getName(); // types are already trimmed

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
                        getLogger().debug( "Adding component (" + role + " = " + className + ")" );
                        final Class clazz =
                            getClass().getClassLoader().loadClass( className );
                        addComponent( role, clazz, configurations[ i ] );
                    }
                    catch( final Exception e )
                    {
                        final String message =
                            "Could not get class " + className + " for role " + role +
                            " on configuration element " + configurations[ i ].getName();

                        getLogger().error( message, e );
                        throw new ConfigurationException( message, e );
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
        //HACK: Is this really necessary???
        if( null == m_roles )
        {
            m_roles = roles;
        }
    }

    /**
     * Release a Component.  This implementation makes sure it has a handle on the propper
     * ComponentHandler, and let's the ComponentHandler take care of the actual work.
     */
    public void release( final Component component )
    {
        if( null == component ) return;

        final ComponentHandler handler =
            (ComponentHandler)m_componentMapping.get( component );

        if( null != handler )
        {
            try
            {
                handler.put( component );
            }
            catch (Exception e)
            {
                getLogger().debug("Error trying to release component.", e);
            }

            m_componentMapping.remove( component );
        }
    }

    /** Add a new component to the manager.
     * @param role the role name for the new component.
     * @param component the class of this component.
     * @param Configuration the configuration for this component.
     */
    public void addComponent( final String role,
                              final Class component,
                              final Configuration configuration )
        throws ComponentException
    {
        try
        {
            getLogger().debug("Attempting to get Handler for: " + role);
            final ComponentHandler handler =
                ComponentHandler.getComponentHandler( component, configuration, this, m_context, m_roles );

            getLogger().debug("Handler type = " + handler.getClass().getName());
            handler.setLogger( getLogger() );
            m_componentHandlers.put( role, handler );
        }
        catch( final Exception e )
        {
            throw new ComponentException( "Could not set up Component for role: " + role, e );
        }
    }

    /** Add a static instance of a component to the manager.
     * @param role the role name for the component.
     * @param instance the instance of the component.
     */
    public void addComponentInstance( final String role, final Object instance )
    {
        try
        {
            ComponentHandler handler = ComponentHandler.getComponentHandler( (Component)instance );
            handler.setLogger( getLogger() );
            m_componentHandlers.put( role, handler );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Could not set up Component for role: " + role, e );
        }
    }
}
