/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.service;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the default implementation of the ServiceSelector
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class DefaultServiceSelector
    implements ServiceSelector
{
    private final HashMap  m_objects = new HashMap();
    private boolean        m_readOnly;

    /**
     * Select the desired object.
     *
     * @param hint the hint to retrieve Object
     * @return the Object
     * @exception ServiceException if an error occurs
     */
    public Object select( Object hint )
        throws ServiceException
    {
        final Object object = m_objects.get( hint );

        if( null != object )
        {
            return object;
        }
        else
        {
            throw new ServiceException( "Unable to provide implementation for " +
                                          hint.toString() );
        }
    }

    /**
     * Returns whether a Object exists or not
     */
    public boolean isSelectable( final Object hint ) {
        boolean objectExists = false;

        try
        {
            this.release(this.select(hint));
            objectExists = true;
        }
        catch (Throwable t)
        {
            // Ignore all throwables--we want a yes or no answer.
        }

        return objectExists;
    }

    /**
     * Release object.
     *
     * @param <code>Object</code> the object to release
     */
    public void release( final Object object )
    {
        // if the ServiceManager handled pooling, it would be
        // returned to the pool here.
    }

    /**
     * Populate the ServiceSelector.
     */
    public void put( final Object hint, final Object object )
    {
        checkWriteable();
        m_objects.put( hint, object );
    }

    /**
     * Helper method for subclasses to retrieve object map.
     *
     * @return the object map
     */
    protected final Map getObjectMap()
    {
        return m_objects;
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
            throw new IllegalStateException( "ServiceSelector is read only and can not be modified" );       }
    }
}
