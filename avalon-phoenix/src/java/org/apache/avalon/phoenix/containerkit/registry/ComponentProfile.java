/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.registry;

import org.apache.avalon.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;

/**
 * The ComponentProfile defines a component as a conjunction
 * of the {@link ComponentInfo} and {@link ComponentMetaData}.
 * The {@link ComponentInfo} defines the type of the component
 * and the {@link ComponentMetaData} defines the data required to
 * construct a specific instance of the component.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:44 $
 */
public class ComponentProfile
{
    /**
     * The {@link ComponentInfo} that describes
     * the type of this component.
     */
    private final ComponentInfo m_info;

    /**
     * The {@link ComponentMetaData} that describes
     * this component.
     */
    private final ComponentMetaData m_metaData;

    /**
     * Creation of a new <code>ComponentProfile</code> instance.
     *
     * @param metaData the {@link ComponentMetaData} instance defining the component.
     */
    public ComponentProfile( final ComponentInfo info,
                             final ComponentMetaData metaData )
    {
        m_info = info;
        m_metaData = metaData;
    }

    /**
     * Returns the underlying {@link ComponentInfo} instance.
     *
     * @return the component info instance
     */
    public ComponentInfo getInfo()
    {
        return m_info;
    }

    /**
     * Returns the underlying {@link ComponentMetaData} instance.
     * @return the component meta data instance
     */
    public ComponentMetaData getMetaData()
    {
        return m_metaData;
    }
}
