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
import org.apache.avalon.excalibur.cache.policy.TimeMapLRUPolicy;
import org.apache.avalon.excalibur.cache.store.MemoryStore;

/**
 * TimeMapLRUCache.
 *
 * @author <a href="alag@users.sourceforge.net">Alexis Agahi</a>
 * @version $Revision: 1.1 $ $Date: 2002/04/03 10:36:02 $
 */
public class TimeMapLRUCache
    extends DefaultCache
{
    public TimeMapLRUCache( final int capacity )
    {
        this( new MemoryStore( capacity ) );
    }

    public TimeMapLRUCache( final CacheStore store )
    {
        super( new TimeMapLRUPolicy(), store );
    }
}
