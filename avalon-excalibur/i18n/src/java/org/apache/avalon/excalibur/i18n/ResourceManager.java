/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.i18n;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Manager for resources.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class ResourceManager
{
    /**
     * Permission needed to clear complete cache.
     */
    private static final RuntimePermission CLEAR_CACHE_PERMISSION =
        new RuntimePermission( "i18n.clearCompleteCache" );
    private static final HashMap c_resources = new HashMap();

    /**
     * Retrieve resource with specified basename.
     *
     * @param baseName the basename
     * @return the Resources
     */
    public static final Resources getBaseResources( final String baseName )
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
    public synchronized static final Resources getBaseResources( final String baseName,
                                                                 final ClassLoader classLoader )
    {
        Resources resources = getCachedResource( baseName );
        if( null == resources )
        {
            resources = new Resources( baseName, classLoader );
            putCachedResource( baseName, resources );
        }

        return resources;
    }

    /**
     * Clear the cache of all resources currently loaded into the
     * system. This method is useful if you need to dump the complete
     * cache and because part of the application is reloading and
     * thus the resources may need to be reloaded.
     *
     * <p>Note that the caller must have been granted the
     * "i18n.clearCompleteCache" {@link RuntimePermission} or
     * else a security exception will be thrown.</p>
     *
     * @throws SecurityException if the caller does not have
     *                           permission to clear cache
     */
    public synchronized static final void clearResourceCache()
        throws SecurityException
    {
        final SecurityManager sm = System.getSecurityManager();
        if( null != sm )
        {
            sm.checkPermission( CLEAR_CACHE_PERMISSION );
        }

        c_resources.clear();
    }

    /**
     * Cache specified resource in weak reference.
     *
     * @param baseName the resource key
     * @param resources the resources object
     */
    private synchronized static final void putCachedResource( final String baseName,
                                                              final Resources resources )
    {
        c_resources.put( baseName,
                         new WeakReference( resources ) );
    }

    /**
     * Retrieve cached resource.
     *
     * @param baseName the resource key
     * @return resources the resources object
     */
    private synchronized static final Resources getCachedResource( final String baseName )
    {
        final WeakReference weakReference =
            (WeakReference)c_resources.get( baseName );
        if( null == weakReference )
        {
            return null;
        }
        else
        {
            return (Resources)weakReference.get();
        }
    }

    /**
     * Retrieve resource for specified name.
     * The basename is determined by name postfixed with ".Resources".
     *
     * @param name the name to use when looking up resources
     * @return the Resources
     */
    public static final Resources getResources( final String name )
    {
        return getBaseResources( name + ".Resources" );
    }

    /**
     * Retrieve resource for specified Classes package.
     * The basename is determined by name of classes package
     * postfixed with ".Resources".
     *
     * @param clazz the Class
     * @return the Resources
     */
    public static final Resources getPackageResources( final Class clazz )
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
    public static final Resources getClassResources( final Class clazz )
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
    public static final String getPackageResourcesBaseName( final Class clazz )
    {
        final Package pkg = clazz.getPackage();

        String baseName;
        if( null == pkg )
        {
            final String name = clazz.getName();
            if( -1 == name.lastIndexOf( "." ) )
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
    public static final String getClassResourcesBaseName( final Class clazz )
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
