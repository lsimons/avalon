/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache;

/**
 * TimeMapLRUCache.
 *
 * @author <a href="alag@users.sourceforge.net">Alexis Agahi</a>
 * @deprecated Use org.apache.excalibur.cache.impl.TimeMapLRUCache
 */
public class TimeMapLRUCache
    extends org.apache.excalibur.cache.impl.TimeMapLRUCache
{
    public TimeMapLRUCache( final int capacity )
    {
        super( capacity );
    }

    public TimeMapLRUCache( final CacheStore store )
    {
        super( store );
    }
}
