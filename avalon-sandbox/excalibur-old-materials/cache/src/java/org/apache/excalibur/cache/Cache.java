/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This is a cache that caches objects for reuse.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface Cache
    extends ThreadSafe, Component
{
    /**
     * Set value validator.
     *
     * @param validator object validator
     */
    void setValidator( CacheValidator validator );

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
     * @return the number of key-value mappings in this cache.
     */
    int size();

    /**
     * Puts a new item in the cache. If the cache is full, remove the selected item.
     * @param key
     * @param value
     * @return old value
     */
    Object put( Object key, Object value );

    /**
     * Get an item from the cache.
     *
     * @param key key to lookup the item
     * @return the matching object in the cache
     */
    Object get( Object key );

    /**
     * Removes an item from the cache.
     *
     * @param key key to remove
     * @return the value removed
     */
    Object remove( Object key );

    /**
     * @param key
     * @return
     */
    boolean containsKey( Object key );

    /**
     * Clear cache.
     */
    void clear();
}
