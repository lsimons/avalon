/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache.validator.test;

import junit.framework.TestCase;

import org.apache.excalibur.cache.Cache;
import org.apache.excalibur.cache.ValidatingCache;
import org.apache.excalibur.cache.impl.LRUCache;
import org.apache.excalibur.cache.validator.TimeoutValidator;

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

        final Cache cache =
            new ValidatingCache( new LRUCache( 10 ), validator );
        cache.addListener( validator );

        cache.put( "K1", "V1" );

        Thread.sleep( 100 );

        assertNotNull( cache.get( "K1" ) );
        assertNull( cache.get( "K2" ) );
    }

    public void testExpired()
        throws InterruptedException
    {
        final TimeoutValidator validator = new TimeoutValidator( 1000 );

        final Cache cache =
            new ValidatingCache( new LRUCache( 10 ), validator );
        cache.addListener( validator );

        cache.put( "K1", "V1" );

        Thread.sleep( 2000 );

        assertNull( cache.get( "K1" ) );
        assertNull( cache.get( "K2" ) );
    }
}
