/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.pool.HardResourceLimitingPool;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.ResourceLimitingPool;
import org.apache.avalon.excalibur.pool.SoftResourceLimitingPool;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;

/**
 * Used as a basis for the PoolComparisonProfile Tests
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version $Id: PoolComparisonProfileAbstract.java,v 1.2 2003/02/25 16:28:25 bloritsch Exp $
 */
public abstract class PoolComparisonProfileAbstract
    extends TestCase
{
    /**
     * The TEST_SIZE defines the overall size of the tests.  Decreasing this will
     *  decrease the time the test takes to run, but also decrease its efficiency.
     */
    protected static final int TEST_SIZE = 50000;

    protected Logger m_logger;
    protected Logger m_poolLogger;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public PoolComparisonProfileAbstract( String name )
    {
        super( name );

        // Set to debug to see more useful information.
        org.apache.log.Logger logger =
            org.apache.log.Hierarchy.getDefaultHierarchy().getLoggerFor( "test" );
        logger.setPriority( org.apache.log.Priority.INFO );
        m_logger = new LogKitLogger( logger );

        // The output from the pools is too much data to be useful, so use a different logger.
        org.apache.log.Logger poolLogger =
            org.apache.log.Hierarchy.getDefaultHierarchy().getLoggerFor( "pool" );
        poolLogger.setPriority( org.apache.log.Priority.INFO );
        m_poolLogger = new LogKitLogger( poolLogger );
    }

    /*---------------------------------------------------------------
     * SoftResourceLimitingPool vs ResourceLimitingPool TestCases
     *-------------------------------------------------------------*/
    /**
     * Compare the SoftResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SoftResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 100 at a time,
     *  Poolables are small objects.
     */
    public void testCompare_SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_SmallPoolables()
        throws Exception
    {
        String name = "SoftResourceLimitingPool_And_ResourceLimitingPool_Max10_Gets10_SmallPoolables";

        Class poolableClass = SmallPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SoftResourceLimitingPool poolA = new SoftResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 100, factory );
    }

    /**
     * Compare the SoftResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SoftResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 200 at a time,
     *  Poolables are small objects.
     */
    public void testCompare_SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets200_SmallPoolables()
        throws Exception
    {
        String name = "SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets200_SmallPoolables";

        Class poolableClass = SmallPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SoftResourceLimitingPool poolA = new SoftResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 200, factory );
    }

    /**
     * Compare the SoftResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SoftResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 100 at a time,
     *  Poolables are medium objects.
     */
    public void testCompare_SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_MediumPoolables()
        throws Exception
    {
        String name = "SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_MediumPoolables";

        Class poolableClass = MediumPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SoftResourceLimitingPool poolA = new SoftResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 100, factory );
    }

    /**
     * Compare the SoftResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SoftResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 200 at a time,
     *  Poolables are medium objects.
     */
    public void testCompare_SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets200_MediumPoolables()
        throws Exception
    {
        String name = "SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets200_MediumPoolables";

        Class poolableClass = MediumPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SoftResourceLimitingPool poolA = new SoftResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 200, factory );
    }

    /**
     * Compare the SoftResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SoftResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 100 at a time,
     *  Poolables are large objects.
     */
    public void testCompare_SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_LargePoolables()
        throws Exception
    {
        String name = "SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_LargePoolables";

        Class poolableClass = LargePoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SoftResourceLimitingPool poolA = new SoftResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 100, factory );
    }

    /**
     * Compare the SoftResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SoftResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 200 at a time,
     *  Poolables are large objects.
     */
    public void testCompare_SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets200_LargePoolables()
        throws Exception
    {
        String name = "SoftResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets200_LargePoolables";

        Class poolableClass = LargePoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SoftResourceLimitingPool poolA = new SoftResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 200, factory );
    }

    /*---------------------------------------------------------------
     * HardResourceLimitingPool vs ResourceLimitingPool TestCases
     *-------------------------------------------------------------*/
    /**
     * Compare the HardResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a HardResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 100 at a time,
     *  Poolables are small objects.
     */
    public void testCompare_HardResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_SmallPoolables()
        throws Exception
    {
        String name = "HardResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_SmallPoolables";

        Class poolableClass = SmallPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = true;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        HardResourceLimitingPool poolA = new HardResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 100, factory );
    }

    /**
     * Compare the HardResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a HardResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 100 at a time,
     *  Poolables are medium objects.
     */
    public void testCompare_HardResourceLimitingPool_And_ResourceLimitingPool_Max10_Gets10_MediumPoolables()
        throws Exception
    {
        String name = "HardResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_MediumPoolables";

        Class poolableClass = MediumPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = true;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        HardResourceLimitingPool poolA = new HardResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 100, factory );
    }

    /**
     * Compare the HardResourceLimitingPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a HardResourceLimitingPool.
     * <p>
     * Test will use pools with a max size of 100, while getting up to 100 at a time,
     *  Poolables are large objects.
     */
    public void testCompare_HardResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_LargePoolables()
        throws Exception
    {
        String name = "HardResourceLimitingPool_And_ResourceLimitingPool_Max100_Gets100_LargePoolables";

        Class poolableClass = LargePoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 100;
        boolean maxStrict = true;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        HardResourceLimitingPool poolA = new HardResourceLimitingPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 100, factory );
    }

    /*---------------------------------------------------------------
     * Test Classes
     *-------------------------------------------------------------*/
    public static class SmallPoolable
        implements Poolable
    {
        int a;
    }

    public static class MediumPoolable
        implements Poolable
    {
        int[] a = new int[ 100 ];
    }

    public static class LargePoolable
        implements Poolable
    {
        int[][] a = new int[ 10 ][ 100 ];
    }

    /**
     * Dummy class used for timing test cases where no pooling is done.
     */
    public static class NoPoolingPool
        implements Pool, LogEnabled
    {
        private ObjectFactory m_factory;
        private Logger m_logger;

        public NoPoolingPool( ObjectFactory factory )
        {
            m_factory = factory;
        }

        public void enableLogging( Logger logger )
        {
            m_logger = logger;
        }

        public Poolable get() throws Exception
        {
            return (Poolable)m_factory.newInstance();
        }

        public void put( Poolable poolable )
        {
            try
            {
                m_factory.decommission( poolable );
            }
            catch( Exception e )
            {
                m_logger.debug( "Error decommissioning object", e );
            }
        }
    }

    /*---------------------------------------------------------------
     * Utility Methods
     *-------------------------------------------------------------*/
    protected void resetMemory()
    {
        System.gc();
        System.gc();

        // Let the system settle down.
        try
        {
            Thread.sleep( 50 );
        }
        catch( InterruptedException e )
        {
        }
        Runtime runtime = Runtime.getRuntime();
        m_logger.debug( "Memory: " + ( runtime.totalMemory() - runtime.freeMemory() ) );
    }

    protected String getShortClassName( Object o )
    {
        String name = o.getClass().getName();
        int pos = name.lastIndexOf( '.' );
        if( pos > 0 )
        {
            name = name.substring( pos + 1 );
        }
        return name;
    }

    protected abstract long getPoolRunTime( Pool pool, int gets )
        throws Exception;

    /**
     * The guts of the various test cases.  Will dispose the pools
     */
    protected void generalTest( String name, Pool poolA, Pool poolB, int gets, ObjectFactory factory )
        throws Exception
    {
        m_logger.info( "Test Case: " + name );

        // Get the short class names
        final String poolAName = getShortClassName( poolA );
        final String poolBName = getShortClassName( poolB );

        // Start clean
        resetMemory();

        // Get a baseline speed for object creation
        NoPoolingPool poolBase = new NoPoolingPool( factory );
        poolBase.enableLogging( m_poolLogger );
        final long noPoolDuration = getPoolRunTime( poolBase, gets );
        m_logger.info( "     Unpooled time = " + noPoolDuration + "ms. to use " + TEST_SIZE + " objects." );
        resetMemory();


        // Get the time for poolA
        final long poolADuration = getPoolRunTime( poolA, gets );
        m_logger.info( "     " + poolAName + " time = " + poolADuration + "ms. to use " + TEST_SIZE + " objects, " + gets + " at a time." );
        resetMemory();


        // Get the time for poolB
        final long poolBDuration = getPoolRunTime( poolB, gets );
        m_logger.info( "     " + poolBName + " time = " + poolBDuration + "ms. to use " + TEST_SIZE + " objects, " + gets + " at a time." );
        resetMemory();

        // Show a summary
        if( m_logger.isInfoEnabled() )
        {
            double mult;
            mult = ( poolADuration > 0 ? ( noPoolDuration * 100 / poolADuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => " + poolAName + " is " + mult + " X as fast as not pooling." );

            mult = ( poolBDuration > 0 ? ( noPoolDuration * 100 / poolBDuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => " + poolBName + " is " + mult + " X as fast as not pooling." );

            mult = ( poolBDuration > 0 ? ( poolADuration * 100 / poolBDuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => " + poolBName + " is " + mult + " X as fast as " + poolAName + "." );
        }
    }
}
