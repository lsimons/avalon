/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.factory;

import java.io.InputStream;
import org.apache.avalon.framework.info.ComponentInfo;

/**
 *
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public class DefaultComponentBundle
    implements ComponentBundle
{
    private final ComponentInfo m_componentInfo;
    private final ClassLoader m_classLoader;

    public DefaultComponentBundle( final ComponentInfo componentInfo,
                                   final ClassLoader classLoader )
    {
        if( null == componentInfo )
        {
            throw new NullPointerException( "componentInfo" );
        }
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        m_componentInfo = componentInfo;
        m_classLoader = classLoader;
    }

    public ComponentInfo getComponentInfo()
    {
        return m_componentInfo;
    }

    public InputStream getResourceAsStream( String resource )
    {
        return m_classLoader.getResourceAsStream( resource );
    }
}
