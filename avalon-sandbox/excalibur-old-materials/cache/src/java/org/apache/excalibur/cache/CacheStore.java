/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.Iterator;

/**
 * Store cached objects.
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
     * @return the number of key-value mappings in this store.
     */
    int size();

    boolean isFull();

    Object put( Object key, Object value );

    Object get( Object key );

    Object remove( Object key );

    boolean containsKey( Object key );

   /**
     * Return the array containing all key.
     */
    Object[] keys();
}
