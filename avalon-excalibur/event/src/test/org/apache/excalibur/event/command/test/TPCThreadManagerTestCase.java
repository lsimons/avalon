/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
