/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

/**
 * A special ObjectInputStream to handle highly transient classes hosted
 * by Avalon components that are juggling many classloaders.
 *
 * @author <a href="mailto:paul_hammant@yahoo.com">Paul Hammant</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 */
public class ClassLoaderObjectInputStream
    extends ObjectInputStream
{
    private ClassLoader m_classLoader;

    public ClassLoaderObjectInputStream( final ClassLoader classLoader,
                                         final InputStream inputStream )
        throws IOException, StreamCorruptedException
    {
        super( inputStream );
        m_classLoader = classLoader;
    }

    protected Class resolveClass( final ObjectStreamClass objectStreamClass )
        throws IOException, ClassNotFoundException
    {
        final Class clazz =
            Class.forName( objectStreamClass.getName(), false, m_classLoader );

        if( null != clazz )
        {
            return clazz; // the classloader knows of the class
        }
        else
        {
            // classloader knows not of class, let the super classloader do it
            return super.resolveClass( objectStreamClass );
        }
    }
}
