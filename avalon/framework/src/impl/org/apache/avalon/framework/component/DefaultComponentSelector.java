/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.component;

import java.util.HashMap;

/**
 * This is the default implementation of the ComponentSelector
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DefaultComponentSelector
    implements ComponentSelector
{
    protected final HashMap m_components = new HashMap();

    /**
     * Select the desired component.  It does not cascade, neither
     * should it.
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
        m_components.put( hint, component );
    }
}
