/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.impl;

import org.apache.avalon.excalibur.cache.CacheStore;
import org.apache.avalon.excalibur.cache.DefaultCache;
import org.apache.avalon.excalibur.cache.policy.LRUPolicy;
import org.apache.avalon.excalibur.cache.store.MemoryStore;

/**
 * LRUCache.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @version $Revision: 1.1 $ $Date: 2002/04/03 10:36:02 $
 */
public class LRUCache
    extends DefaultCache
{
    public LRUCache( final int capacity )
    {
        this( new MemoryStore( capacity ) );
    }

    public LRUCache( final CacheStore store )
    {
        super( new LRUPolicy(), store );
    }
}
