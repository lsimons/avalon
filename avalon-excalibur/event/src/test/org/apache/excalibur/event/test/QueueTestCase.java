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
        assertEquals( 0, queue.size() );

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
        DefaultQueue queue = new DefaultQueue();
        assertEquals( 0, queue.size() );

        for ( int j = 0; j < 1000000; j++ )
        {
            queue.enqueue( elements );
            assertEquals( 10, queue.size() );

            QueueElement[] results = queue.dequeueAll();
            assertEquals( 10, results.length );
            assertEquals( 0, queue.size() );
        }
    }

    public void testDefaultQueue()
    {
        DefaultQueue queue = new DefaultQueue();
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