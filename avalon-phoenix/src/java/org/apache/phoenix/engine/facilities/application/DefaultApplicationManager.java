/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities.application;

import org.apache.avalon.atlantis.SystemManager;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.context.Context;
import org.apache.avalon.context.ContextException;
import org.apache.avalon.context.Contextualizable;
import org.apache.phoenix.engine.facilities.ApplicationManager;

/**
 * This is default implementation of ApplicationManager.
 * It uses kernel Manager to export management.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultApplicationManager
    implements ApplicationManager, Contextualizable, Composable
{
    private SystemManager    m_systemManager;
    private String           m_name;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_name = (String)context.get( "name" );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_systemManager = (SystemManager)componentManager.
            lookup( "org.apache.avalon.atlantis.SystemManager" );
    }
}
