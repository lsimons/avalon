/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the default implementation of the ComponentSelector
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DefaultComponentSelector
    implements ComponentSelector
{
    private final HashMap  m_components = new HashMap();
    private boolean        m_readOnly;

    /**
     * Select the desired component.  It does not cascade, neither
     * should it.
     *
     * @param hint the hint to retrieve Component 
     * @return the Component
     * @exception ComponentException if an error occurs
     */
    public Component select( Object hint )
        throws ComponentException
    {
        final Component component = (Component)m_components.get( hint );

        if( null != component )
        {
            return component;
        }
        else
        {
            throw new ComponentException( "Unable to provide implementation for " +
                                          hint.toString() );
        }
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
     * Populate the ComponentSelector.
     */
    public void put( final Object hint, final Component component )
    {
        checkWriteable();
        m_components.put( hint, component );
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

    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            throw new IllegalStateException( "ComponentManager is read only and can not be modified" );
        }
    }
}
