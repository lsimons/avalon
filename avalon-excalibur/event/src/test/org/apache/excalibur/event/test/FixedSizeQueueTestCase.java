/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event.test;

import junit.framework.TestCase;
import org.apache.avalon.excalibur.event.FixedSizeQueue;
import org.apache.avalon.excalibur.event.PreparedEnqueue;
import org.apache.avalon.excalibur.event.QueueElement;
import org.apache.avalon.excalibur.event.SourceException;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class FixedSizeQueueTestCase extends TestCase
{
    QueueElement element = new TestQueueElement();
    QueueElement[] elements = new TestQueueElement[10];

    private static final class TestQueueElement implements QueueElement
    {
         private final Object m_attachment;

         private TestQueueElement()
         {
             this( null );
         }

         private TestQueueElement( Object attachment )
         {
             m_attachment = attachment;
         }

         public Object getAttachment()
         {
             return m_attachment;
         }

         public long getType()
         {
             return 1;
         }
    }

    public FixedSizeQueueTestCase( String name )
    {
        super( name );

        for ( int i = 0; i < 10; i++ )
        {
            elements[i] = new TestQueueElement();
        }
    }

    public void testMillionIterationOneElement()
        throws Exception
    {
        FixedSizeQueue queue = new FixedSizeQueue(10);
        assertEquals( queue.size(), 0 );

        for ( int j = 0; j < 1000000; j++ )
        {
            queue.enqueue( element );
            assertEquals( 1, queue.size() );

            assertNotNull( queue.dequeue() );
            assertEquals( 0, queue.size() );
        }
    }

    public void testMillionIterationTenElements()
        throws Exception
    {
        FixedSizeQueue queue = new FixedSizeQueue(10);
        assertEquals( queue.size(), 0 );

        for ( int j = 0; j < 1000000; j++ )
        {
            queue.enqueue( elements );
            assertEquals( 0, queue.size() );

            QueueElement[] results = queue.dequeueAll();
            assertEquals( 10, results.length );
            assertEquals( 0, queue.size() );
        }
    }

    public void testDefaultQueue()
    {
        FixedSizeQueue queue = new FixedSizeQueue( 10 );
        assertEquals( 0, queue.size() );

        //test enqueue
        try
        {
            queue.enqueue( new TestQueueElement () );
            assertEquals( 1, queue.size() );

            assertNotNull( queue.dequeue() );
            assertEquals( 0, queue.size() );

            queue.enqueue( elements );
            assertEquals( 10, queue.size() );

            QueueElement[] results = queue.dequeue( 3 );
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
        catch ( SourceException se )
        {
        }
    }
}