/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.test;

import org.apache.avalon.excalibur.component.PoolableComponentHandler;
import org.apache.avalon.excalibur.testcase.BufferedLogger;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.component.ComponentManager;

/**
 * Test the PoolableComponentHandler.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 */
public class PoolableComponentHandlerTestCase
    extends ExcaliburTestCase
{
    private Exception m_exception;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public PoolableComponentHandlerTestCase( String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Inner access method to manager to work around a bug in the Javac compiler
     *  when manager is referenced from the method of an inner class.  Jikes seems to
     *  handle it Ok. :-/
     */
    private ComponentManager getInnerManager()
    {
        return manager;
    }

    /*---------------------------------------------------------------
     * TestCase Methods
     *-------------------------------------------------------------*/
    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /*---------------------------------------------------------------
     * Test Cases
     *-------------------------------------------------------------*/
    /**
     * Test the default values and make sure that objects are reused as expected.
     */
    public void testDefaults() throws Exception
    {
        String name = "testDefaults";
        getLogger().info( "Test: " + name );

        int size = PoolableComponentHandler.DEFAULT_MAX_POOL_SIZE + 2;

        BufferedLogger logger = new BufferedLogger();
        PoolableTestObject.setStaticLoggger( logger );
        PoolableTestObject.resetInstanceCounter();

        PoolableTestObject[] poolables = new PoolableTestObject[ size ];

        // Lookup the components.
        for( int i = 0; i < size; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }

        // Release the components.
        for( int i = 0; i < size; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // Lookup the components.
        for( int i = 0; i < size; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }

        // Release the components.
        for( int i = 0; i < size; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // The disposal of the objects will not show up in the log until the component manager is
        //  actually disposed.
        // When objects are returned the pool, they are stored in a last in first off list.
        String resultLog = logger.toString();
        String expectedLog =
            "DEBUG - PoolableTestObject #1 initialized.\n" +
            "DEBUG - PoolableTestObject #2 initialized.\n" +
            "DEBUG - PoolableTestObject #3 initialized.\n" +
            "DEBUG - PoolableTestObject #4 initialized.\n" +
            "DEBUG - PoolableTestObject #5 initialized.\n" +
            "DEBUG - PoolableTestObject #6 initialized.\n" +
            "DEBUG - PoolableTestObject #7 initialized.\n" +
            "DEBUG - PoolableTestObject #8 initialized.\n" +
            "DEBUG - PoolableTestObject #9 initialized.\n" +
            "DEBUG - PoolableTestObject #10 initialized.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" +
            "DEBUG - PoolableTestObject #1 disposed.\n" + // Still 9 outstanding
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #2 disposed.\n" + // Still 8 outstanding
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n" +
            "DEBUG - PoolableTestObject #5 recycled.\n" +
            "DEBUG - PoolableTestObject #6 recycled.\n" +
            "DEBUG - PoolableTestObject #7 recycled.\n" +
            "DEBUG - PoolableTestObject #8 recycled.\n" +
            "DEBUG - PoolableTestObject #9 recycled.\n" +
            "DEBUG - PoolableTestObject #10 recycled.\n" +
            "DEBUG - PoolableTestObject #11 initialized.\n" +
            "DEBUG - PoolableTestObject #12 initialized.\n" +
            "DEBUG - PoolableTestObject #10 recycled.\n" + // Gets are in LIFO order.
            "DEBUG - PoolableTestObject #10 disposed.\n" + // Still 9 outstanding
            "DEBUG - PoolableTestObject #9 recycled.\n" +
            "DEBUG - PoolableTestObject #9 disposed.\n" + // Still 8 outstanding
            "DEBUG - PoolableTestObject #8 recycled.\n" +
            "DEBUG - PoolableTestObject #7 recycled.\n" +
            "DEBUG - PoolableTestObject #6 recycled.\n" +
            "DEBUG - PoolableTestObject #5 recycled.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n" +
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #11 recycled.\n" +
            "DEBUG - PoolableTestObject #12 recycled.\n";

        assertEquals( "Log did not contain the expected output.", resultLog, expectedLog );
    }

    /**
     * Test a non-default max value.
     */
    public void testMax4() throws Exception
    {
        String name = "testMax4";
        getLogger().info( "Test: " + name );

        int size = 4 + 1;

        BufferedLogger logger = new BufferedLogger();
        PoolableTestObject.setStaticLoggger( logger );
        PoolableTestObject.resetInstanceCounter();

        PoolableTestObject[] poolables = new PoolableTestObject[ size ];

        // Lookup the components.
        for( int i = 0; i < size; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }

        // Release the components.
        for( int i = 0; i < size; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // Lookup the components.
        for( int i = 0; i < size; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }

        // Release the components.
        for( int i = 0; i < size; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // The disposal of the objects will not show up in the log until the component manager is
        //  actually disposed.
        String resultLog = logger.toString();
        String expectedLog =
            "DEBUG - PoolableTestObject #1 initialized.\n" +
            "DEBUG - PoolableTestObject #2 initialized.\n" +
            "DEBUG - PoolableTestObject #3 initialized.\n" +
            "DEBUG - PoolableTestObject #4 initialized.\n" +
            "DEBUG - PoolableTestObject #5 initialized.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" +
            "DEBUG - PoolableTestObject #1 disposed.\n" + // Still 4 outstanding
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n" +
            "DEBUG - PoolableTestObject #5 recycled.\n" +
            "DEBUG - PoolableTestObject #6 initialized.\n" +
            "DEBUG - PoolableTestObject #5 recycled.\n" + // Gets are in LIFO order.
            "DEBUG - PoolableTestObject #5 disposed.\n" + // Still 4 outstanding
            "DEBUG - PoolableTestObject #4 recycled.\n" +
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #6 recycled.\n";

        assertEquals( "Log did not contain the expected output.", resultLog, expectedLog );
    }

    /**
     * Test a non-default max value with a strict max and no blocking
     */
    public void testMax4StrictNoBlocking() throws Exception
    {
        String name = "testMax4StrictNoBlocking";
        getLogger().info( "Test: " + name );

        int size = 4;

        BufferedLogger logger = new BufferedLogger();
        PoolableTestObject.setStaticLoggger( logger );
        PoolableTestObject.resetInstanceCounter();

        PoolableTestObject[] poolables = new PoolableTestObject[ size ];

        // Lookup the components.
        for( int i = 0; i < size; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }

        // Try to get one more.  Should fail.
        try
        {
            manager.lookup( PoolableTestObject.ROLE + "_" + name );
            fail( "Attempt to get more Pollables than are in the pool should have failed." );
        }
        catch( Exception e )
        {
            // Passed
        }

        // Release the components.
        for( int i = 0; i < size; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // The disposal of the objects will not show up in the log until the component manager is
        //  actually disposed.
        String resultLog = logger.toString();
        String expectedLog =
            "DEBUG - PoolableTestObject #1 initialized.\n" +
            "DEBUG - PoolableTestObject #2 initialized.\n" +
            "DEBUG - PoolableTestObject #3 initialized.\n" +
            "DEBUG - PoolableTestObject #4 initialized.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" +
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n";

        assertEquals( "Log did not contain the expected output.", resultLog, expectedLog );
    }

    /**
     * Test a non-default max value with a strict max and blocking with no timeout
     */
    public void testMax4StrictBlocking() throws Exception
    {
        final String name = "testMax4StrictBlocking";
        getLogger().info( "Test: " + name );

        int size = 3;

        // Initialize the exception field.
        m_exception = null;

        final BufferedLogger logger = new BufferedLogger();
        PoolableTestObject.setStaticLoggger( logger );
        PoolableTestObject.resetInstanceCounter();

        PoolableTestObject[] poolables = new PoolableTestObject[ size ];

        // Lookup the components.
        for( int i = 0; i < size; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }

        // In another thread, get and release another poolable to cause this one to wait.
        new Thread()
        {
            public void run()
            {
                try
                {
                    logger.debug( "Lookup in second thread." );
                    PoolableTestObject poolable =
                        (PoolableTestObject)getInnerManager().lookup( PoolableTestObject.ROLE + "_" + name );

                    // Give the main thread a chance to block
                    try
                    {
                        Thread.sleep( 500 );
                    }
                    catch( InterruptedException e )
                    {
                    }

                    logger.debug( "Release in second thread." );
                    getInnerManager().release( poolable );
                }
                catch( Exception e )
                {
                    m_exception = e;
                }
            }
        }.start();

        // Give the second thread a chance to get the 4th poolable
        try
        {
            Thread.sleep( 250 );
        }
        catch( InterruptedException e )
        {
        }

        // Try to get one more.  Should block until the other thread has put it back.
        logger.debug( "Lookup in main thread." );
        PoolableTestObject poolable =
            (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );

        logger.debug( "Release in main thread." );
        manager.release( poolable );

        // Release the components.
        for( int i = 0; i < size; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // Make sure that the second thread did not throw an exception
        assertTrue( "Unexpected exception in second thread.", m_exception == null );

        // The disposal of the objects will not show up in the log until the component manager is
        //  actually disposed.
        String resultLog = logger.toString();
        String expectedLog =
            "DEBUG - PoolableTestObject #1 initialized.\n" +
            "DEBUG - PoolableTestObject #2 initialized.\n" +
            "DEBUG - PoolableTestObject #3 initialized.\n" +
            "DEBUG - Lookup in second thread.\n" +
            "DEBUG - PoolableTestObject #4 initialized.\n" +
            "DEBUG - Lookup in main thread.\n" +
            "DEBUG - Release in second thread.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n" +
            "DEBUG - Release in main thread.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" +
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #3 recycled.\n";

        assertEquals( "Log did not contain the expected output.", resultLog, expectedLog );
    }

    /**
     * Test a non-default max value with a strict max and blocking with a timeout
     */
    public void testMax4StrictBlockingTimeout() throws Exception
    {
        String name = "testMax4StrictBlockingTimeout";
        getLogger().info( "Test: " + name );

        int size = 4;

        BufferedLogger logger = new BufferedLogger();
        PoolableTestObject.setStaticLoggger( logger );
        PoolableTestObject.resetInstanceCounter();

        PoolableTestObject[] poolables = new PoolableTestObject[ size ];

        // Lookup the components.
        for( int i = 0; i < size; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }

        // Try to get one more.  Should fail after 500 milliseconds.
        long start = System.currentTimeMillis();
        try
        {
            manager.lookup( PoolableTestObject.ROLE + "_" + name );
            fail( "Attempt to get more Pollables than are in the pool should have failed." );
        }
        catch( Exception e )
        {
            // Passed
        }
        long dur = System.currentTimeMillis() - start;
        assertTrue( "Block timeout was not within 50 milliseconds of the configured 500 milliseconds,",
                    dur >= 450 && dur <= 550 );

        // Release the components.
        for( int i = 0; i < size; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // The disposal of the objects will not show up in the log until the component manager is
        //  actually disposed.
        String resultLog = logger.toString();
        String expectedLog =
            "DEBUG - PoolableTestObject #1 initialized.\n" +
            "DEBUG - PoolableTestObject #2 initialized.\n" +
            "DEBUG - PoolableTestObject #3 initialized.\n" +
            "DEBUG - PoolableTestObject #4 initialized.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" +
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n";

        assertEquals( "Log did not contain the expected output.", resultLog, expectedLog );
    }

    /**
     * Test the trimming features.
     */
    public void testTrimming() throws Exception
    {
        String name = "testTrimming";
        getLogger().info( "Test: " + name );

        BufferedLogger logger = new BufferedLogger();
        PoolableTestObject.setStaticLoggger( logger );
        PoolableTestObject.resetInstanceCounter();

        PoolableTestObject[] poolables = new PoolableTestObject[ 4 ];

        // Lookup and release all 4 components a couple of times.
        for( int i = 0; i < 4; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }
        for( int i = 0; i < 4; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }
        for( int i = 0; i < 4; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }
        for( int i = 0; i < 4; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // Now wait for 550 ms to trigger a trim on the next lookup.
        try
        {
            Thread.sleep( 550 );
        }
        catch( InterruptedException e )
        {
        }

        // Lookup and release 2 components to mark them as being recently used.
        for( int i = 0; i < 2; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }
        for( int i = 0; i < 2; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }

        // Now wait for 550 ms to trigger a trim on the next lookup.
        try
        {
            Thread.sleep( 550 );
        }
        catch( InterruptedException e )
        {
        }

        // This next get should cause 2 of the components to be trimmed but the 2 we just lookedup
        //  should stay around.
        // Lookup and release all 4 components to see which ones are left.
        for( int i = 0; i < 4; i++ )
        {
            poolables[ i ] =
                (PoolableTestObject)manager.lookup( PoolableTestObject.ROLE + "_" + name );
        }
        for( int i = 0; i < 4; i++ )
        {
            manager.release( poolables[ i ] );
            poolables[ i ] = null;
        }


        // The disposal of the objects will not show up in the log until the component manager is
        //  actually disposed.
        String resultLog = logger.toString();
        String expectedLog =
            "DEBUG - PoolableTestObject #1 initialized.\n" + // First 4 lookups
            "DEBUG - PoolableTestObject #2 initialized.\n" +
            "DEBUG - PoolableTestObject #3 initialized.\n" +
            "DEBUG - PoolableTestObject #4 initialized.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" + // First 4 releases
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n" +
            "DEBUG - PoolableTestObject #4 recycled.\n" + // Second 4 releases already existed.
            "DEBUG - PoolableTestObject #3 recycled.\n" +
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" +
            "DEBUG - PoolableTestObject #1 recycled.\n" + // 2 lookups after wait.
            "DEBUG - PoolableTestObject #2 recycled.\n" +
            "DEBUG - PoolableTestObject #4 disposed.\n" + // First lookup after second wait triggers disposal of 2 old Poolables.
            "DEBUG - PoolableTestObject #3 disposed.\n" +
            "DEBUG - PoolableTestObject #5 initialized.\n" + // 4 lookups requred 2 more instances.
            "DEBUG - PoolableTestObject #6 initialized.\n" +
            "DEBUG - PoolableTestObject #2 recycled.\n" + // Final 4 releases
            "DEBUG - PoolableTestObject #1 recycled.\n" +
            "DEBUG - PoolableTestObject #5 recycled.\n" +
            "DEBUG - PoolableTestObject #6 recycled.\n";

        assertEquals( "Log did not contain the expected output.", resultLog, expectedLog );
    }
}

