/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.applications;

import org.apache.framework.component.ComponentManager;
import org.apache.framework.context.Context;
import org.apache.avalon.camelot.Entry;
import org.apache.framework.configuration.Configuration;
import org.apache.log.Logger;

/**
 * This is the structure describing each server application before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ServerApplicationEntry
    extends Entry
{
    protected Logger            m_logger;
    protected Context           m_context;
    protected ComponentManager  m_componentManager;
    protected Configuration     m_configuration;

    public Logger getLogger()
    {
        return m_logger;
    }

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    public Context getContext()
    {
        return m_context;
    }

    public void setContext( final Context context )
    {
        m_context = context;;
    }

    public ComponentManager getComponentManager()
    {
        return m_componentManager;
    }

    public void setComponentManager( final ComponentManager componentManager )
    {
        m_componentManager = componentManager;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    public void setConfiguration( final Configuration configuration )
    {
        m_configuration = configuration;
    }
}
