/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit.test;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

public class TestBlock
        implements Serviceable, Configurable, Initializable, Contextualizable, LogEnabled
{

    public ServiceManager m_serviceManager;
    public boolean m_initialized;
    public Context m_context;
    public Logger m_logger;
    public Configuration m_configuration;

    public void service( final ServiceManager serviceManager ) throws ServiceException
    {
        m_logger.info("service");
        m_serviceManager = serviceManager;
    }

    public void initialize() throws Exception
    {
        m_logger.warn("initialize");
        m_initialized = true;
    }

    public void contextualize(Context context) throws ContextException
    {
        m_logger.error("contextualize");
        m_context = context;
    }

    public void enableLogging(Logger logger)
    {
        m_logger = logger;
    }

    public void configure(Configuration configuration) throws ConfigurationException
    {
        m_logger.fatalError("configure");
        m_configuration = configuration;
    }


}

