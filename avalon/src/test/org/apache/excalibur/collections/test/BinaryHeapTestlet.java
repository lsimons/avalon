/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */ 
package org.apache.excalibur.collections.test;
 
import org.apache.testlet.AbstractTestlet; 
import org.apache.excalibur.collections.BinaryHeap;
 
/** 
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a> 
 */ 
public final class BinaryHeapTestlet
    extends AbstractTestlet 
{
    protected final static Integer VAL1 = new Integer( 1 );
    protected final static Integer VAL2 = new Integer( 2 );
    protected final static Integer VAL3 = new Integer( 3 );
    protected final static Integer VAL4 = new Integer( 4 );
    protected final static Integer VAL5 = new Integer( 5 );
    protected final static Integer VAL6 = new Integer( 6 );
    protected final static Integer VAL7 = new Integer( 7 );

    public void testSimpleOrder() 
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL1 );
        heap.insert( VAL2 );
        heap.insert( VAL3 );
        heap.insert( VAL4 );
        
        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL4 == heap.pop() );
    }  

    public void testReverseOrder() 
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL3 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );        

        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL4 == heap.pop() );
    }  

    public void testMixedOrder() 
    {
        final BinaryHeap heap = new BinaryHeap();

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );        
        heap.insert( VAL3 );

        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL4 == heap.pop() );
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

        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL4 == heap.pop() );
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

        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );

        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );        
        heap.insert( VAL1 );        
        heap.insert( VAL1 );        
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL4 == heap.pop() );
        assert( VAL4 == heap.pop() );
    }

    public void testReverseSimpleOrder() 
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL1 );
        heap.insert( VAL2 );
        heap.insert( VAL3 );
        heap.insert( VAL4 );

        assert( VAL4 == heap.pop() );
        assert( VAL3 == heap.pop() );        
        assert( VAL2 == heap.pop() );
        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );

    }  

    public void testReverseReverseOrder() 
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL3 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );        

        assert( VAL4 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
    }  

    public void testReverseMixedOrder() 
    {
        final BinaryHeap heap = new BinaryHeap( false );

        heap.clear();
        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );        
        heap.insert( VAL3 );

        assert( VAL4 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
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

        assert( VAL4 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
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

        assert( VAL4 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL2 == heap.pop() );

        heap.insert( VAL4 );
        heap.insert( VAL2 );
        heap.insert( VAL1 );        
        heap.insert( VAL1 );        
        heap.insert( VAL1 );        
        heap.insert( VAL1 );
        heap.insert( VAL3 );

        assert( VAL4 == heap.pop() );
        assert( VAL3 == heap.pop() );
        assert( VAL2 == heap.pop() );
        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.peek() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
        assert( VAL1 == heap.pop() );
    }  
}
