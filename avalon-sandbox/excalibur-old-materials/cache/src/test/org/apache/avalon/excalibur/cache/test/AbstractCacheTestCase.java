/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.test;

import junit.framework.TestCase;
import org.apache.avalon.excalibur.cache.Cache;

/**
 * TestCase for Cache.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class AbstractCacheTestCase
    extends TestCase
{
    protected Cache m_cache;

    public AbstractCacheTestCase( final String name )
    {
        super( name );
    }
}
