/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.lifecycle;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.components.lifecycle.ComponentEntry;

/**
 *
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/05/15 12:30:02 $
 */
public interface ResourceAccessor
{
    Object createObject( ComponentEntry entry )
        throws Exception;

    Logger createLogger( ComponentEntry entry )
        throws Exception;

    Context createContext( ComponentEntry entry )
        throws Exception;

    ComponentManager createComponentManager( ComponentEntry entry )
        throws Exception;

    ServiceManager createServiceManager( ComponentEntry entry )
        throws Exception;

    Configuration createConfiguration( ComponentEntry entry )
        throws Exception;

    Parameters createParameters( ComponentEntry entry )
        throws Exception;
}
