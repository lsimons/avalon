/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache.test;

import org.apache.avalon.excalibur.cache.Cache;
import org.apache.avalon.excalibur.cache.LRUCache;
import org.apache.avalon.excalibur.cache.TimeoutValidator;
import junit.framework.TestCase;

/**
 * JUnit TestCase for TimeoutValidator.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class TimeoutValidatorTestCase
    extends TestCase
{
    public TimeoutValidatorTestCase( final String name )
    {
        super( name );
    }

    public void testNotExpired()
        throws InterruptedException
    {
        final TimeoutValidator validator = new TimeoutValidator( 1000 );

        final Cache cache = new LRUCache( 10 );
        cache.addListener( validator );
        cache.setValidator( validator );

        cache.put( "K1", "V1" );

        Thread.sleep( 100 );

        assertTrue( cache.containsKey( "K1" ) );
    }

    public void testExpired()
        throws InterruptedException
    {
        final TimeoutValidator validator = new TimeoutValidator( 1000 );

        final Cache cache = new LRUCache( 10 );
        cache.addListener( validator );
        cache.setValidator( validator );

        cache.put( "K1", "V1" );

        Thread.sleep( 2000 );

        assertTrue( ! cache.containsKey( "K1" ) );
    }
}
