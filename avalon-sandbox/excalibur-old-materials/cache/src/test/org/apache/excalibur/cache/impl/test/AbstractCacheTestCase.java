/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache.impl.test;

import junit.framework.TestCase;
import org.apache.excalibur.cache.Cache;

/**
 * TestCase for Cache.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class AbstractCacheTestCase
    extends TestCase
{
    protected static final int STORE_SIZE = 10;

    protected Cache m_cache;

    public AbstractCacheTestCase( final String name )
    {
        super( name );
    }

    public void testPut()
    {
        for ( int i = 0; i < STORE_SIZE * 2; i++ )
        {
            m_cache.put( "KEY" + i, "VALUE" + i );
            if ( i < STORE_SIZE )
            {
                assertEquals( i + 1, m_cache.size() );
            }
            else
            {
                assertEquals( STORE_SIZE, m_cache.size() );
            }
        }
    }

    public void testPutNull()
    {
        m_cache.put( null, "VALUE" );
        assertEquals( 0, m_cache.size() );

        m_cache.put( "KEY", null );
        assertEquals( 0, m_cache.size() );

        m_cache.put( null, null );
        assertEquals( 0, m_cache.size() );
    }
}
