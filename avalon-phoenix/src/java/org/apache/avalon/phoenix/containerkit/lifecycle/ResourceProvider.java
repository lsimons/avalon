/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.lifecycle;

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
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public interface ResourceProvider
{
    String ROLE = ResourceProvider.class.getName();

    /**
     * Create the object specified by entry.
     *
     * @param entry the entry
     * @return the new object
     * @throws Exception if unable to create resource
     */
    Object createObject( Object entry )
        throws Exception;

    /**
     * Create a new Logger for component.
     *
     * @param entry the entry
     * @return a new Logger for component
     * @throws Exception if unable to create resource
     */
    Logger createLogger( Object entry )
        throws Exception;

    /**
     * Create a new Context for component.
     *
     * @param entry the entry
     * @return a new Context for component
     * @throws Exception if unable to create resource
     */
    Context createContext( Object entry )
        throws Exception;

    /**
     * Create a new ComponentManager for component.
     *
     * @param entry the entry
     * @return a new ComponentManager for component
     * @throws Exception if unable to create resource
     */
    ComponentManager createComponentManager( Object entry )
        throws Exception;

    /**
     * Create a new ServiceManager for component.
     *
     * @param entry the entry
     * @return a new ServiceManager for component
     * @throws Exception if unable to create resource
     */
    ServiceManager createServiceManager( Object entry )
        throws Exception;

    /**
     * Create a new Configuration object for component.
     *
     * @param entry the entry
     * @return a new Configuration object for component
     * @throws Exception if unable to create resource
     */
    Configuration createConfiguration( Object entry )
        throws Exception;

    /**
     * Create a new Parameters object for component.
     *
     * @param entry the entry
     * @return a new Parameters object for component
     * @throws Exception if unable to create resource
     */
    Parameters createParameters( Object entry )
        throws Exception;
}
