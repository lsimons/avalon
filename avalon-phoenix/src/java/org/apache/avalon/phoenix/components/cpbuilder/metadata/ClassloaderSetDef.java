/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.cpbuilder.metadata;

/**
 * This class defines a set of ClassLoaders and
 * the default ClassLoader to use.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/01 00:51:37 $
 */
public class ClassloaderSetDef
{
    /**
     * The name of the current classloader.
     * This may be used by other ClassLoader definitions to refer
     * to this ClassLoader.
     */
    private final String m_default;

    /**
     * The classloaders defined in set.
     */
    private final ClassLoaderDef[] m_classLoaders;

    /**
     * Construct set with specified set and ClassLoaders.
     *
     * @param aDefault the name of default ClassLoader
     * @param classLoaders the ClassLoaders in set
     */
    public ClassloaderSetDef( final String aDefault,
                              final ClassLoaderDef[] classLoaders )
    {
        if( null == aDefault )
        {
            throw new NullPointerException( "aDefault" );
        }
        if( null == classLoaders )
        {
            throw new NullPointerException( "classLoaders" );
        }

        m_default = aDefault;
        m_classLoaders = classLoaders;
    }

    /**
     * Return the default ClassLoader name.
     *
     * @return the default ClassLoader name.
     * @see #m_default
     */
    public String getDefault()
    {
        return m_default;
    }

    /**
     * Return the classloaders in set.
     *
     * @return the classloaders in set.
     * @see #m_classLoaders
     */
    public ClassLoaderDef[] getClassLoaders()
    {
        return m_classLoaders;
    }

    /**
     * Return the classloader with specified name.
     *
     * @return the classloader with specified name
     */
    public ClassLoaderDef getClassLoader( final String name )
    {
        for( int i = 0; i < m_classLoaders.length; i++ )
        {
            final ClassLoaderDef classLoader = m_classLoaders[ i ];
            if( classLoader.getName().equals( name ) )
            {
                return classLoader;
            }
        }
        return null;
    }
}
