/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

/**
 * An adapting class for ComponentManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/11/07 05:00:17 $
 */
class WrapperComponentManager implements ComponentManager
{
    private final ServiceManager m_manager;

    public WrapperComponentManager( final ServiceManager manager )
    {
        m_manager = manager;
    }

    public Component lookup( String role )
        throws ComponentException
    {
        try
        {
            final Object object = m_manager.lookup( role );
            if( object instanceof Component )
            {
                return (Component)object;
            }
        }
        catch( ServiceException e )
        {
            throw new ComponentException( e.getRole(), e.getMessage(), e.getCause() );
        }

        final String message = "Role does not implement the Component " +
            "interface and can not be accessed via ComponentManager";
        throw new ComponentException( role, message );
    }

    public boolean hasComponent( final String role )
    {
        return m_manager.hasService( role );
    }

    public void release( final Component component )
    {
        m_manager.release( component );
    }
}
