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

/**
 * This class is a static implementation of a ComponentManager. Allow ineritance
 * and extention so you can generate a tree of ComponentManager each defining
 * Component scope.
 *
 * @author <a href="mailto:scoobie@pop.systemy.it">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultComponentManager
    implements ComponentManager
{
    protected final HashMap               m_components = new HashMap();
    protected final ComponentManager      m_parent;

    public DefaultComponentManager()
    {
        this( null );
    }

    public DefaultComponentManager( final ComponentManager parent )
    {
        m_parent = parent;
    }

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

    public void put( final String name, final Component component )
    {
        m_components.put( name, component );
    }

    public void release( final Component component )
    {
        // if the ComponentManager handled pooling, it would be
        // returned to the pool here.
    }

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
}
