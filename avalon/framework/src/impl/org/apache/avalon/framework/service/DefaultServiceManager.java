/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a static implementation of a ServiceManager. Allow ineritance
 * and extension so you can generate a tree of ServiceManager each defining
 * Object scope.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class DefaultServiceManager
    implements ServiceManager
{
    private final HashMap               m_components = new HashMap();
    private final ServiceManager        m_parent;
    private boolean                     m_readOnly;

    /**
     * Construct ServiceManager with no parent.
     *
     */
    public DefaultServiceManager()
    {
        this( null );
    }

    /**
     * Construct ServiceManager with specified parent.
     *
     * @param parent the ServiceManager parent
     */
    public DefaultServiceManager( final ServiceManager parent )
    {
        m_parent = parent;
    }

    /**
     * Retrieve Object by role from ServiceManager.
     *
     * @param role the role
     * @return the Object
     * @exception ServiceException if an error occurs
     */
    public Object lookup( final String role )
        throws ServiceException
    {
        final Object component = m_components.get( role );

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
            throw new ServiceException( "Unable to provide implementation for " + role );
        }
    }

    public boolean hasService( final String role ) {
        boolean componentExists = false;

        try
        {
            this.lookup(role);
            componentExists = true;
        }
        catch (Throwable t)
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
    public void put( final String role, final Object object )
    {
        checkWriteable();
        m_components.put( role, object );
    }

    /**
     * Build a human readable representation of ComponentManager2.
     *
     * @return the description of ComponentManager2
     */
    public String toString()
    {
        final StringBuffer buffer = new StringBuffer();
        final Iterator components = m_components.keySet().iterator();
        buffer.append( "Services:" );

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
     * @return the parent ServiceManager
     */
    protected final ServiceManager getParent()
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

    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            throw new IllegalStateException( "ServiceManager is read only and can not be modified" );
        }
    }

    /**
     * Release the object.
     * @param object The <code>Object</code> to release.
     */
    public void release( Object object ){}

}
