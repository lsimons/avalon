/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

import org.apache.avalon.framework.component.Component;

/**
 * This is a cache that caches objects for reuse.
 * Key and value are must not <code>null</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface Cache
    extends Component
{
    /**
     * Add listener.
     *
     * @param listener listener instance to add
     */
    void addListener( CacheListener listener );

    /**
     * Remove listener.
     *
     * @param listener listener instance to remove
     */
    void removeListener( CacheListener listener );

    /**
     * Return capacity of cache.
     *
     * @return capacity of cache
     */
    int capacity();

    /**
     * Return size of cache.
     *
     * @return the number of key-value mappings in this cache
     */
    int size();

    /**
     * Puts a new item in the cache. If the cache is full, remove the selected item.
     *
     * @param key key for the item
     * @param value item
     * @return old value. null if old value not exists
     */
    Object put( Object key, Object value );

    /**
     * Get an item from the cache.
     *
     * @param key key to lookup the item
     * @return the matching object in the cache. null if item not exists
     */
    Object get( Object key );

    /**
     * Removes an item from the cache.
     *
     * @param key key to remove
     * @return the value removed. null if old value not exists
     */
    Object remove( Object key );

    /**
     * Returns true if this cache contains a specified key.
     *
     * @param key key whose presence in this map is to be tested
     * @return true if matching item in the cache
     * @deprecated unnecessary
     */
    boolean containsKey( Object key );

    /**
     * Clear cache.
     */
    void clear();
}
