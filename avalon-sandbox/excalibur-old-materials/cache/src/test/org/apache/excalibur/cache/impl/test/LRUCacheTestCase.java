/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.impl.test;

import org.apache.avalon.excalibur.cache.LRUCache;

/**
 * TestCase for LRUCache.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class LRUCacheTestCase
    extends AbstractCacheTestCase
{
    public LRUCacheTestCase( final String name )
    {
        super( name );
    }

    protected void setUp()
    {
        m_cache = new LRUCache( STORE_SIZE );
    }
}
