/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities.classloader;

import java.security.Policy;
import org.apache.avalon.Initializable;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.context.Context;
import org.apache.avalon.context.Contextualizable;
import org.apache.avalon.context.DefaultContext;
import org.apache.phoenix.engine.facilities.ClassLoaderManager;

/**
 * This facility manages the ClassLoader for an application instance.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultClassLoaderManager
    implements ClassLoaderManager, Contextualizable, Composable, Initializable
{
    private SarClassLoader   m_classLoader = new SarClassLoader();

    public void contextualize( final Context context )
    {
        m_classLoader.contextualize( context );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_classLoader.compose( componentManager );
    }

    public void init()
        throws Exception
    {
        m_classLoader.init();
    }

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }
}
