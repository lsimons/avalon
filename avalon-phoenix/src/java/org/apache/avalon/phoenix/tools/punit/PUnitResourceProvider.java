/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.WrapperComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.containerkit.lifecycle.ResourceProvider;

/**
 * PUnitResourceProvider
 *
 * @author Paul Hammant
 */
public class PUnitResourceProvider
    implements ResourceProvider
{
    private ServiceManager m_serviceManager;
    private Configuration m_configuration;
    private Logger m_logger;

    /**
     * PUnitResourceProvider
     * @param serviceManager The service manager
     * @param configuration The configuration
     */
    public PUnitResourceProvider( final ServiceManager serviceManager,
                                  final Configuration configuration,
                                  final Logger logger )
    {
        m_serviceManager = serviceManager;
        m_configuration = configuration;
        m_logger = logger;
    }

    /**
     * Create an object
     *
     * @param object The object
     * @return The returned object
     * @throws Exception If a problm
     */
    public Object createObject( final Object object )
        throws Exception
    {
        return object;
    }

    /**
     * Create a Logger
     * @param object The object to make a logger for
     * @return The Logger
     * @throws Exception If a problem
     */
    public Logger createLogger( final Object object ) throws Exception
    {
        return m_logger;
    }

    /**
     * Create some Context
     * @param object For this object
     * @return the context
     * @throws Exception If a problem
     */
    public Context createContext( final Object object )
        throws Exception
    {
        return new PUnitBlockContext();
    }

    /**
     * Create a Comp Mgr
     * @param object For this object
     * @return The comp mgr
     * @throws Exception If a problem
     */
    public ComponentManager createComponentManager( Object object ) throws Exception
    {
        return new WrapperComponentManager( m_serviceManager );
    }

    /**
     * Create a Service Manager
     * @param object For this object
     * @return The service manager
     * @throws Exception If a problem
     */
    public ServiceManager createServiceManager( final Object object )
        throws Exception
    {
        return m_serviceManager;
    }

    /**
     * Create some Configuration
     *
     * @param object For this object
     * @return The configuration
     * @throws Exception If a problem
     */
    public Configuration createConfiguration( final Object object )
        throws Exception
    {
        return m_configuration;
    }

    /**
     * Create Some parameters
     * @param object For this object
     * @return The parameters
     * @throws Exception If a problem
     */
    public Parameters createParameters( final Object object )
        throws Exception
    {
        final Configuration configuration = createConfiguration( object );
        return Parameters.fromConfiguration( configuration );
    }
}
