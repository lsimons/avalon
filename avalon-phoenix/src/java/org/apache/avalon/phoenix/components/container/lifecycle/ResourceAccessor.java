/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.container.lifecycle;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * The interface via which resources required for a component
 * are aquired.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/06/04 04:35:08 $
 */
public interface ResourceAccessor
{
    Object createObject( Object entry )
        throws Exception;

    Logger createLogger( Object entry )
        throws Exception;

    Context createContext( Object entry )
        throws Exception;

    ComponentManager createComponentManager( Object entry )
        throws Exception;

    ServiceManager createServiceManager( Object entry )
        throws Exception;

    Configuration createConfiguration( Object entry )
        throws Exception;

    Parameters createParameters( Object entry )
        throws Exception;
}
