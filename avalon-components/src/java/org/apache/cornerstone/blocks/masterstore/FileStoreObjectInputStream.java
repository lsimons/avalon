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

class FileStoreObjectInputStream extends ObjectInputStream
{
    private ClassLoader classLoader;
    public FileStoreObjectInputStream(ClassLoader classLoader, InputStream in) throws IOException, StreamCorruptedException
    {
        super(in);
        this.classLoader = classLoader;
    }
    protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException
    {
        Class cl = Class.forName(v.getName(), false, classLoader);
        if (cl !=null)
        {
            return cl; // the classloader knows of the class
        }
        else
        {
            return super.resolveClass(v); // classloader knows not of class, let the super classloader do it
        }
    }
}