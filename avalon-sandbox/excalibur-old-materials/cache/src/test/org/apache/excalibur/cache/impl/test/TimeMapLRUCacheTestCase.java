/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache.impl.test;

import org.apache.excalibur.cache.Cache;
import org.apache.excalibur.cache.impl.TimeMapLRUCache;

/**
 * TestCase for TimeMapLRUCache

 * @author <a href="alag@users.sourceforge.net">Alexis Agahi</a>
 */
public class TimeMapLRUCacheTestCase
    extends AbstractCacheTestCase
{
    public TimeMapLRUCacheTestCase( final String name )
    {
        super( name );
    }

    protected void setUp()
    {
        m_cache = new TimeMapLRUCache( STORE_SIZE );
    }
}
