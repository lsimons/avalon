/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool;

import java.lang.reflect.Constructor;

/**
 * This is the default for factory that is used to create objects for Pool.
 *
 * It creates objects via reflection and constructor.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.0
 */
public class DefaultObjectFactory
    implements ObjectFactory
{
    protected Constructor m_constructor;
    protected Object[] m_arguements;

    public DefaultObjectFactory( final Constructor constructor, final Object[] arguements )
    {
        m_arguements = arguements;
        m_constructor = constructor;
    }

    public DefaultObjectFactory( final Class clazz,
                                 final Class[] arguementClasses,
                                 final Object[] arguements )
        throws NoSuchMethodException
    {
        this( clazz.getConstructor( arguementClasses ), arguements );
    }

    public DefaultObjectFactory( final Class clazz )
        throws NoSuchMethodException
    {
        this( clazz, null, null );
    }

    public Class getCreatedClass()
    {
        return m_constructor.getDeclaringClass();
    }

    public Object newInstance()
    {
        try
        {
            return (Poolable)m_constructor.newInstance( m_arguements );
        }
        catch( final Exception e )
        {
            throw new Error( "Failed to instantiate the class " +
                             m_constructor.getDeclaringClass().getName() + " due to " + e );
        }
    }

    public void decommission( final Object object )
    {
        // Nothing to do
    }
}
