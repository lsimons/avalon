/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.excalibur.event.test;

import junit.framework.TestCase;
import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.Queue;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public abstract class AbstractQueueTestCase extends TestCase
{
    Object element = new TestQueueElement();
    Object[] elements = new TestQueueElement[ 10 ];

    private static final class TestQueueElement
    {
    }

    public AbstractQueueTestCase( String name )
    {
        super( name );

        for( int i = 0; i < 10; i++ )
        {
            elements[ i ] = new TestQueueElement();
        }
    }

    protected final void performMillionIterationOneElement( Queue queue )
        throws Exception
    {
        assertEquals( 0, queue.size() );

        if( queue.maxSize() > 0 )
        {
            for( int j = 0; j < 1000000; j++ )
            {
                queue.enqueue( element );
                assertEquals( 1, queue.size() );

                assertNotNull( queue.dequeue() );
                assertEquals( 0, queue.size() );
            }
        }
        else
        {
            for( int i = 0; i < 1000; i++ )
            {
                for( int j = 0; j < 1000; j++ )
                {
                    queue.enqueue( element );
                    assertEquals( "Queue Size: " + queue.size(), j + 1, queue.size() );
                }

                for( int j = 0; j < 1000; j++ )
                {
                    assertNotNull( "Queue Size: " + queue.size(), queue.dequeue() );
                    assertEquals( "Queue Size: " + queue.size(), 999 - j, queue.size() );
                }
            }
        }
    }

    protected final void performMillionIterationTenElements( Queue queue )
        throws Exception
    {
        assertEquals( 0, queue.size() );

        if( queue.maxSize() > 0 )
        {
            for( int j = 0; j < 1000000; j++ )
            {
                queue.enqueue( elements );
                assertEquals( 10, queue.size() );

                Object[] results = queue.dequeueAll();
                assertEquals( 10, results.length );
                assertEquals( 0, queue.size() );
            }
        }
        else
        {
            for( int i = 0; i < 1000; i++ )
            {
                for( int j = 0; j < 1000; j++ )
                {
                    queue.enqueue( elements );
                    assertEquals( "Queue Size: " + queue.size(), 10 * ( j + 1 ), queue.size() );
                }

                Object[] results = queue.dequeueAll();
                assertEquals( "Queue Size: " + queue.size(), 10 * 1000, results.length );
                assertEquals( "Queue Size: " + queue.size(), 0, queue.size() );
            }
        }
    }

    protected final void performQueue( Queue queue )
        throws Exception
    {
        assertEquals( 0, queue.size() );

        queue.enqueue( new TestQueueElement() );
        assertEquals( 1, queue.size() );

        assertNotNull( queue.dequeue() );
        assertEquals( 0, queue.size() );

        queue.enqueue( elements );
        assertEquals( 10, queue.size() );

        Object[] results = queue.dequeue( 3 );
        assertEquals( 3, results.length );
        assertEquals( 7, queue.size() );

        results = queue.dequeueAll();
        assertEquals( 7, results.length );
        assertEquals( 0, queue.size() );

        PreparedEnqueue prep = queue.prepareEnqueue( elements );
        assertEquals( 0, queue.size() );
        prep.abort();
        assertEquals( 0, queue.size() );

        prep = queue.prepareEnqueue( elements );
        assertEquals( 0, queue.size() );
        prep.commit();
        assertEquals( 10, queue.size() );

        results = queue.dequeue( queue.size() );
        assertEquals( 0, queue.size() );
    }
}