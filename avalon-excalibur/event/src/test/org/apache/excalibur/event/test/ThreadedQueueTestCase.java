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
import org.apache.excalibur.event.DefaultQueue;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.QueueElement;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.Source;

/**
 * Simple test to expose the thread queue bug
 *
 * @author <a href="mailto:proyal@managingpartners.com">Peter Royal</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 * @version VSS $Revision: 1.7 $ $Date: 2002/08/13 08:15:21 $
 */
public class ThreadedQueueTestCase
    extends TestCase
{
    private QueueStart start;
    private QueueEnd end;

    private Queue queue;

    private Thread[] stages;

    public ThreadedQueueTestCase( String name )
    {
        super( name );
    }

    public void testThreaded() throws Exception
    {
        initialize( 10000, 1 );
        start();

        initialize( 10000, 1000 );
        start();

        initialize( 20000, 1000 );
        start();

        initialize( 30000, 1000 );
        start();
    }

    public void initialize( int count, long timeout ) throws Exception
    {
        this.stages = new Thread[ 2 ];

        this.queue = new DefaultQueue();
        this.queue.setTimeout( timeout );

        this.start = new QueueStart( count );
        this.start.setSink( this.queue );
        this.stages[ 0 ] = new Thread( this.start );

        this.end = new QueueEnd();
        this.end.setSource( this.queue );
        this.end.setTimeout( timeout );
        this.stages[ 1 ] = new Thread( this.end );
    }

    public void start() throws Exception
    {
        /*
         * Commented out. Tests should be silent(?). /LS
         *
         * System.out.println("Starting test");
         */

        for( int i = 0; i < this.stages.length; i++ )
        {
            this.stages[ i ].start();
        }

        stop();
    }

    public void stop() throws Exception
    {
        for( int i = 0; i < this.stages.length; i++ )
        {
            try
            {
                this.stages[ i ].join();
            }
            catch( InterruptedException e )
            {
                throw new RuntimeException( "Stage unexpectedly interrupted: " + e );
            }
        }

        /*
         *
         * Commented out. Tests should be silent(?). /LS
         *
         * System.out.println("Test complete");

         * System.out.println("Enqueue: " + this.start.getCount() +
         *     " sum " + this.start.getSum());
         * System.out.println("Dequeue: " + this.end.getCount() +
         *     " sum " + this.end.getSum());
         */

        assertEquals( this.start.getCount(), this.end.getCount() );
        assertEquals( this.start.getSum(), this.end.getSum() );
    }

    private class QueueInteger implements QueueElement
    {
        private int integer;

        public QueueInteger( int integer )
        {
            this.integer = integer;
        }

        public int getInteger()
        {
            return integer;
        }
    }

    private class QueueStart implements Runnable
    {
        private Sink sink;
        private int queueCount;
        private int count;
        private long sum = 0;

        public QueueStart( int queueCount )
        {
            this.queueCount = queueCount;
        }

        protected void setSink( Sink sink )
        {
            this.sink = sink;
        }

        public int getCount()
        {
            return count;
        }

        public long getSum()
        {
            return sum;
        }

        public void run()
        {
            for( int i = 0; i < this.queueCount; i++ )
            {
                try
                {
                    this.sink.enqueue( new QueueInteger( i ) );
                    this.count++;
                    sum = sum * 127 + i;
                }
                catch( SinkException e )
                {
                    System.out.println( "Unable to queue: " + e.getMessage() );
                }
            }

            try
            {
                this.sink.enqueue( new QueueInteger( -1 ) );
            }
            catch( SinkException e )
            {
                System.out.println( "Unable to queue stop" );
            }
        }
    }

    private class QueueEnd implements Runnable
    {
        private Source source;
        private int count;
        private long timeout = 0;
        private long sum = 0;

        protected void setTimeout( long timeout )
        {
            this.timeout = timeout;
        }

        protected void setSource( Source source )
        {
            this.source = source;
        }

        public int getCount()
        {
            return count;
        }

        public long getSum()
        {
            return sum;
        }

        public void run()
        {
            while( true )
            {
                QueueElement qe = this.source.dequeue();

                if( qe == null )
                {
                    if( timeout > 0 )
                    {
                        try
                        {
                            Thread.sleep( timeout );
                        }
                        catch( InterruptedException ie )
                        {
                            break;
                        }
                    }
                }
                else if( qe instanceof QueueInteger )
                {
                    QueueInteger qi = (QueueInteger)qe;

                    if( qi.getInteger() == -1 )
                    {
                        break;
                    }
                    else
                    {
                        this.count++;
                        sum = sum * 127 + qi.getInteger();
                    }
                }
            }
        }
    }
}

