/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache;

/**
 * Cache replacement policy.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface ReplacementPolicy
{
    /**
     * Call after add.
     *
     * @param key the key of added item
     */
    void add( Object key );

    /**
     * Call after hit.
     *
     * @param key the key of hitted item
     */
    void hit( Object key );

    /**
     * Call after remove.
     *
     * @param key the key of removed item
     */
    void remove( Object key );

    /**
     * Return the key of item to remove.
     *
     * @return the key of item to remove
     */
    Object selectVictim();
}
