/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.excalibur.collections.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.collections.BinaryHeap;

/**
 *
 * @author Peter Donald
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
