/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.store.test;

import org.apache.avalon.excalibur.cache.CacheStore;
import junit.framework.TestCase;

/**
 * TestCase for CacheStore.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class AbstractCacheStoreTestCase
    extends TestCase
{
    protected CacheStore m_store;

    public AbstractCacheStoreTestCase( final String name )
    {
        super( name );
    }

    public void testNullValue()
    {
        m_store.put( "KEY", null );
        assertTrue( m_store.containsKey( "KEY" ) );
        assertEquals( 1, m_store.size() );
    }
}
