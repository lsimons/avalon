/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.connection;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

/**
 * A simple component manager that adapts from a {@link ServiceManager}
 * to a {@link ComponentManager}.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2002/08/16 04:14:32 $
 */
class AdaptingComponentManager
    implements ComponentManager
{
    private final ServiceManager m_serviceManager;

    AdaptingComponentManager( final ServiceManager serviceManager )
    {
        if( null == serviceManager )
        {
            throw new NullPointerException( "serviceManager" );
        }
        m_serviceManager = serviceManager;
    }

    public Component lookup( final String role )
        throws ComponentException
    {
        try
        {
            return (Component) m_serviceManager.lookup( role );
        }
        catch( final ClassCastException e )
        {
            final String error = 
              "Manager returned a non-component for the role: " + role;
            throw new ComponentException( error, e );
        }
        catch( final ServiceException se )
        {
            throw new ComponentException( se.getMessage(), se );
        }
    }

    public boolean hasComponent( final String role )
    {
        return m_serviceManager.hasService( role );
    }

    public void release( final Component component )
    {
    }
}
