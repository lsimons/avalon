/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.masterstore;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * A special ObjectInputStream to handle highly transient classes hosted
 * by Avalon components that are juggling many classloaders.
 *
 * @author <a href="mailto:paul_hammant@yahoo.com">Paul Hammant</a>
 */
class FileStoreObjectInputStream 
    extends ObjectInputStream
{
    private ClassLoader m_classLoader;

    public FileStoreObjectInputStream( final ClassLoader classLoader, 
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
