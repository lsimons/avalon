/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.metadata;

import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.FeatureDescriptor;

/**
 * The {@link DependencyMetaData} is the mapping of a component as a dependency
 * of another component. Each component declares dependencies (via
 * {@link org.apache.avalon.phoenix.framework.info.ComponentInfo})
 * and for each dependency there must be a coressponding DependencyMetaData which
 * has a matching key. The name value in {@link DependencyMetaData} object must refer
 * to another Component that implements a service as specified in DependencyInfo.
 *
 * <p>Note that it is invalid to have circular dependencies.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/01 03:39:46 $
 */
public final class DependencyMetaData
    extends FeatureDescriptor
{
    /**
     * The key that the client component will use to access a dependency.
     */
    private final String m_key;

    /**
     * the name of the component profile that represents a component
     * type that is capable of fullfilling the dependency.
     */
    private final String m_providerName;

    /**
     * The key that is used when the dependency is a map dependency.
     * Usually this defaults to the same value as the key.
     */
    private final String m_alias;

    /**
     * Create Association between key and provider.
     *
     * @param key the key the client uses to access component
     * @param providerName the name of {@link ComponentMetaData}
     *   that is associated as a service provider
     */
    public DependencyMetaData( final String key,
                               final String providerName,
                               final String alias,
                               final Attribute[] attributes )
    {
        super( attributes );

        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == providerName )
        {
            throw new NullPointerException( "providerName" );
        }
        if( null == alias )
        {
            throw new NullPointerException( "alias" );
        }
        m_key = key;
        m_providerName = providerName;
        m_alias = alias;
    }

    /**
     * Return the key that will be used by a component instance to access a
     * dependent service.
     *
     * @return the name that the client component will use to access dependency.
     * @see org.apache.avalon.framework.service.ServiceManager#lookup( String )
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the name of a {@link ComponentMetaData} instance that will used to
     * fulfill the dependency.
     *
     * @return the name of the Component that will provide the dependency.
     */
    public String getProviderName()
    {
        return m_providerName;
    }

    /**
     * The key under which the dependency is placed in map if dependency is
     * a Map dependency.
     *
     * @return the key under which the dependency is placed in map if dependency is
     *         a Map dependency.
     */
    public String getAlias()
    {
        return m_alias;
    }
}
