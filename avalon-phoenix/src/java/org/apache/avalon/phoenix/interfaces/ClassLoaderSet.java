/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.util.Map;

/**
 * A dodgy class to hold all apps classloaders.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/11/01 08:23:31 $
 * @todo Remove me when kernel is reworked!
 */
public final class ClassLoaderSet
{
    private final ClassLoader m_defaultClassLoader;
    private final Map m_classLoaders;

    public ClassLoaderSet( final ClassLoader defaultClassLoader,
                           final Map classLoaders )
    {
        if( null == defaultClassLoader )
        {
            throw new NullPointerException( "defaultClassLoader" );
        }
        if( null == classLoaders )
        {
            throw new NullPointerException( "classLoaders" );
        }

        m_defaultClassLoader = defaultClassLoader;
        m_classLoaders = classLoaders;
    }

    public ClassLoader getDefaultClassLoader()
    {
        return m_defaultClassLoader;
    }

    public Map getClassLoaders()
    {
        return m_classLoaders;
    }
}
