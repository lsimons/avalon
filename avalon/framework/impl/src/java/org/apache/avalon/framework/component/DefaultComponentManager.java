/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a static implementation of a ComponentManager. Allow ineritance
 * and extension so you can generate a tree of ComponentManager each defining
 * Component scope.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version 1.0
 * @deprecated Use {@link org.apache.avalon.framework.service.DefaultServiceManager} instead.
 */
public class DefaultComponentManager
    implements ComponentManager
{
    private final HashMap m_components = new HashMap();
    private final ComponentManager m_parent;
    private boolean m_readOnly;

    /**
     * Construct ComponentManager with no parent.
     *
     */
    public DefaultComponentManager()
    {
        this( null );
    }

    /**
     * Construct ComponentManager with specified parent.
     *
     * @param parent the ComponentManagers parent
     */
    public DefaultComponentManager( final ComponentManager parent )
    {
        m_parent = parent;
    }

    /**
     * Retrieve Component by role from ComponentManager.
     *
     * @param role the role
     * @return the Component
     * @throws ComponentException if an error occurs
     */
    public Component lookup( final String role )
        throws ComponentException
    {
        final Component component = (Component)m_components.get( role );

        if( null != component )
        {
            return component;
        }
        else if( null != m_parent )
        {
            return m_parent.lookup( role );
        }
        else
        {
            throw new ComponentException( role, "Unable to provide implementation." );
        }
    }

    /**
     * Returns <code>true</code> if the component manager is managing a component
     * with the specified role, <code>false</code> otherwise.
     *
     * @param role role of the component you are lokking for
     * @return <code>true</code> if the component manager has a component with that role
     */
    public boolean hasComponent( final String role )
    {
        boolean componentExists = false;

        try
        {
            this.release( this.lookup( role ) );
            componentExists = true;
        }
        catch( Throwable t )
        {
            // Ignore all throwables--we want a yes or no answer.
        }

        return componentExists;
    }

    /**
     * Place Component into ComponentManager.
     *
     * @param role the components role
     * @param component the component
     */
    public void put( final String role, final Component component )
    {
        checkWriteable();
        m_components.put( role, component );
    }

    /**
     * Release component.
     *
     * @param component the component
     */
    public void release( final Component component )
    {
        // if the ComponentManager handled pooling, it would be
        // returned to the pool here.
    }

    /**
     * Build a human readable representation of ComponentManager.
     *
     * @return the description of ComponentManager
     */
    public String toString()
    {
        final StringBuffer buffer = new StringBuffer();
        final Iterator components = m_components.keySet().iterator();
        buffer.append( "Components:" );

        while( components.hasNext() )
        {
            buffer.append( "[" );
            buffer.append( components.next() );
            buffer.append( "]" );
        }

        return buffer.toString();
    }

    /**
     * Helper method for subclasses to retrieve parent.
     *
     * @return the parent ComponentManager
     */
    protected final ComponentManager getParent()
    {
        return m_parent;
    }

    /**
     * Helper method for subclasses to retrieve component map.
     *
     * @return the component map
     */
    protected final Map getComponentMap()
    {
        return m_components;
    }

    /**
     * Make this component manager read only.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Check if this component manager is writeable.
     *
     * @throws IllegalStateException if this component manager is read-only
     */
    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            throw new IllegalStateException
                ( "ComponentManager is read only and can not be modified" );
        }
    }
}
