/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.proxy;

/**
 * Utility class to help load dynamically generated classes.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ProxyClassLoader
    extends ClassLoader
{
    public ProxyClassLoader( final ClassLoader parent )
    {
        super( parent );
    }

    public Class loadClass( final String name,
                            final boolean resolve,
                            final byte[] classData )
        throws ClassNotFoundException
    {
        final Class result =
            defineClass( name, classData, 0, classData.length );

        if( resolve )
        {
            resolveClass( result );
        }

        return result;
    }
}

