/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.collections.BucketMap;

/**
 *
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 */
public final class BucketMapTestCase
    extends TestCase
{

    private static class TestInteger
    {
        int i;

        public TestInteger( int i )
        {
            this.i = i;
        }

        public boolean equals( Object o )
        {
            return this == o;
        }

        public int hashCode()
        {
            return i;
        }

        public String toString()
        {
            return "TestInteger " + i + " @" + System.identityHashCode( this );
        }
    }

    private static final TestInteger VAL1 = new TestInteger( 5 );
    private static final TestInteger VAL2 = new TestInteger( 5 );
    private static final TestInteger VAL3 = new TestInteger( 5 );
    private static final TestInteger VAL4 = new TestInteger( 5 );
    private static final TestInteger VAL5 = new TestInteger( 5 );
    private static final TestInteger VAL6 = new TestInteger( 5 );
    private static final TestInteger VAL7 = new TestInteger( 5 );

    public BucketMapTestCase()
    {
        this( "Bucket Map Test Case" );
    }

    public BucketMapTestCase( String name )
    {
        super( name );
    }

    public void testBucket()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        assertTrue( map.size() == 1 );
        assertTrue( VAL1 == map.get( VAL1 ) );

        map.put( VAL2, VAL2 );
        assertTrue( map.size() == 2 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );

        map.put( VAL3, VAL3 );
        assertTrue( map.size() == 3 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );

        map.put( VAL4, VAL4 );
        assertTrue( map.size() == 4 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );

        map.put( VAL5, VAL5 );
        assertTrue( map.size() == 5 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );
        assertTrue( VAL5 == map.get( VAL5 ) );

        map.put( VAL6, VAL6 );
        assertTrue( map.size() == 6 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );
        assertTrue( VAL5 == map.get( VAL5 ) );
        assertTrue( VAL6 == map.get( VAL6 ) );

        map.put( VAL7, VAL7 );
        assertTrue( map.size() == 7 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );
        assertTrue( VAL5 == map.get( VAL5 ) );
        assertTrue( VAL6 == map.get( VAL6 ) );
        assertTrue( VAL7 == map.get( VAL7 ) );

        map.remove( VAL1 );
        assertTrue( map.size() == 6 );
        assertTrue( map.get( VAL1 ) == null );

        map.remove( VAL7 );
        assertTrue( map.size() == 5 );
        assertTrue( map.get( VAL7 ) == null );

        map.remove( VAL4 );
        assertTrue( map.size() == 4 );
        assertTrue( map.get( VAL4 ) == null );
    }

    public void testReplace1()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL1, VAL2 );
        assertTrue( map.size() == 1 );
        assertTrue( map.get( VAL1 ) == VAL2 );
    }

    public void testReplace2()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL2, VAL2 );
        map.put( VAL1, VAL3 );
        assertTrue( map.size() == 2 );
        assertTrue( map.get( VAL1 ) == VAL3 );
    }

    public void testReplace3()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL2, VAL2 );
        map.put( VAL3, VAL3 );
        map.put( VAL3, VAL4 );
        assertTrue( map.size() == 3 );
        assertTrue( map.get( VAL3 ) == VAL4 );
    }

    public void testReplace4()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL2, VAL2 );
        map.put( VAL3, VAL3 );
        map.put( VAL4, VAL4 );
        map.put( VAL3, VAL5 );
        assertTrue( map.size() == 4 );
        assertTrue( map.get( VAL3 ) == VAL5 );
    }
}
