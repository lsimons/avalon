/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.collections.BinaryHeap;

/**
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class BinaryHeapTestCase
    extends TestCase
{
    private static final Integer VAL1 = new Integer( 1 );
    private static final Integer VAL2 = new Integer( 2 );
    private static final Integer VAL3 = new Integer( 3 );
    private static final Integer VAL4 = new Integer( 4 );
    private static final Integer VAL5 = new Integer( 5 );
    private static final Integer VAL6 = new Integer( 6 );
    private static final Integer VAL7 = new Integer( 7 );

    public BinaryHeapTestCase()
    {
        this( "Binary Heap Test Case" );
    }

    public BinaryHeapTestCase( String name )
    {
        super( name );
    }

    public void testSimpleOrder()
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL1 );
        heap.insert( VAL2 );
        heap.insert( VAL3 );
        heap.insert( VAL4 );

        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL4 == heap.pop() );
    }

    public void testReverseOrder()
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL3 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );

        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL4 == heap.pop() );
    }

    public void testMixedOrder()
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL4 == heap.pop() );
    }

    public void testDuplicates()
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL4 == heap.pop() );
    }

    public void testMixedInsertPopOrder()
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL1 );
        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );

        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL4 == heap.pop() );
        assertTrue( VAL4 == heap.pop() );
    }

    public void testReverseSimpleOrder()
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL1 );
        heap.insert( VAL2 );
        heap.insert( VAL3 );
        heap.insert( VAL4 );

        assertTrue( VAL4 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );

    }

    public void testReverseReverseOrder()
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL3 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );

        assertTrue( VAL4 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
    }

    public void testReverseMixedOrder()
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assertTrue( VAL4 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
    }

    public void testReverseDuplicates()
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL3 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );

        assertTrue( VAL4 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
    }

    public void testReverseMixedInsertPopOrder()
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL1 );
        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assertTrue( VAL4 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );

        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assertTrue( VAL4 == heap.pop() );
        assertTrue( VAL3 == heap.pop() );
        assertTrue( VAL2 == heap.pop() );
        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.peek() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
        assertTrue( VAL1 == heap.pop() );
    }
}
