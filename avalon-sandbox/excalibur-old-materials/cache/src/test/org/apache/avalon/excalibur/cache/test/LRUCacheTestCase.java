/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache.test;

import junit.framework.TestCase;
import org.apache.avalon.excalibur.cache.Cache;
import org.apache.avalon.excalibur.cache.LRUCache;

/**
 * JUnit TestCase for LRUCache.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class LRUCacheTestCase
    extends TestCase
{
    public LRUCacheTestCase( final String name )
    {
        super( name );
    }

    private static final String KEY1 = "key 1";
    private static final String VALUE1 = "value 1";
    private static final String VALUE1_1 = "value 1-1";
    private static final String KEY2 = "key 2";
    private static final String VALUE2 = "value 2";
    private static final String KEY3 = "key 3";
    private static final String VALUE3 = "value 3";

    public void testPutGet()
    {
        final Cache cache = new LRUCache( 3 );

        assertTrue( ! cache.containsKey( KEY1 ) );
        assertNull( cache.put( KEY1, VALUE1 ) );
        assertTrue( cache.containsKey( KEY1 ) );
        assertEquals( VALUE1, cache.get( KEY1 ) );

        assertTrue( ! cache.containsKey( KEY2 ) );
        assertNull( cache.put( KEY2, VALUE2 ) );
        assertTrue( cache.containsKey( KEY2 ) );
        assertEquals( VALUE2, cache.get( KEY2 ) );

        assertTrue( ! cache.containsKey( KEY3 ) );
        assertNull( cache.put( KEY3, VALUE3 ) );
        assertTrue( cache.containsKey( KEY3 ) );
        assertEquals( VALUE3, cache.get( KEY3 ) );

        assertTrue( cache.containsKey( KEY1 ) );
        assertEquals( VALUE1, cache.put( KEY1, VALUE1_1 ) );
        assertTrue( cache.containsKey( KEY1 ) );
        assertEquals( VALUE1_1, cache.get( KEY1 ) );

        cache.clear();
        assertEquals( 0, cache.size() );
        assertTrue( ! cache.containsKey( KEY1 ) );
        assertTrue( ! cache.containsKey( KEY2 ) );
        assertTrue( ! cache.containsKey( KEY3 ) );
    }

    public void testRemove()
    {
        final Cache cache = new LRUCache( 3 );

        assertTrue( ! cache.containsKey( KEY1 ) );
        assertNull( cache.put( KEY1, VALUE1 ) );
        assertTrue( cache.containsKey( KEY1 ) );
        assertEquals( VALUE1, cache.get( KEY1 ) );

        assertTrue( ! cache.containsKey( KEY2 ) );
        assertNull( cache.put( KEY2, VALUE2 ) );
        assertTrue( cache.containsKey( KEY2 ) );
        assertEquals( VALUE2, cache.get( KEY2 ) );

        assertTrue( ! cache.containsKey( KEY3 ) );
        assertNull( cache.put( KEY3, VALUE3 ) );
        assertTrue( cache.containsKey( KEY3 ) );
        assertEquals( VALUE3, cache.get( KEY3 ) );

        assertEquals( VALUE1, cache.remove( KEY1 ) );
        assertTrue( ! cache.containsKey( KEY1 ) );
        assertTrue( cache.containsKey( KEY2 ) );
        assertTrue( cache.containsKey( KEY3 ) );

        assertEquals( VALUE2, cache.remove( KEY2 ) );
        assertTrue( ! cache.containsKey( KEY1 ) );
        assertTrue( ! cache.containsKey( KEY2 ) );
        assertTrue( cache.containsKey( KEY3 ) );

        assertEquals( VALUE3, cache.remove( KEY3 ) );
        assertTrue( ! cache.containsKey( KEY1 ) );
        assertTrue( ! cache.containsKey( KEY2 ) );
        assertTrue( ! cache.containsKey( KEY3 ) );

        cache.clear();
        assertEquals( 0, cache.size() );
        assertTrue( ! cache.containsKey( KEY1 ) );
        assertTrue( ! cache.containsKey( KEY2 ) );
        assertTrue( ! cache.containsKey( KEY3 ) );
    }
}
