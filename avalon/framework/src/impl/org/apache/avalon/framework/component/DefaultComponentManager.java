/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a static implementation of a ComponentManager. Allow ineritance
 * and extention so you can generate a tree of ComponentManager each defining
 * Component scope.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultComponentManager
    implements ComponentManager
{
    //TODO: Make these private before next release
    protected final HashMap               m_components = new HashMap();
    protected final ComponentManager      m_parent;

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
     * @exception ComponentException if an error occurs
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
            throw new ComponentException( "Unable to provide implementation for " + role );
        }
    }

    /**
     * Place Component into ComponentManager.
     *
     * @param role the components role
     * @param component the component
     */
    public void put( final String role, final Component component )
    {
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
}
