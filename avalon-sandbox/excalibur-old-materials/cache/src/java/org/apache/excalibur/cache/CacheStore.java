/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

/**
 * Store cached objects.
 * Key and value are must not be <code>null</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface CacheStore
{
    /**
     * Return capacity of store.
     *
     * @return capacity of store
     */
    int capacity();

    /**
     * Return size of store.
     *
     * @return the number of key-value mappings in this store
     */
    int size();

    /**
     *
     * @return
     */
    boolean isFull();

    /**
     * @param key
     * @param value
     */
    Object put( Object key, Object value );

    /**
     *
     * @param key
     * @return
     */
    Object get( Object key );

    /**
     *
     * @param key
     * @return
     */
    Object remove( Object key );

    /**
     *
     * @param key
     * @return
     */
    boolean containsKey( Object key );

    /**
     * Return the array containing all key.
     *
     * @return the array containing all key
     */
    Object[] keys();
}
