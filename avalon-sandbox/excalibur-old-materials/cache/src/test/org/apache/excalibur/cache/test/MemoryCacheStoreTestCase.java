/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.test;

import org.apache.avalon.excalibur.cache.MemoryCacheStore;
import junit.framework.TestCase;

/**
 * TestCase for MemoryCacheStore.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class MemoryCacheStoreTestCase
    extends AbstractCacheStoreTestCase
{
    public MemoryCacheStoreTestCase( final String name )
    {
        super( name );
    }

    protected void setUp()
    {
        m_store = new MemoryCacheStore( 10 );
    }
}
