/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the default implementation of the ComponentSelector
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version 1.0
 * @deprecated Use {@link org.apache.avalon.framework.service.DefaultServiceSelector} instead.
 */
public class DefaultComponentSelector
    implements ComponentSelector
{
    private final HashMap m_components = new HashMap();
    private boolean m_readOnly;

    /**
     * Select the desired component.  It does not cascade, neither
     * should it.
     *
     * @param hint the hint to retrieve Component
     * @return the Component
     * @throws ComponentException if an error occurs
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
            throw new ComponentException( hint.toString(), "Unable to provide implementation." );
        }
    }

    /**
     * Returns whether a Component exists or not
     * @param hint the hint to retrieve Component
     * @return <code>true</code> if the Component exists
     */
    public boolean hasComponent( final Object hint )
    {
        boolean componentExists = false;

        try
        {
            this.release( this.select( hint ) );
            componentExists = true;
        }
        catch( Throwable t )
        {
            // Ignore all throwables--we want a yes or no answer.
        }

        return componentExists;
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
     * @param hint the hint to retrieve Component
     * @param component the component to add
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

    /**
     * Make this component selector read-only.
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
                ( "ComponentSelector is read only and can not be modified" );
        }
    }
}
