/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool.test;

import junit.framework.TestCase;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.ResourceLimitingPool;
import org.apache.avalon.excalibur.testcase.BufferedLogger;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.1
 */
public final class ResourceLimitingPoolTestCase extends TestCase
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ResourceLimitingPoolTestCase()
    {
        this( "ResourceLimitingPool Test Case" );
    }

    public ResourceLimitingPoolTestCase( final String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * TestCases
     *-------------------------------------------------------------*/
    public void testCreateDestroy()
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );
        pool.dispose();

        // Make sure the logger output check out.
        assertEquals(
            logger.toString(),
            ""
        );
    }

    public void testSingleGetPut() throws Exception
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );

        assertEquals( "1) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "1) Pool Size", 0, pool.getSize() );

        Poolable p = pool.get();

        assertEquals( "2) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "2) Pool Size", 1, pool.getSize() );

        pool.put( p );

        assertEquals( "3) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "3) Pool Size", 1, pool.getSize() );

        pool.dispose();

        // Make sure the logger output check out.
        assertEquals( "Logger output",
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:1\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:1\n",
                      logger.toString()
        );
    }

    public void testSingleGetPutPoolCheck() throws Exception
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );

        assertEquals( "1) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "1) Pool Size", 0, pool.getSize() );

        Poolable p1 = pool.get();

        assertEquals( "2) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "2) Pool Size", 1, pool.getSize() );

        pool.put( p1 );

        assertEquals( "3) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "3) Pool Size", 1, pool.getSize() );

        Poolable p2 = pool.get();

        assertEquals( "4) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "4) Pool Size", 1, pool.getSize() );

        assertEquals( "Pooled Object reuse check", p1, p2 );

        pool.put( p2 );

        assertEquals( "5) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "5) Pool Size", 1, pool.getSize() );

        pool.dispose();

        // Make sure the logger output check out.
        assertEquals( "Logger output",
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:1\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:1\n",
                      logger.toString()
        );
    }

    public void testMultipleGetPut() throws Exception
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );

        assertEquals( "1) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "1) Pool Size", 0, pool.getSize() );

        Poolable p1 = pool.get();

        assertEquals( "2) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "2) Pool Size", 1, pool.getSize() );

        Poolable p2 = pool.get();

        assertEquals( "3) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "3) Pool Size", 2, pool.getSize() );

        pool.put( p1 );

        assertEquals( "4) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "4) Pool Size", 2, pool.getSize() );

        pool.put( p2 );

        assertEquals( "5) Pool Ready Size", 2, pool.getReadySize() );
        assertEquals( "5) Pool Size", 2, pool.getSize() );

        pool.dispose();

        // Make sure the logger output check out.
        assertEquals( "Logger output",
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:1\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:2\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:1\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:2\n",
                      logger.toString()
        );
    }
}

