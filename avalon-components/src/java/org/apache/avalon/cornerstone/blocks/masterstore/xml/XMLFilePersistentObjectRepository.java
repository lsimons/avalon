/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.masterstore.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.apache.avalon.cornerstone.blocks.masterstore.AbstractFileRepository;

/**
 * This is a simple implementation of persistent object store using
 * XML serialization from JDK 1.4 to a file system.
 *
 * @author <a href="mailto:paul_hammant@yahoo.com">Paul Hammant</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public class XMLFilePersistentObjectRepository
    extends AbstractFileRepository
    implements ObjectRepository
{
    protected String getExtensionDecorator()
    {
        return ".FileObjectStore";
    }

    /**
     * Get the object associated to the given unique key.
     */
    public synchronized Object get( final String key )
    {
        try
        {
            final InputStream inputStream = getInputStream( key );

            try
            {
                final XMLDecoder decoder = new  XMLDecoder( inputStream );
                final Object object = decoder.readObject();
                if( DEBUG )
                {
                    getLogger().debug( "returning object " + object + " for key " + key );
                }
                return object;
            }
            finally
            {
                inputStream.close();
            }
        }
        catch( final Exception e )
        {
            throw new RuntimeException( "Exception caught while retrieving an object: " + e );
        }
    }

    public synchronized Object get( final String key , final ClassLoader classLoader )
    {
        try
        {
            final InputStream inputStream = getInputStream( key );
            final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            try
            {
                final XMLDecoder decoder = new XMLDecoder( inputStream );
                final Object object = decoder.readObject();
                if( DEBUG )
                {
                    getLogger().debug( "returning object " + object + " for key " + key );
                }
                return object;
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(oldCL);
                inputStream.close();
            }
        }
        catch( final Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( "Exception caught while retrieving an object: " + e );
        }

    }

    /**
     * Store the given object and associates it to the given key
     */
    public synchronized void put( final String key, final Object value )
    {
        try
        {
            final OutputStream outputStream = getOutputStream( key );

            try
            {
                //System.out.println("Putting key!:" + key + " " + value + " " + value.getClass().getName());
                final XMLEncoder encoder = new  XMLEncoder( outputStream );
                encoder.writeObject( value );
                encoder.flush();
                if( DEBUG ) getLogger().debug( "storing object " + value + " for key " + key );
            }
            finally
            {
                outputStream.close();
            }
        }
        catch( final Exception e )
        {
            throw new RuntimeException( "Exception caught while storing an object: " + e );
        }
    }
}
