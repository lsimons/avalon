/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache;

/**
 * LRUCache.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @version $Revision: 1.8 $ $Date: 2002/05/01 04:01:32 $
 * @deprecated Use org.apache.excalibur.cache.impl.LRUCache
 */
public class LRUCache
    extends org.apache.excalibur.cache.impl.LRUCache
{
    public LRUCache( final int capacity )
    {
        super( capacity );
    }

    public LRUCache( final CacheStore store )
    {
        super( store );
    }
}
