/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool.test;

import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.LatchedThreadGroup;
import org.apache.avalon.framework.activity.Disposable;

/**
 * This is used to profile and compare various pool implementations
 *  given a single access thread.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version $Id: MultiThreadedPoolComparisonProfile.java,v 1.1 2002/04/04 05:09:04 leif Exp $
 */
public class MultiThreadedPoolComparisonProfile
    extends PoolComparisonProfileAbstract
{
    protected static final int THREADS = 100;

    private Object m_semaphore = new Object();
    private int m_startedCount;
    private boolean m_latched;
    private int m_completedCount;
    private int m_getCount;
    private Throwable m_throwable;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public MultiThreadedPoolComparisonProfile( String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * PoolComparisonProfileAbstract Methods
     *-------------------------------------------------------------*/
    protected long getPoolRunTime( final Pool pool, final int gets )
        throws Exception
    {
        if( gets % THREADS != 0 )
        {
            fail( "gets must be evenly divisible by THREADS" );
        }

        m_getCount = 0;
        m_throwable = null;

        // Create the runnable
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                // Perform this threads part of the test.
                final int cnt = gets / THREADS;
                final Poolable[] poolTmp = new Poolable[ cnt ];
                final int loops = ( TEST_SIZE / THREADS ) / cnt;
                for( int i = 0; i < loops; i++ )
                {
                    // Get some Poolables
                    for( int j = 0; j < cnt; j++ )
                    {
                        try
                        {
                            poolTmp[ j ] = pool.get();
                            synchronized( m_semaphore )
                            {
                                m_getCount++;
                            }
                        }
                        catch( Throwable t )
                        {
                            m_poolLogger.error( "Unexpected error", t );

                            synchronized( m_semaphore )
                            {
                                if( m_throwable == null )
                                {
                                    m_throwable = t;
                                }
                            }
                            return;
                        }
                    }

                    // Make the loops hold the poolables longer than they are released, but only slightly.
                    Thread.yield();

                    // Put the Poolables back
                    for( int j = 0; j < cnt; j++ )
                    {
                        pool.put( poolTmp[ j ] );
                        synchronized( m_semaphore )
                        {
                            m_getCount--;
                        }
                        poolTmp[ j ] = null;
                    }
                }
            }
        };

        LatchedThreadGroup group = new LatchedThreadGroup( runnable, THREADS );
        group.enableLogging( m_logger );

        long duration;
        try
        {
            duration = group.go();
        }
        catch( Throwable t )
        {
            // Throwable could have been thrown by one of the tests.
            if( m_throwable == null )
            {
                m_throwable = t;
            }
            duration = 0;
        }

        if( m_throwable != null )
        {
            throw new CascadingAssertionFailedError( "Exception in test thread.", m_throwable );
        }

        assertTrue( "m_getCount == 0 (" + m_getCount + ")", m_getCount == 0 );

        // Dispose if necessary
        if( pool instanceof Disposable )
        {
            ( (Disposable)pool ).dispose();
        }

        return duration;
    }
}
