/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache.policy;

/**
 * LRU(Least Recently Used) replacement policy.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class LRUPolicy
    extends ListBasedPolicy
{
    public LRUPolicy()
    {
        super();
    }

    public void hit( final Object key )
    {
        m_keyList.remove( key );
        m_keyList.addFirst( key );
    }
}
