/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.test;

import org.apache.avalon.excalibur.cache.Cache;
import org.apache.avalon.excalibur.cache.TimeMapLRUCache;

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
        m_cache = new TimeMapLRUCache( 10 );
    }
}
