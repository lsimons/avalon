/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.factory;

import java.util.Map;
import java.util.WeakHashMap;
import org.apache.avalon.framework.info.ComponentInfo;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.tools.infobuilder.InfoBuilder;

/**
 * The default implementation of {@link ComponentFactory}
 * that simply creates components from a {@link java.lang.ClassLoader}.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public class DefaultComponentFactory
    extends AbstractLogEnabled
    implements ComponentFactory
{
    /**
     * Cache of ComponentInfo objects.
     */
    private final Map m_infos = new WeakHashMap();

    /**
     * The utility class that is used when building info
     * objects for Components.
     */
    private final InfoBuilder m_infoBuilder = new InfoBuilder();

    /**
     * The classloader from which all resources are loaded.
     */
    private final ClassLoader m_classLoader;

    /**
     * Create a Factory that loads from specified ClassLoader.
     *
     * @param classLoader the classLoader to use in factory, must not be null
     */
    public DefaultComponentFactory( final ClassLoader classLoader )
    {
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        m_classLoader = classLoader;
    }

    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_infoBuilder, "info" );
    }

    /**
     * Create a component by creating info for class
     * with specified name and loaded from factorys ClassLoader.
     *
     * @see ComponentFactory#createBundle
     */
    public ComponentBundle createBundle( final String implementationKey )
        throws Exception
    {
        ComponentBundle bundle = (ComponentBundle)m_infos.get( implementationKey );
        if( null == bundle )
        {
            bundle = newBundle( implementationKey );
            m_infos.put( implementationKey, bundle );
        }

        return bundle;
    }

    /**
     * Create a component by creating instance of class
     * with specified name and loaded from factorys ClassLoader.
     *
     * @see ComponentFactory#createComponent
     */
    public Object createComponent( final String implementationKey )
        throws Exception
    {
        final Class clazz = getClassLoader().loadClass( implementationKey );
        return clazz.newInstance();
    }



    /**
     * Create a bundle for specified key.
     * Note that this does not cache bundle in any way.
     *
     * @param implementationKey the implementationKey
     * @return the new ComponentBundle
     * @throws Exception if unable to create bundle
     */
    protected ComponentBundle newBundle( final String implementationKey )
        throws Exception
    {
        ComponentBundle bundle;
        final ComponentInfo info = createComponentInfo( implementationKey );
        bundle = new DefaultComponentBundle( info, getClassLoader() );
        return bundle;
    }

    /**
     * Create a {@link ComponentInfo} for component with specified implementationKey.
     *
     * @param implementationKey the implementationKey
     * @return the created {@link ComponentInfo}
     * @throws Exception if unabel to create componentInfo
     */
    protected ComponentInfo createComponentInfo( final String implementationKey )
        throws Exception
    {
        return m_infoBuilder.buildComponentInfo( implementationKey, getClassLoader() );
    }

    /**
     * Retrieve ClassLoader associated with ComponentFactory.
     *
     * @return
     */
    protected ClassLoader getClassLoader()
    {
        return m_classLoader;
    }
}
