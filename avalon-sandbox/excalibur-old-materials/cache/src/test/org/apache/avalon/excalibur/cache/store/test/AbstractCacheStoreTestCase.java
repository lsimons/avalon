/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.store.test;

import junit.framework.TestCase;
import org.apache.avalon.excalibur.cache.CacheStore;

/**
 * TestCase for CacheStore.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class AbstractCacheStoreTestCase
    extends TestCase
{
    protected static final int STORE_SIZE = 10;

    protected CacheStore m_store;

    public AbstractCacheStoreTestCase( final String name )
    {
        super( name );
    }

    public void testIsFull()
    {
        for ( int i = 0; i < STORE_SIZE - 1; i++ )
        {
            m_store.put( "KEY" + i , "VALUE" + i );
            assertTrue( ! m_store.isFull() );
        }
        m_store.put( "KEY" + STORE_SIZE, "VALUE" + STORE_SIZE );
        assertTrue( m_store.isFull() );
    }
}
