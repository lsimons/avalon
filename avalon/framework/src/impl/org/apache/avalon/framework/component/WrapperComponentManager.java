/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.component;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;

/**
 *  * This is a {@link ComponentManager} implementation that can wrap around a
 * {@link ServiceManager} object effectively adapting a {@link ServiceManager}
 * interface to a {@link ComponentManager} interface.
 *
 * @author <a href="mailto:avalon-dev@jakarta.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.8 $ $Date: 2002/11/23 08:58:58 $
 */
public class WrapperComponentManager
    implements ComponentManager
{
    /**
     * The service manager we are adapting.
     */
    private final ServiceManager m_manager;

    public WrapperComponentManager( final ServiceManager manager )
    {
        if( null == manager )
        {
            throw new NullPointerException( "manager" );
        }

        m_manager = manager;
    }

    /**
     * Retrieve a component via a key.
     *
     * @param key the key
     * @return the component
     * @throws ComponentException if unable to aquire component
     */
    public Component lookup( final String key )
        throws ComponentException
    {
        try
        {
            final Object object = m_manager.lookup( key );
            if( object instanceof ServiceSelector )
            {
                return new WrapperComponentSelector( key, (ServiceSelector)object );
            }
            else if( object instanceof Component )
            {
                return (Component)object;
            }
        }
        catch( final ServiceException se )
        {
            throw new ComponentException( se.getKey(), se.getMessage(), se.getCause() );
        }

        final String message = "Role does not implement the Component " +
            "interface and thus can not be accessed via ComponentManager";
        throw new ComponentException( key, message );
    }

    /**
     * Check to see if a <code>Component</code> exists for a key.
     *
     * @param key  a string identifying the key to check.
     * @return True if the component exists, False if it does not.
     */
    public boolean hasComponent( final String key )
    {
        return m_manager.hasService( key );
    }

    /**
     * Return the <code>Component</code> when you are finished with it.  This
     * allows the <code>ComponentManager</code> to handle the End-Of-Life Lifecycle
     * events associated with the Component.  Please note, that no Exceptions
     * should be thrown at this point.  This is to allow easy use of the
     * ComponentManager system without having to trap Exceptions on a release.
     *
     * @param component The Component we are releasing.
     */
    public void release( final Component component )
    {
        if( component instanceof WrapperComponentSelector )
        {
            m_manager.
                release( ( (WrapperComponentSelector)component ).getWrappedSelector() );
        }
        else
        {
            m_manager.release( component );
        }
    }
}
