/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

/**
 * Cache replacement policy.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface ReplacementPolicy
{
    void add( Object key );

    void hit( Object key );

    void remove( Object key );

    Object selectVictim();
}
