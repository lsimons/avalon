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
import org.apache.avalon.excalibur.event.QueueElement;
import org.apache.avalon.excalibur.event.SourceException;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class QueueTestCase extends TestCase
{
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
    }

    public void testDefaultQueue()
    {
        DefaultQueue queue = new DefaultQueue();
        assertEquals( queue.size(), 0 );

        //test enqueue
        try
        {
            queue.enqueue( new TestQueueElement () );
            assertTrue( queue.size() > 0 );

            assertNotNull( queue.dequeue() );
        }
        catch ( SourceException se )
        {
        }
    }
}