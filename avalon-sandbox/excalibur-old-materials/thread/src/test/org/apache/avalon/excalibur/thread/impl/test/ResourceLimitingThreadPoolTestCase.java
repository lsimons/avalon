/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool.test;

import com.clarkware.junitperf.ConstantTimer;
import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TimedTest;
import com.clarkware.junitperf.Timer;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.avalon.excalibur.testcase.BufferedLogger;
import org.apache.avalon.excalibur.thread.impl.ResourceLimitingThreadPool;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/05/13 12:17:38 $
 * @since 4.1
 */
public final class ResourceLimitingThreadPoolTestCase
    extends TestCase
{
    private volatile int m_completeCount;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ResourceLimitingThreadPoolTestCase()
    {
        this( "ResourceLimitingThreadPool Test Case" );
    }

    public ResourceLimitingThreadPoolTestCase( final String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * Suite
     *-------------------------------------------------------------*/
    public void test1Worker1Task()
    {
        commonTest( 1, 1, 0L, 200L, 1, true, true, -1, -1 );
    }

    public void test1Worker5Tasks()
    {
        // One will start immediately, 4 will have to wait 200ms each in turn.
        commonTest( 5, 1, 800L, 1000L, 1, true, true, -1, -1 );
    }

    public void test5Workers10Tasks()
    {
        // 5 will start immediately, 5 will have to wait 200ms for the first 5 to complete.
        commonTest( 10, 5, 200L, 400L, 5, true, true, -1, -1 );
    }

    public void test10Workers100Tasks()
    {
        // 10 will start immediately, next 10 will have to wait 200ms for the
        //  first 10 to complete and so on.
        commonTest( 100, 10, 1800L, 2000L, 10, true, true, -1, -1 );
    }

    public void test5Workers6TasksNoBlock()
    {
        commonTest( 6, 5, 0L, 200L, 5, true, false, -1, -1 );
    }

    public void test5Workers10TasksNotStrict()
    {
        commonTest( 10, 10, 0L, 200L, 5, false, false, -1, -1 );
    }

    private void incCompleteCount()
    {
        synchronized( this )
        {
            m_completeCount++;
        }
    }

    private void commonTest( int taskCount,
                             int firstSize,
                             long firstTime,
                             long totalTime,
                             int max,
                             boolean maxStrict,
                             boolean blocking,
                             long blockTimeout,
                             long trimInterval )
    {
        BufferedLogger logger = new BufferedLogger();
        ResourceLimitingThreadPool pool = new ResourceLimitingThreadPool(
            "Test Worker Pool", max, maxStrict, blocking, blockTimeout, trimInterval );
        pool.enableLogging( logger );

        Runnable runner = new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep( 200 );
                }
                catch( InterruptedException e )
                {
                }

                incCompleteCount();
            }
        };

        long start = System.currentTimeMillis();
        m_completeCount = 0;
        for( int i = 0; i < taskCount; i++ )
        {
            if( maxStrict && ( !blocking ) && i >= max )
            {
                // This request shoudl throw an exception.
                try
                {
                    pool.execute( runner );
                    fail( "Should have failed when requesting more than max resources." );
                }
                catch( Exception e )
                {
                    // Ok
                    incCompleteCount();
                }
            }
            else
            {
                pool.execute( runner );
            }
        }
        long dur = System.currentTimeMillis() - start;

        // Make sure that the size of the pool is what is expected.
        assertEquals( "The pool size was not what it should be.", firstSize, pool.getSize() );

        // Make sure this took about the right amount of time to get here.
        //System.out.println( "First time: " + dur );
        if( Math.abs( dur - firstTime ) > 50 )
        {
            fail( "Time to start all tasks, " + dur +
                  ", was not within 50ms of the expected time, " + firstTime );
        }

        // Wait for all worker threads to complete.
        while( m_completeCount < taskCount )
        {
            try
            {
                Thread.sleep( 10 );
            }
            catch( InterruptedException e )
            {
            }
        }

        dur = System.currentTimeMillis() - start;

        // Make sure this took about the right amount of time to get here.
        //System.out.println( "Total time: " + dur );
        if( Math.abs( dur - totalTime ) > 50 )
        {
            fail( "Time to complete all tasks, " + dur +
                  ", was not within 50ms of the expected time, " + totalTime );
        }

        //System.out.println( logger.toString() );
    }
}

