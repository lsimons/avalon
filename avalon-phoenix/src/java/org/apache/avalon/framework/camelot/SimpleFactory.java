/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */
package org.apache.avalon.framework.camelot;

import java.net.URL;
import org.apache.avalon.framework.container.Locator;

/**
 * This is the component that creates the components.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SimpleFactory
    implements Factory
{
    private final ClassLoader    m_classLoader;

    public SimpleFactory( final ClassLoader classLoader )
    {
        m_classLoader = classLoader;
    }

    /**
     * Create a component whos position is indicated by locator.
     *
     * @param locator the locator indicating the component location
     * @return the component
     * @exception FactoryException if an error occurs
     */
    public Object create( final Locator locator )
        throws FactoryException
    {
        try
        {
            final Class clazz = m_classLoader.loadClass( locator.getName() );
            return clazz.newInstance();
        }
        catch( final Exception e )
        {
            throw new FactoryException( "Unable to create " + locator.getName() +
                                        " from " + locator.getLocation(), e );
        }
    }

    public Object create( final Locator locator, final Class clazz )
        throws FactoryException
    {
        final Object object = create( locator );

        if( !clazz.isInstance( object ) )
        {
            throw new FactoryException( "Created object of type " + object.getClass().getName() +
                                        " not compatable with type " + clazz.getName() );
        }

        return object;
    }
}
