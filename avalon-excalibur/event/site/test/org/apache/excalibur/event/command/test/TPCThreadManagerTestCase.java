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
package org.apache.excalibur.event.command.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.event.DefaultQueue;
import org.apache.excalibur.event.EventHandler;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.QueueElement;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.Source;
import org.apache.excalibur.event.command.EventPipeline;
import org.apache.excalibur.event.command.TPCThreadManager;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:greg-tpcthreadmanager@nest.cx">Gregory Steuck</a>
 */
public class TPCThreadManagerTestCase extends TestCase
{
    public TPCThreadManagerTestCase( String name )
    {
        super( name );
    }

    // number of milliseconds it reasonably takes the JVM to switch threads
    private final static int SCHEDULING_TIMEOUT = 1000; // ms

    // number of times the handler should be called
    private final static int MINIMAL_NUMBER_INVOCATIONS = 2;

    private Parameters createParameters( int processors, int threadsPerProcessor, long sleep )
    {
        final Parameters parameters = new Parameters();

        parameters.setParameter( "processors", String.valueOf( processors ) );
        parameters.setParameter( "threads-per-processor", String.valueOf( threadsPerProcessor ) );
        parameters.setParameter( "sleep-time", String.valueOf( sleep ) );

        return parameters;
    }

    /**
     * Checks TPCThreadManager ability to survive the situation when
     * it tries to schedule more tasks than it has threads. Originally
     * it was dying due to hitting Pool limit and not catching the
     * resulting runtime exception.
     * <p>
     * The test is not foolproof, it probably depends on preemtive
     * threads management.
     */
    public void testThreadContention() throws Exception
    {
        // enforces only 1 thread and no timeout which makes it
        // fail quickly
        final TPCThreadManager threadManager = new TPCThreadManager();

        threadManager.parameterize( createParameters( 1, 1, 0 ) );
        threadManager.initialize();

        // an obviously syncronized component
        final StringBuffer result = new StringBuffer();
        final StringWriter exceptionBuffer = new StringWriter();
        final PrintWriter errorOut = new PrintWriter( exceptionBuffer );

        threadManager.register( new Pipeline( result, errorOut ) );

        // sleeps for 1 more scheduling timeout to surely go over limit
        Thread.sleep( SCHEDULING_TIMEOUT * ( MINIMAL_NUMBER_INVOCATIONS + 1 ) );

        int numberCalls = result.length();

        String msg =
          "Number of calls to handler (" + numberCalls +
          ") is less than the expected number of calls (" +
          MINIMAL_NUMBER_INVOCATIONS + ")";

        assertTrue( msg, numberCalls >= MINIMAL_NUMBER_INVOCATIONS );

        errorOut.flush(); // why not?

        String stackTrace = exceptionBuffer.toString();

        assertEquals( "Exceptions while running the test",
                      "",
                      stackTrace );
    }

    private static class Pipeline implements EventPipeline, EventHandler
    {
        private final Queue m_queue = new DefaultQueue();
        private final Source[] m_sources = new Source[]{m_queue};
        private final StringBuffer m_result;
        private final PrintWriter m_errorOut;

        Pipeline( StringBuffer resultAccumulator, PrintWriter errorOut )
          throws SinkException
        {
            m_result = resultAccumulator;
            m_errorOut = errorOut;
            // even though TPCThreadManager currently calls event handlers
            // when there is nothing to do, that may change
            m_queue.enqueue( new QueueElement()
            {
            } );
        }

        public EventHandler getEventHandler()
        {
            return this;
        }

        public final Source[] getSources()
        {
            return m_sources;
        }

        public final Sink getSink()
        {
            return m_queue;
        }

        public void handleEvent( QueueElement element )
        {
            handleEvents( new QueueElement[]{element} );
        }

        public void handleEvents( QueueElement[] elements )
        {
            // records the fact that the handler was called
            m_result.append( 'a' );
            try
            {
                // sleeps to occupy the thread and let thread manager try to reschedule
                Thread.sleep( SCHEDULING_TIMEOUT );
                // enqueues another element to be called again
                m_queue.enqueue( new QueueElement()
                {
                } );
            }
            catch( Exception e )
            {
                // fails the test, no exceptions are expected
                e.printStackTrace( m_errorOut );

            }
        }
    }
}
