/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.camelot;

import java.net.URL;
import java.net.URLClassLoader;
import org.apache.avalon.ExceptionUtil;

/**
 * Class used to load resources from a source.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultLoader
    implements Loader
{
    protected ClassLoader         m_classLoader;

    public DefaultLoader( final ClassLoader classLoader )
    {
        m_classLoader = classLoader;
    }

    public DefaultLoader( final URL location, final ClassLoader classLoader )
    {
        m_classLoader = new URLClassLoader( new URL[] { location }, classLoader );
    }

    public DefaultLoader( final URL location )
    {
        this( location, Thread.currentThread().getContextClassLoader() );
    }

    public DefaultLoader()
    {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        m_classLoader = new URLClassLoader( new URL[0], classLoader );
    }

    /**
     * Retrieve classloader associated with source.
     *
     * @return the ClassLoader
     */
    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    public Object load( final String classname, final Class clazz )
        throws FactoryException
    {
        final Object object = load( classname );

        if( !clazz.isInstance( object ) )
        {
            throw new FactoryException( "Created object of type " + object.getClass().getName() +
                                        " not compatable with type " + clazz.getName() );
        }

        return object;
    }

    /**
     * Load an object from source.
     *
     * @param classname the name of object
     * @return the Object
     * @exception FactoryException if an error occurs
     */
    public Object load( final String classname )
        throws FactoryException
    {
        try
        {
            return m_classLoader.loadClass( classname ).newInstance();
        }
        catch( final ClassNotFoundException cnfe )
        {
            throw new FactoryException( "Failed to locate class " + classname, cnfe );
        }
        catch( final InstantiationException ie )
        {
            throw new FactoryException( "Failed to instantiate class " + classname, ie );
        }
        catch( final IllegalAccessException iae )
        {
            throw new FactoryException( "Failed to instantiate class " + classname +
                                        " as it does not have a publicly accesable " +
                                        "default constructor", iae );
        }
        catch( final Throwable t )
        {
            throw new FactoryException( "Failed to get class " + classname +
                                        " due to " + ExceptionUtil.printStackTrace( t, 5, true ),
                                        t );
        }
    }
}
