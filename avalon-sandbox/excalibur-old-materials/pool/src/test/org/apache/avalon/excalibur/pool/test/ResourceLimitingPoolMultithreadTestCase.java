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
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.ResourceLimitingPool;
import org.apache.avalon.excalibur.testcase.BufferedLogger;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.1
 */
public final class ResourceLimitingPoolMultithreadTestCase
    extends TestCase
{
    private static BufferedLogger m_logger;
    private static ClassInstanceObjectFactory m_factory;
    private static ResourceLimitingPool m_pool;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ResourceLimitingPoolMultithreadTestCase()
    {
        this( "ResourceLimitingPool Multithreaded Test Case" );
    }

    public ResourceLimitingPoolMultithreadTestCase( final String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * Suite
     *-------------------------------------------------------------*/
    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        Timer timer = new ConstantTimer( 100 );
        int maxUsers = 10;
        int iterations = 10;
        long maxElapsedTime = 20000;

        Test testCase = new ResourceLimitingPoolMultithreadTestCase( "testGetPut" );
        Test loadTest = new LoadTest( testCase, maxUsers, iterations, timer );
        Test timedTest = new TimedTest( loadTest, maxElapsedTime );
        suite.addTest( timedTest );

        TestSetup wrapper = new TestSetup( suite )
        {
            public void setUp()
            {
                oneTimeSetUp();
            }

            public void tearDown() throws Exception
            {
                oneTimeTearDown();
            }
        };

        return wrapper;
    }

    public static void oneTimeSetUp()
    {
        m_logger = new BufferedLogger();
        m_factory = new ClassInstanceObjectFactory( PoolableTestObject.class, m_logger );
        m_pool = new ResourceLimitingPool( m_factory, 0, false, false, 0, 0 );

        m_pool.enableLogging( m_logger );
    }

    public static void oneTimeTearDown() throws Exception
    {
        // The timing of this test makes it so the pool should grow to 4 elements
        assertEquals( "1) Pool Ready Size", 4, m_pool.getReadySize() );
        assertEquals( "1) Pool Size", 4, m_pool.getSize() );

        // Make sure that each of the objects are uniqe by checking them all back out.
        Poolable p1 = m_pool.get();
        Poolable p2 = m_pool.get();
        Poolable p3 = m_pool.get();
        Poolable p4 = m_pool.get();

        assertEquals( "2) Pool Ready Size", 0, m_pool.getReadySize() );
        assertEquals( "2) Pool Size", 4, m_pool.getSize() );

        assertTrue( "p1 != p2", p1 != p2 );
        assertTrue( "p1 != p3", p1 != p3 );
        assertTrue( "p1 != p4", p1 != p4 );
        assertTrue( "p2 != p3", p2 != p3 );
        assertTrue( "p2 != p4", p2 != p4 );
        assertTrue( "p3 != p4", p3 != p4 );

        m_pool.put( p1 );
        m_pool.put( p2 );
        m_pool.put( p3 );
        m_pool.put( p4 );

        assertEquals( "3) Pool Ready Size", 4, m_pool.getReadySize() );
        assertEquals( "3) Pool Size", 4, m_pool.getSize() );

        m_pool.dispose();

        assertEquals( "4) Pool Ready Size", 0, m_pool.getReadySize() );
        assertEquals( "4) Pool Size", 0, m_pool.getSize() );
    }

    /*---------------------------------------------------------------
     * TestCases
     *-------------------------------------------------------------*/
    public void testGetPut() throws Exception
    {
        Poolable p = m_pool.get();
        try
        {
            Thread.sleep( 33 );
        }
        catch( InterruptedException e )
        {
        }
        m_pool.put( p );
    }
}

