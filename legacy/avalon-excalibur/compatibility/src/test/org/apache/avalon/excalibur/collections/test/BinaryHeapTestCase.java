/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.collections.test;

import org.apache.avalon.excalibur.collections.BinaryHeap;

import junit.framework.TestCase;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
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
