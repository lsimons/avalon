/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event.test;

import junit.framework.TestCase;
import org.apache.avalon.excalibur.event.DefaultQueue;
import org.apache.avalon.excalibur.event.PreparedEnqueue;
import org.apache.avalon.excalibur.event.QueueElement;
import org.apache.avalon.excalibur.event.SourceException;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class QueueTestCase extends TestCase
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

    public QueueTestCase( String name )
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
        DefaultQueue queue = new DefaultQueue();
        assertEquals( queue.size(), 0 );

        for ( int j = 0; j < 1000000; j++ )
        {
            queue.enqueue( element );
            assertEquals( queue.size(), 1 );

            assertNotNull( queue.dequeue() );
            assertEquals( queue.size(), 0 );
        }
    }

    public void testMillionIterationTenElements()
        throws Exception
    {
        DefaultQueue queue = new DefaultQueue();
        assertEquals( queue.size(), 0 );

        for ( int j = 0; j < 1000000; j++ )
        {
            queue.enqueue( elements );
            assertEquals( queue.size(), 10 );

            QueueElement[] results = queue.dequeueAll();
            assertEquals( results.length, 10 );
            assertEquals( queue.size(), 0 );
        }
    }

    public void testDefaultQueue()
    {
        DefaultQueue queue = new DefaultQueue();
        assertEquals( queue.size(), 0 );

        //test enqueue
        try
        {
            queue.enqueue( new TestQueueElement () );
            assertEquals( queue.size(), 1 );

            assertNotNull( queue.dequeue() );
            assertEquals( queue.size(), 0 );

            queue.enqueue( elements );
            assertEquals( queue.size(), 10 );

            QueueElement[] results = queue.dequeue( 3 );
            assertEquals( results.length, 3 );
            assertEquals( queue.size(), 7 );

            results = queue.dequeueAll();
            assertEquals( results.length, 7 );
            assertEquals( queue.size(), 0 );

            PreparedEnqueue prep = queue.prepareEnqueue( elements );
            assertEquals( queue.size(), 0 );
            prep.abort();
            assertEquals( queue.size(), 0 );

            prep = queue.prepareEnqueue( elements );
            assertEquals( queue.size(), 0 );
            prep.commit();
            assertEquals( queue.size(), 10 );

            results = queue.dequeue( queue.size() );
            assertEquals( queue.size(), 0 );
        }
        catch ( SourceException se )
        {
        }
    }
}