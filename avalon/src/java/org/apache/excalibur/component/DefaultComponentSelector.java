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
import org.apache.avalon.Disposable;
import org.apache.avalon.component.Component;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.ComponentSelector;
import org.apache.avalon.component.Composable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.configuration.DefaultConfiguration;
import org.apache.avalon.context.Context;
import org.apache.avalon.context.Contextualizable;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.thread.ThreadSafe;

/**
 * Default component manager for Avalon's components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @version CVS $Revision: 1.2 $ $Date: 2001/04/20 20:48:34 $
 */
public class DefaultComponentSelector
    extends AbstractLoggable
    implements Contextualizable, ComponentSelector, Composable, Configurable, ThreadSafe, Disposable
{
    private static final String DEFAULT_NAME = "UnnamedSelector";

    /** The role name for this instance
     */
    private String           m_rolename;

    /** The application context for components
     */
    protected Context        m_context;

    /** The application context for components
     */
    private ComponentManager m_componentManager;

    /** Dynamic component handlers mapping.
     */
    private Map              m_componentMapping;

    /** Static configuraiton object.
     */
    private Configuration    m_configuration;

    /** Static component handlers.
     */
    private Map              m_componentHandlers;

    /** Flag for if this is disposed or not.
     */
    private boolean          m_disposed;

    /** Shorthand for hints
     */
    private Map              m_hints;

    /** The RoleManager to get hint shortcuts
     */
    private RoleManager      m_roles;

    /** Construct a new default component manager.
     */
    public DefaultComponentSelector()
    {
        // Setup the maps.
        m_componentHandlers = Collections.synchronizedMap( new HashMap() );
        m_componentMapping = Collections.synchronizedMap( new HashMap() );
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

    /**
     * Properly dispose of all the ComponentHandlers.
     */
    public synchronized void dispose()
    {
        m_disposed = true;

        Iterator keys = m_componentHandlers.keySet().iterator();
        List keyList = new ArrayList();

        while( keys.hasNext() )
        {
            Object key = keys.next();
            ComponentHandler handler =
                (ComponentHandler)m_componentHandlers.get( key );

            try {
                handler.dispose();
            } catch (Exception e) {
                getLogger().debug("Caught an exception disposing of component handler.", e);
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
     * Return an instance of a component based on a hint.  The Composable has already selected the
     * role, so the only part left it to make sure the Component is handled.
     */
    public Component select( final Object hint )
        throws ComponentException
    {
        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot select a Component " +
                                             "from a disposed ComponentSelector" );
        }

        if( null == hint )
        {
            final String message =
                getName() + ": ComponentSelector Attempted to retrieve component with null hint.";
            getLogger().error( message );
            throw new ComponentException( message );
        }

        ComponentHandler handler = (ComponentHandler)m_componentHandlers.get( hint );

        // Retrieve the instance of the requested component
        if( null == handler )
        {
            final String message =
                getName() + ": ComponentSelector could not find the component for hint: " + hint;
            throw new ComponentException( message );
        }

        Component component = null;

        try
        {
            component = handler.get();
        }
        catch( final Exception e )
        {
            final String message =
                getName() + ": ComponentSelector could not access the Component for hint: " + hint;
            throw new ComponentException( message, e );
        }

        if( null == component )
        {
            final String message =
                getName() + ": ComponentSelector could not find the component for hint: " + hint;
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
        getLogger().debug( "ComponentSelector setting up with root element: " +
                           m_configuration.getName() );

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

            if("component-instance".equals(instances[i].getName())) {
                className = (String)instances[i].getAttribute( "class" ).trim();
            } else {
                className = m_roles.getDefaultClassNameForHint(m_rolename, instances[i].getName());
            }

            try
            {
                final Class clazz = getClass().getClassLoader().loadClass( className );
                addComponent( hint, clazz, instances[i]);
            }
            catch( final Exception e )
            {
                final String message =
                    "The component instance for '" + hint + "' has an invalid class name.";
                getLogger().error( message, e );
                throw new ConfigurationException( message, e );
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
     * Release the Component to the propper ComponentHandler.
     */
    public void release( final Component component )
    {
        if( null == component ) return;

        final ComponentHandler handler =
            (ComponentHandler)m_componentMapping.get( component );

        if( null == handler ) return;

        try {
            handler.put( component );
        } catch (Exception e) {
            getLogger().debug("Error trying to release component", e);
        }

        m_componentMapping.remove( component );
    }

    /** Add a new component to the manager.
     * @param hint the hint name for the new component.
     * @param component the class of this component.
     * @param Configuration the configuration for this component.
     */
    public void addComponent( final Object hint,
                              final Class component,
                              final Configuration configuration )
        throws ComponentException
    {
        try
        {
            final ComponentHandler handler =
                ComponentHandler.getComponentHandler( component,
                                                      configuration,
                                                      m_componentManager,
                                                      m_context,
                                                      m_roles );

            handler.setLogger( getLogger() );
            handler.init();
            m_componentHandlers.put( hint, handler );
            getLogger().debug( "Adding " + component.getName() + " for " + hint.toString() );
        }
        catch( final Exception e )
        {
            final String message =
                "Could not set up Component for hint: " + hint;
            getLogger().error( message, e);
            throw new ComponentException( message, e );
        }
    }

    /** Add a static instance of a component to the manager.
     * @param hint the hint name for the component.
     * @param instance the instance of the component.
     */
    public void addComponentInstance( final String hint, final Object instance )
    {
        try
        {
            final ComponentHandler handler =
                ComponentHandler.getComponentHandler( (Component)instance );
            handler.setLogger( getLogger() );
            handler.init();
            m_componentHandlers.put( hint, handler );
            getLogger().debug( "Adding " + instance.getClass().getName() + " for " + hint.toString() );
        }
        catch( final Exception e )
        {
            getLogger().error( "Could not set up Component for hint: " + hint, e );
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
