/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Fixed length cache with a LRU replacement policy.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class LRUCache
    extends DefaultCache
{
    public LRUCache( final int capacity )
    {
        this( new MemoryCacheStore( capacity ) );
    }

    public LRUCache( final CacheStore store )
    {
        super( new LRUCachePolicy(), store );
    }
}
