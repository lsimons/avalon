/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.i18n;

import java.util.HashMap;

/**
 * Manager for resources.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class ResourceManager
{
    private final static HashMap  c_resources   = new HashMap();

    /**
     * Retrieve resource with specified basename.
     *
     * @param baseName the basename
     * @return the Resources
     */
    public final static Resources getBaseResources( final String baseName )
    {
        return getBaseResources( baseName, null );
    }

    /**
     * Retrieve resource with specified basename.
     *
     * @param baseName the basename
     * @param classLoader the classLoader to load resources from
     * @return the Resources
     */
    public final static Resources getBaseResources( final String baseName,
                                                    final ClassLoader classLoader )
    {
        //TODO: Make these weak references????
        Resources packet = (Resources)c_resources.get( baseName );

        if( null == packet )
        {
            packet = new Resources( baseName, classLoader );
            c_resources.put( baseName, packet );
        }

        return packet;
    }

    /**
     * Retrieve resource for specified name.
     * The basename is determined by name postfixed with ".Resources".
     *
     * @param clazz the Class
     * @return the Resources
     */
    public final static Resources getResources( final String resource )
    {
        return getBaseResources( resource + ".Resources" );
    }

    /**
     * Retrieve resource for specified Classes package.
     * The basename is determined by name of classes package
     * postfixed with ".Resources".
     *
     * @param clazz the Class
     * @return the Resources
     */
    public final static Resources getPackageResources( final Class clazz )
    {
        return getBaseResources( getPackageResourcesBaseName( clazz ), clazz.getClassLoader() );
    }

    /**
     * Retrieve resource for specified Class.
     * The basename is determined by name of Class
     * postfixed with "Resources".
     *
     * @param clazz the Class
     * @return the Resources
     */
    public final static Resources getClassResources( final Class clazz )
    {
        return getBaseResources( getClassResourcesBaseName( clazz ), clazz.getClassLoader() );
    }

    /**
     * Retrieve resource basename for specified Classes package.
     * The basename is determined by name of classes package
     * postfixed with ".Resources".
     *
     * @param clazz the Class
     * @return the resource basename
     */
    public final static String getPackageResourcesBaseName( final Class clazz )
    {
        final Package pkg = clazz.getPackage();

        String baseName;
        if ( null == pkg )
        {
            final String name = clazz.getName();
            if ( -1 == name.lastIndexOf( "." ) )
            {
                baseName = "Resources";
            }
            else
            {
                baseName = name.substring( 0, name.lastIndexOf( "." ) ) + ".Resources";
            }
        }
        else
        {
            baseName = pkg.getName() + ".Resources";
        }

        return baseName;
    }

    /**
     * Retrieve resource basename for specified Class.
     * The basename is determined by name of Class
     * postfixed with "Resources".
     *
     * @param clazz the Class
     * @return the resource basename
     */
    public final static String getClassResourcesBaseName( final Class clazz )
    {
        return clazz.getName() + "Resources";
    }

    /**
     * Private Constructor to block instantiation.
     */
    private ResourceManager()
    {
    }
}
