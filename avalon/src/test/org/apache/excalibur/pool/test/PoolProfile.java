/*
 * Copyright  The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool.test;

import org.apache.avalon.Poolable;
import org.apache.excalibur.pool.DefaultPool;
import org.apache.excalibur.pool.Pool;
import org.apache.excalibur.pool.HardResourceLimitingPool;
import org.apache.testlet.*;

/**
 * This is used to profile the Pool implementation.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class PoolProfile
    extends AbstractTestlet
{
    public static class A
        implements Poolable
    {
        int a;
        int b;
        int c;
        float x;
        float y;
        float z;
    }

    public static class B
        implements Poolable
    {
        int a;
        int b;
        int c;
        float x;
        float y;
        float z;
        Object o1;
        Object o2;
        Object o3;

        public void recycle()
        {
            o1 = o2 = o3 = null;
        }
    }

    public static class C
        implements Poolable
    {
        int a;
        int b;
        int c;
        float x;
        float y;
        float z;
        Object o1;
        Object o2;
        Object o3;
        Object o4;
        Object o5;
        Object o6;

        public void build()
        {
            o1 = new Object();
            o2 = new Object();
            o3 = new Object();
            o4 = new Object();
            o5 = new Object();
            o6 = new Object();
        }

        public void recycle()
        {
            o1 = o2 = o3 = o4 = o5 = o6 = null;
        }
    }

    protected static final int               TEST_SIZE          = 1000000;

    public void testSmallObjects()
        throws Exception
    {
        System.out.println("SMALL Sized Objects");

        final DefaultPool pool1 = new DefaultPool( A.class, 5, 10 );
        final long pool1Start = System.currentTimeMillis();
        final int pool1Factor = 1;
        final int pool1Loops = TEST_SIZE / pool1Factor;
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = pool1.get();
            pool1.put(a1);
        }
        final long pool1End = System.currentTimeMillis();
        final long pool1Duration = pool1End - pool1Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 1: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool2 = new DefaultPool( A.class, 5, 10 );
        final long pool2Start = System.currentTimeMillis();
        final int pool2Factor = 10;
        final int pool2Loops = TEST_SIZE / pool2Factor;
        for( int i = 0; i < pool2Loops; i++ )
        {
            final Poolable a1 = pool2.get();
            final Poolable a2 = pool2.get();
            final Poolable a3 = pool2.get();
            final Poolable a4 = pool2.get();
            final Poolable a5 = pool2.get();
            final Poolable a6 = pool2.get();
            final Poolable a7 = pool2.get();
            final Poolable a8 = pool2.get();
            final Poolable a9 = pool2.get();
            final Poolable a0 = pool2.get();
            pool2.put(a1);
            pool2.put(a2);
            pool2.put(a3);
            pool2.put(a4);
            pool2.put(a5);
            pool2.put(a6);
            pool2.put(a7);
            pool2.put(a8);
            pool2.put(a9);
            pool2.put(a0);
        }
        final long pool2End = System.currentTimeMillis();
        final long pool2Duration = pool2End - pool2Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 2: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool3 = new DefaultPool( A.class, 5, 10 );
        final long pool3Start = System.currentTimeMillis();
        final int pool3Factor = 15;
        final int pool3Loops = TEST_SIZE / pool3Factor;
        for( int i = 0; i < pool3Loops; i++ )
        {
            final Poolable a1 = pool3.get();
            final Poolable a2 = pool3.get();
            final Poolable a3 = pool3.get();
            final Poolable a4 = pool3.get();
            final Poolable a5 = pool3.get();
            final Poolable a6 = pool3.get();
            final Poolable a7 = pool3.get();
            final Poolable a8 = pool3.get();
            final Poolable a9 = pool3.get();
            final Poolable a10 = pool3.get();
            final Poolable a11 = pool3.get();
            final Poolable a12 = pool3.get();
            final Poolable a13 = pool3.get();
            final Poolable a14 = pool3.get();
            final Poolable a15 = pool3.get();
            pool3.put(a1);
            pool3.put(a2);
            pool3.put(a3);
            pool3.put(a4);
            pool3.put(a5);
            pool3.put(a6);
            pool3.put(a7);
            pool3.put(a8);
            pool3.put(a9);
            pool3.put(a10);
            pool3.put(a11);
            pool3.put(a12);
            pool3.put(a13);
            pool3.put(a14);
            pool3.put(a15);
        }
        final long pool3End = System.currentTimeMillis();
        final long pool3Duration = pool3End - pool3Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 3: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool4 = new DefaultPool( A.class, 5, 10 );
        final long pool4Start = System.currentTimeMillis();
        final int pool4Factor = 20;
        final int pool4Loops = TEST_SIZE / pool4Factor;
        for( int i = 0; i < pool4Loops; i++ )
        {
            final Poolable a1 = pool4.get();
            final Poolable a2 = pool4.get();
            final Poolable a3 = pool4.get();
            final Poolable a4 = pool4.get();
            final Poolable a5 = pool4.get();
            final Poolable a6 = pool4.get();
            final Poolable a7 = pool4.get();
            final Poolable a8 = pool4.get();
            final Poolable a9 = pool4.get();
            final Poolable a10 = pool4.get();
            final Poolable a11 = pool4.get();
            final Poolable a12 = pool4.get();
            final Poolable a13 = pool4.get();
            final Poolable a14 = pool4.get();
            final Poolable a15 = pool4.get();
            final Poolable a16 = pool4.get();
            final Poolable a17 = pool4.get();
            final Poolable a18 = pool4.get();
            final Poolable a19 = pool4.get();
            final Poolable a20 = pool4.get();
            pool4.put(a1);
            pool4.put(a2);
            pool4.put(a3);
            pool4.put(a4);
            pool4.put(a5);
            pool4.put(a6);
            pool4.put(a7);
            pool4.put(a8);
            pool4.put(a9);
            pool4.put(a10);
            pool4.put(a11);
            pool4.put(a12);
            pool4.put(a13);
            pool4.put(a14);
            pool4.put(a15);
            pool4.put(a16);
            pool4.put(a17);
            pool4.put(a18);
            pool4.put(a19);
            pool4.put(a20);
        }
        final long pool4End = System.currentTimeMillis();
        final long pool4Duration = pool4End - pool4Start;

        System.out.println("FreeMem post 4: " + Runtime.getRuntime().freeMemory() );

        final long createStart = System.currentTimeMillis();
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = new C();
        }
        final long createEnd = System.currentTimeMillis();
        final long createDuration = createEnd - createStart;

        //System.out.println("Create Duration: " + createDuration + "ms ");
        System.out.println("FreeMem post create: " + Runtime.getRuntime().freeMemory() );

        final double pool1Efficiancy = (double)createDuration/(double)pool1Duration * 100.0;
        final double pool2Efficiancy = (double)createDuration/(double)pool2Duration * 100.0;
        final double pool3Efficiancy = (double)createDuration/(double)pool3Duration * 100.0;
        final double pool4Efficiancy = (double)createDuration/(double)pool4Duration * 100.0;

        //System.out.println("Pool Duration for 100% hits: " + pool1Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits: " + pool1Efficiancy + "ms ");
        //System.out.println("Pool Duration for 100% hits and saturated: " + pool2Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits and saturated: " + pool2Efficiancy + "ms ");
        //System.out.println("Pool Duration for 60% hits: " + pool3Duration + "ms ");
        System.out.println("Pool Efficiancy for 60% hits: " + pool3Efficiancy + "ms ");
        //System.out.println("Pool Duration for 50% hits: " + pool4Duration + "ms ");
        System.out.println("Pool Efficiancy for 50% hits: " + pool4Efficiancy + "ms ");
    }

    public void testMediumObjects()
        throws Exception
    {
        System.out.println("MEDIUM Sized Objects");

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        final DefaultPool pool1 = new DefaultPool( B.class, 5, 10 );
        final long pool1Start = System.currentTimeMillis();
        final int pool1Factor = 1;
        final int pool1Loops = TEST_SIZE / pool1Factor;
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = pool1.get();
            pool1.put(a1);
        }
        final long pool1End = System.currentTimeMillis();
        final long pool1Duration = pool1End - pool1Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 1: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool2 = new DefaultPool( B.class, 5, 10 );
        final long pool2Start = System.currentTimeMillis();
        final int pool2Factor = 10;
        final int pool2Loops = TEST_SIZE / pool2Factor;
        for( int i = 0; i < pool2Loops; i++ )
        {
            final Poolable a1 = pool2.get();
            final Poolable a2 = pool2.get();
            final Poolable a3 = pool2.get();
            final Poolable a4 = pool2.get();
            final Poolable a5 = pool2.get();
            final Poolable a6 = pool2.get();
            final Poolable a7 = pool2.get();
            final Poolable a8 = pool2.get();
            final Poolable a9 = pool2.get();
            final Poolable a10 = pool2.get();
            pool2.put(a1);
            pool2.put(a2);
            pool2.put(a3);
            pool2.put(a4);
            pool2.put(a5);
            pool2.put(a6);
            pool2.put(a7);
            pool2.put(a8);
            pool2.put(a9);
            pool2.put(a10);
        }
        final long pool2End = System.currentTimeMillis();
        final long pool2Duration = pool2End - pool2Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 2: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool3 = new DefaultPool( B.class, 5, 10 );
        final long pool3Start = System.currentTimeMillis();
        final int pool3Factor = 15;
        final int pool3Loops = TEST_SIZE / pool3Factor;
        for( int i = 0; i < pool3Loops; i++ )
        {
            final Poolable a1 = pool3.get();
            final Poolable a2 = pool3.get();
            final Poolable a3 = pool3.get();
            final Poolable a4 = pool3.get();
            final Poolable a5 = pool3.get();
            final Poolable a6 = pool3.get();
            final Poolable a7 = pool3.get();
            final Poolable a8 = pool3.get();
            final Poolable a9 = pool3.get();
            final Poolable a10 = pool3.get();
            final Poolable a11 = pool3.get();
            final Poolable a12 = pool3.get();
            final Poolable a13 = pool3.get();
            final Poolable a14 = pool3.get();
            final Poolable a15 = pool3.get();
            pool3.put(a1);
            pool3.put(a2);
            pool3.put(a3);
            pool3.put(a4);
            pool3.put(a5);
            pool3.put(a6);
            pool3.put(a7);
            pool3.put(a8);
            pool3.put(a9);
            pool3.put(a10);
            pool3.put(a11);
            pool3.put(a12);
            pool3.put(a13);
            pool3.put(a14);
            pool3.put(a15);
        }
        final long pool3End = System.currentTimeMillis();
        final long pool3Duration = pool3End - pool3Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 3: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool4 = new DefaultPool( B.class, 5, 10 );
        final long pool4Start = System.currentTimeMillis();
        final int pool4Factor = 20;
        final int pool4Loops = TEST_SIZE / pool4Factor;
        for( int i = 0; i < pool4Loops; i++ )
        {
            final Poolable a1 = pool4.get();
            final Poolable a2 = pool4.get();
            final Poolable a3 = pool4.get();
            final Poolable a4 = pool4.get();
            final Poolable a5 = pool4.get();
            final Poolable a6 = pool4.get();
            final Poolable a7 = pool4.get();
            final Poolable a8 = pool4.get();
            final Poolable a9 = pool4.get();
            final Poolable a10 = pool4.get();
            final Poolable a11 = pool4.get();
            final Poolable a12 = pool4.get();
            final Poolable a13 = pool4.get();
            final Poolable a14 = pool4.get();
            final Poolable a15 = pool4.get();
            final Poolable a16 = pool4.get();
            final Poolable a17 = pool4.get();
            final Poolable a18 = pool4.get();
            final Poolable a19 = pool4.get();
            final Poolable a20 = pool4.get();
            pool4.put(a1);
            pool4.put(a2);
            pool4.put(a3);
            pool4.put(a4);
            pool4.put(a5);
            pool4.put(a6);
            pool4.put(a7);
            pool4.put(a8);
            pool4.put(a9);
            pool4.put(a10);
            pool4.put(a11);
            pool4.put(a12);
            pool4.put(a13);
            pool4.put(a14);
            pool4.put(a15);
            pool4.put(a16);
            pool4.put(a17);
            pool4.put(a18);
            pool4.put(a19);
            pool4.put(a20);
        }
        final long pool4End = System.currentTimeMillis();
        final long pool4Duration = pool4End - pool4Start;

        final long createStart = System.currentTimeMillis();
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = new C();
        }
        final long createEnd = System.currentTimeMillis();
        final long createDuration = createEnd - createStart;

        //System.out.println("Create Duration: " + createDuration + "ms ");
        System.out.println("FreeMem post create: " + Runtime.getRuntime().freeMemory() );

        final double pool1Efficiancy = (double)createDuration/(double)pool1Duration * 100.0;
        final double pool2Efficiancy = (double)createDuration/(double)pool2Duration * 100.0;
        final double pool3Efficiancy = (double)createDuration/(double)pool3Duration * 100.0;
        final double pool4Efficiancy = (double)createDuration/(double)pool4Duration * 100.0;

        //System.out.println("Pool Duration for 100% hits: " + pool1Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits: " + pool1Efficiancy + "ms ");
        //System.out.println("Pool Duration for 100% hits and saturated: " + pool2Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits and saturated: " + pool2Efficiancy + "ms ");
        //System.out.println("Pool Duration for 60% hits: " + pool3Duration + "ms ");
        System.out.println("Pool Efficiancy for 60% hits: " + pool3Efficiancy + "ms ");
        //System.out.println("Pool Duration for 50% hits: " + pool4Duration + "ms ");
        System.out.println("Pool Efficiancy for 50% hits: " + pool4Efficiancy + "ms ");
    }

    public void testLargeObjects()
        throws Exception
    {
        System.out.println("LARGE Sized Objects");

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        final DefaultPool pool1 = new DefaultPool( C.class, 5, 10 );
        final long pool1Start = System.currentTimeMillis();
        final int pool1Factor = 1;
        final int pool1Loops = TEST_SIZE / pool1Factor;
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = pool1.get();
            pool1.put(a1);
        }
        final long pool1End = System.currentTimeMillis();
        final long pool1Duration = pool1End - pool1Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 1: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool2 = new DefaultPool( C.class, 5, 10 );
        final long pool2Start = System.currentTimeMillis();
        final int pool2Factor = 10;
        final int pool2Loops = TEST_SIZE / pool2Factor;
        for( int i = 0; i < pool2Loops; i++ )
        {
            final Poolable a1 = pool2.get();
            final Poolable a2 = pool2.get();
            final Poolable a3 = pool2.get();
            final Poolable a4 = pool2.get();
            final Poolable a5 = pool2.get();
            final Poolable a6 = pool2.get();
            final Poolable a7 = pool2.get();
            final Poolable a8 = pool2.get();
            final Poolable a9 = pool2.get();
            final Poolable a10 = pool2.get();
            pool2.put(a1);
            pool2.put(a2);
            pool2.put(a3);
            pool2.put(a4);
            pool2.put(a5);
            pool2.put(a6);
            pool2.put(a7);
            pool2.put(a8);
            pool2.put(a9);
            pool2.put(a10);
        }
        final long pool2End = System.currentTimeMillis();
        final long pool2Duration = pool2End - pool2Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 2: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool3 = new DefaultPool( C.class, 5, 10 );
        final long pool3Start = System.currentTimeMillis();
        final int pool3Factor = 15;
        final int pool3Loops = TEST_SIZE / pool3Factor;
        for( int i = 0; i < pool3Loops; i++ )
        {
            final Poolable a1 = pool3.get();
            final Poolable a2 = pool3.get();
            final Poolable a3 = pool3.get();
            final Poolable a4 = pool3.get();
            final Poolable a5 = pool3.get();
            final Poolable a6 = pool3.get();
            final Poolable a7 = pool3.get();
            final Poolable a8 = pool3.get();
            final Poolable a9 = pool3.get();
            final Poolable a10 = pool3.get();
            final Poolable a11 = pool3.get();
            final Poolable a12 = pool3.get();
            final Poolable a13 = pool3.get();
            final Poolable a14 = pool3.get();
            final Poolable a15 = pool3.get();
            pool3.put(a1);
            pool3.put(a2);
            pool3.put(a3);
            pool3.put(a4);
            pool3.put(a5);
            pool3.put(a6);
            pool3.put(a7);
            pool3.put(a8);
            pool3.put(a9);
            pool3.put(a10);
            pool3.put(a11);
            pool3.put(a12);
            pool3.put(a13);
            pool3.put(a14);
            pool3.put(a15);
        }
        final long pool3End = System.currentTimeMillis();
        final long pool3Duration = pool3End - pool3Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 3: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool4 = new DefaultPool( C.class, 5, 10 );
        final long pool4Start = System.currentTimeMillis();
        final int pool4Factor = 20;
        final int pool4Loops = TEST_SIZE / pool4Factor;
        for( int i = 0; i < pool4Loops; i++ )
        {
            final Poolable a1 = pool4.get();
            final Poolable a2 = pool4.get();
            final Poolable a3 = pool4.get();
            final Poolable a4 = pool4.get();
            final Poolable a5 = pool4.get();
            final Poolable a6 = pool4.get();
            final Poolable a7 = pool4.get();
            final Poolable a8 = pool4.get();
            final Poolable a9 = pool4.get();
            final Poolable a10 = pool4.get();
            final Poolable a11 = pool4.get();
            final Poolable a12 = pool4.get();
            final Poolable a13 = pool4.get();
            final Poolable a14 = pool4.get();
            final Poolable a15 = pool4.get();
            final Poolable a16 = pool4.get();
            final Poolable a17 = pool4.get();
            final Poolable a18 = pool4.get();
            final Poolable a19 = pool4.get();
            final Poolable a20 = pool4.get();
            pool4.put(a1);
            pool4.put(a2);
            pool4.put(a3);
            pool4.put(a4);
            pool4.put(a5);
            pool4.put(a6);
            pool4.put(a7);
            pool4.put(a8);
            pool4.put(a9);
            pool4.put(a10);
            pool4.put(a11);
            pool4.put(a12);
            pool4.put(a13);
            pool4.put(a14);
            pool4.put(a15);
            pool4.put(a16);
            pool4.put(a17);
            pool4.put(a18);
            pool4.put(a19);
            pool4.put(a20);
        }
        final long pool4End = System.currentTimeMillis();
        final long pool4Duration = pool4End - pool4Start;

        System.out.println("FreeMem post 4: " + Runtime.getRuntime().freeMemory() );

        final long createStart = System.currentTimeMillis();
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = new C();
        }
        final long createEnd = System.currentTimeMillis();
        final long createDuration = createEnd - createStart;

        System.out.println("FreeMem post create: " + Runtime.getRuntime().freeMemory() );

        final double pool1Efficiancy = (double)createDuration/(double)pool1Duration * 100.0;
        final double pool2Efficiancy = (double)createDuration/(double)pool2Duration * 100.0;
        final double pool3Efficiancy = (double)createDuration/(double)pool3Duration * 100.0;
        final double pool4Efficiancy = (double)createDuration/(double)pool4Duration * 100.0;

        System.out.println("Pool Efficiancy for 100% hits: " + pool1Efficiancy + "ms ");
        System.out.println("Pool Efficiancy for 100% hits and saturated: " + pool2Efficiancy + "ms ");
        System.out.println("Pool Efficiancy for 60% hits: " + pool3Efficiancy + "ms ");
        System.out.println("Pool Efficiancy for 50% hits: " + pool4Efficiancy + "ms ");
    }


    public void testThreadedSmallObjects()
        throws Exception
    {
        System.out.println("SMALL Sized Objects with thread safe pools");

        final HardResourceLimitingPool pool1 = new HardResourceLimitingPool( A.class, 5, 10 );
        final long pool1Start = System.currentTimeMillis();
        final int pool1Factor = 1;
        final int pool1Loops = TEST_SIZE / pool1Factor;
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = pool1.get();
            pool1.put(a1);
        }
        final long pool1End = System.currentTimeMillis();
        final long pool1Duration = pool1End - pool1Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 1: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool2 = new HardResourceLimitingPool( A.class, 5, 10 );
        final long pool2Start = System.currentTimeMillis();
        final int pool2Factor = 10;
        final int pool2Loops = TEST_SIZE / pool2Factor;
        for( int i = 0; i < pool2Loops; i++ )
        {
            final Poolable a1 = pool2.get();
            final Poolable a2 = pool2.get();
            final Poolable a3 = pool2.get();
            final Poolable a4 = pool2.get();
            final Poolable a5 = pool2.get();
            final Poolable a6 = pool2.get();
            final Poolable a7 = pool2.get();
            final Poolable a8 = pool2.get();
            final Poolable a9 = pool2.get();
            final Poolable a0 = pool2.get();
            pool2.put(a1);
            pool2.put(a2);
            pool2.put(a3);
            pool2.put(a4);
            pool2.put(a5);
            pool2.put(a6);
            pool2.put(a7);
            pool2.put(a8);
            pool2.put(a9);
            pool2.put(a0);
        }
        final long pool2End = System.currentTimeMillis();
        final long pool2Duration = pool2End - pool2Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 2: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool3 = new HardResourceLimitingPool( A.class, 5, 10 );
        final long pool3Start = System.currentTimeMillis();
        final int pool3Factor = 15;
        final int pool3Loops = TEST_SIZE / pool3Factor;
        for( int i = 0; i < pool3Loops; i++ )
        {
            final Poolable a1 = pool3.get();
            final Poolable a2 = pool3.get();
            final Poolable a3 = pool3.get();
            final Poolable a4 = pool3.get();
            final Poolable a5 = pool3.get();
            final Poolable a6 = pool3.get();
            final Poolable a7 = pool3.get();
            final Poolable a8 = pool3.get();
            final Poolable a9 = pool3.get();
            final Poolable a10 = pool3.get();
            final Poolable a11 = pool3.get();
            final Poolable a12 = pool3.get();
            final Poolable a13 = pool3.get();
            final Poolable a14 = pool3.get();
            final Poolable a15 = pool3.get();
            pool3.put(a1);
            pool3.put(a2);
            pool3.put(a3);
            pool3.put(a4);
            pool3.put(a5);
            pool3.put(a6);
            pool3.put(a7);
            pool3.put(a8);
            pool3.put(a9);
            pool3.put(a10);
            pool3.put(a11);
            pool3.put(a12);
            pool3.put(a13);
            pool3.put(a14);
            pool3.put(a15);
        }
        final long pool3End = System.currentTimeMillis();
        final long pool3Duration = pool3End - pool3Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 3: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool4 = new HardResourceLimitingPool( A.class, 5, 10 );
        final long pool4Start = System.currentTimeMillis();
        final int pool4Factor = 20;
        final int pool4Loops = TEST_SIZE / pool4Factor;
        for( int i = 0; i < pool4Loops; i++ )
        {
            final Poolable a1 = pool4.get();
            final Poolable a2 = pool4.get();
            final Poolable a3 = pool4.get();
            final Poolable a4 = pool4.get();
            final Poolable a5 = pool4.get();
            final Poolable a6 = pool4.get();
            final Poolable a7 = pool4.get();
            final Poolable a8 = pool4.get();
            final Poolable a9 = pool4.get();
            final Poolable a10 = pool4.get();
            final Poolable a11 = pool4.get();
            final Poolable a12 = pool4.get();
            final Poolable a13 = pool4.get();
            final Poolable a14 = pool4.get();
            final Poolable a15 = pool4.get();
            final Poolable a16 = pool4.get();
            final Poolable a17 = pool4.get();
            final Poolable a18 = pool4.get();
            final Poolable a19 = pool4.get();
            final Poolable a20 = pool4.get();
            pool4.put(a1);
            pool4.put(a2);
            pool4.put(a3);
            pool4.put(a4);
            pool4.put(a5);
            pool4.put(a6);
            pool4.put(a7);
            pool4.put(a8);
            pool4.put(a9);
            pool4.put(a10);
            pool4.put(a11);
            pool4.put(a12);
            pool4.put(a13);
            pool4.put(a14);
            pool4.put(a15);
            pool4.put(a16);
            pool4.put(a17);
            pool4.put(a18);
            pool4.put(a19);
            pool4.put(a20);
        }
        final long pool4End = System.currentTimeMillis();
        final long pool4Duration = pool4End - pool4Start;

        System.out.println("FreeMem post 4: " + Runtime.getRuntime().freeMemory() );

        final long createStart = System.currentTimeMillis();
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = new C();
        }
        final long createEnd = System.currentTimeMillis();
        final long createDuration = createEnd - createStart;

        //System.out.println("Create Duration: " + createDuration + "ms ");
        System.out.println("FreeMem post create: " + Runtime.getRuntime().freeMemory() );

        final double pool1Efficiancy = (double)createDuration/(double)pool1Duration * 100.0;
        final double pool2Efficiancy = (double)createDuration/(double)pool2Duration * 100.0;
        final double pool3Efficiancy = (double)createDuration/(double)pool3Duration * 100.0;
        final double pool4Efficiancy = (double)createDuration/(double)pool4Duration * 100.0;

        //System.out.println("Pool Duration for 100% hits: " + pool1Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits: " + pool1Efficiancy + "ms ");
        //System.out.println("Pool Duration for 100% hits and saturated: " + pool2Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits and saturated: " + pool2Efficiancy + "ms ");
        //System.out.println("Pool Duration for 60% hits: " + pool3Duration + "ms ");
        System.out.println("Pool Efficiancy for 60% hits: " + pool3Efficiancy + "ms ");
        //System.out.println("Pool Duration for 50% hits: " + pool4Duration + "ms ");
        System.out.println("Pool Efficiancy for 50% hits: " + pool4Efficiancy + "ms ");
    }

    public void testThreadedMediumObjects()
        throws Exception
    {
        System.out.println("MEDIUM Sized Objects with thread safe pools");

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        final HardResourceLimitingPool pool1 = new HardResourceLimitingPool( B.class, 5, 10 );
        final long pool1Start = System.currentTimeMillis();
        final int pool1Factor = 1;
        final int pool1Loops = TEST_SIZE / pool1Factor;
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = pool1.get();
            pool1.put(a1);
        }
        final long pool1End = System.currentTimeMillis();
        final long pool1Duration = pool1End - pool1Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 1: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool2 = new HardResourceLimitingPool( B.class, 5, 10 );
        final long pool2Start = System.currentTimeMillis();
        final int pool2Factor = 10;
        final int pool2Loops = TEST_SIZE / pool2Factor;
        for( int i = 0; i < pool2Loops; i++ )
        {
            final Poolable a1 = pool2.get();
            final Poolable a2 = pool2.get();
            final Poolable a3 = pool2.get();
            final Poolable a4 = pool2.get();
            final Poolable a5 = pool2.get();
            final Poolable a6 = pool2.get();
            final Poolable a7 = pool2.get();
            final Poolable a8 = pool2.get();
            final Poolable a9 = pool2.get();
            final Poolable a10 = pool2.get();
            /*
              a1.build();
              a2.build();
              a3.build();
              a4.build();
              a5.build();
              a6.build();
              a7.build();
              a8.build();
              a9.build();
              a10.build();
            */
            pool2.put(a1);
            pool2.put(a2);
            pool2.put(a3);
            pool2.put(a4);
            pool2.put(a5);
            pool2.put(a6);
            pool2.put(a7);
            pool2.put(a8);
            pool2.put(a9);
            pool2.put(a10);
        }
        final long pool2End = System.currentTimeMillis();
        final long pool2Duration = pool2End - pool2Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 2: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool3 = new HardResourceLimitingPool( B.class, 5, 10 );
        final long pool3Start = System.currentTimeMillis();
        final int pool3Factor = 15;
        final int pool3Loops = TEST_SIZE / pool3Factor;
        for( int i = 0; i < pool3Loops; i++ )
        {
            final Poolable a1 = pool3.get();
            final Poolable a2 = pool3.get();
            final Poolable a3 = pool3.get();
            final Poolable a4 = pool3.get();
            final Poolable a5 = pool3.get();
            final Poolable a6 = pool3.get();
            final Poolable a7 = pool3.get();
            final Poolable a8 = pool3.get();
            final Poolable a9 = pool3.get();
            final Poolable a10 = pool3.get();
            final Poolable a11 = pool3.get();
            final Poolable a12 = pool3.get();
            final Poolable a13 = pool3.get();
            final Poolable a14 = pool3.get();
            final Poolable a15 = pool3.get();
            /*
              a1.build();
              a2.build();
              a3.build();
              a4.build();
              a5.build();
              a6.build();
              a7.build();
              a8.build();
              a9.build();
              a10.build();
              a11.build();
              a12.build();
              a13.build();
              a14.build();
              a15.build();
            */
            pool3.put(a1);
            pool3.put(a2);
            pool3.put(a3);
            pool3.put(a4);
            pool3.put(a5);
            pool3.put(a6);
            pool3.put(a7);
            pool3.put(a8);
            pool3.put(a9);
            pool3.put(a10);
            pool3.put(a11);
            pool3.put(a12);
            pool3.put(a13);
            pool3.put(a14);
            pool3.put(a15);
        }
        final long pool3End = System.currentTimeMillis();
        final long pool3Duration = pool3End - pool3Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 3: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool4 = new HardResourceLimitingPool( B.class, 5, 10 );
        final long pool4Start = System.currentTimeMillis();
        final int pool4Factor = 20;
        final int pool4Loops = TEST_SIZE / pool4Factor;
        for( int i = 0; i < pool4Loops; i++ )
        {
            final Poolable a1 = pool4.get();
            final Poolable a2 = pool4.get();
            final Poolable a3 = pool4.get();
            final Poolable a4 = pool4.get();
            final Poolable a5 = pool4.get();
            final Poolable a6 = pool4.get();
            final Poolable a7 = pool4.get();
            final Poolable a8 = pool4.get();
            final Poolable a9 = pool4.get();
            final Poolable a10 = pool4.get();
            final Poolable a11 = pool4.get();
            final Poolable a12 = pool4.get();
            final Poolable a13 = pool4.get();
            final Poolable a14 = pool4.get();
            final Poolable a15 = pool4.get();
            final Poolable a16 = pool4.get();
            final Poolable a17 = pool4.get();
            final Poolable a18 = pool4.get();
            final Poolable a19 = pool4.get();
            final Poolable a20 = pool4.get();
            /*
              a1.build();
              a2.build();
              a3.build();
              a4.build();
              a5.build();
              a6.build();
              a7.build();
              a8.build();
              a9.build();
              a10.build();
              a11.build();
              a12.build();
              a13.build();
              a14.build();
              a15.build();
              a16.build();
              a17.build();
              a18.build();
              a19.build();
              a20.build();
            */
            pool4.put(a1);
            pool4.put(a2);
            pool4.put(a3);
            pool4.put(a4);
            pool4.put(a5);
            pool4.put(a6);
            pool4.put(a7);
            pool4.put(a8);
            pool4.put(a9);
            pool4.put(a10);
            pool4.put(a11);
            pool4.put(a12);
            pool4.put(a13);
            pool4.put(a14);
            pool4.put(a15);
            pool4.put(a16);
            pool4.put(a17);
            pool4.put(a18);
            pool4.put(a19);
            pool4.put(a20);
        }
        final long pool4End = System.currentTimeMillis();
        final long pool4Duration = pool4End - pool4Start;

        final long createStart = System.currentTimeMillis();
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = new C();
        }
        final long createEnd = System.currentTimeMillis();
        final long createDuration = createEnd - createStart;

        //System.out.println("Create Duration: " + createDuration + "ms ");
        System.out.println("FreeMem post create: " + Runtime.getRuntime().freeMemory() );

        final double pool1Efficiancy = (double)createDuration/(double)pool1Duration * 100.0;
        final double pool2Efficiancy = (double)createDuration/(double)pool2Duration * 100.0;
        final double pool3Efficiancy = (double)createDuration/(double)pool3Duration * 100.0;
        final double pool4Efficiancy = (double)createDuration/(double)pool4Duration * 100.0;

        //System.out.println("Pool Duration for 100% hits: " + pool1Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits: " + pool1Efficiancy + "ms ");
        //System.out.println("Pool Duration for 100% hits and saturated: " + pool2Duration + "ms ");
        System.out.println("Pool Efficiancy for 100% hits and saturated: " + pool2Efficiancy + "ms ");
        //System.out.println("Pool Duration for 60% hits: " + pool3Duration + "ms ");
        System.out.println("Pool Efficiancy for 60% hits: " + pool3Efficiancy + "ms ");
        //System.out.println("Pool Duration for 50% hits: " + pool4Duration + "ms ");
        System.out.println("Pool Efficiancy for 50% hits: " + pool4Efficiancy + "ms ");
    }

    public void testThreadedLargeObjects()
        throws Exception
    {
        System.out.println("LARGE Sized Objects with thread safe pools");

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        final HardResourceLimitingPool pool1 = new HardResourceLimitingPool( C.class, 5, 10 );
        final long pool1Start = System.currentTimeMillis();
        final int pool1Factor = 1;
        final int pool1Loops = TEST_SIZE / pool1Factor;
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = pool1.get();
            //a1.build();
            pool1.put(a1);
        }
        final long pool1End = System.currentTimeMillis();
        final long pool1Duration = pool1End - pool1Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 1: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool2 = new HardResourceLimitingPool( C.class, 5, 10 );
        final long pool2Start = System.currentTimeMillis();
        final int pool2Factor = 10;
        final int pool2Loops = TEST_SIZE / pool2Factor;
        for( int i = 0; i < pool2Loops; i++ )
        {
            final Poolable a1 = pool2.get();
            final Poolable a2 = pool2.get();
            final Poolable a3 = pool2.get();
            final Poolable a4 = pool2.get();
            final Poolable a5 = pool2.get();
            final Poolable a6 = pool2.get();
            final Poolable a7 = pool2.get();
            final Poolable a8 = pool2.get();
            final Poolable a9 = pool2.get();
            final Poolable a10 = pool2.get();
            /*
              a1.build();
              a2.build();
              a3.build();
              a4.build();
              a5.build();
              a6.build();
              a7.build();
              a8.build();
              a9.build();
              a10.build();
            */
            pool2.put(a1);
            pool2.put(a2);
            pool2.put(a3);
            pool2.put(a4);
            pool2.put(a5);
            pool2.put(a6);
            pool2.put(a7);
            pool2.put(a8);
            pool2.put(a9);
            pool2.put(a10);
        }
        final long pool2End = System.currentTimeMillis();
        final long pool2Duration = pool2End - pool2Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 2: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool3 = new HardResourceLimitingPool( C.class, 5, 10 );
        final long pool3Start = System.currentTimeMillis();
        final int pool3Factor = 15;
        final int pool3Loops = TEST_SIZE / pool3Factor;
        for( int i = 0; i < pool3Loops; i++ )
        {
            final Poolable a1 = pool3.get();
            final Poolable a2 = pool3.get();
            final Poolable a3 = pool3.get();
            final Poolable a4 = pool3.get();
            final Poolable a5 = pool3.get();
            final Poolable a6 = pool3.get();
            final Poolable a7 = pool3.get();
            final Poolable a8 = pool3.get();
            final Poolable a9 = pool3.get();
            final Poolable a10 = pool3.get();
            final Poolable a11 = pool3.get();
            final Poolable a12 = pool3.get();
            final Poolable a13 = pool3.get();
            final Poolable a14 = pool3.get();
            final Poolable a15 = pool3.get();
            /*
              a1.build();
              a2.build();
              a3.build();
              a4.build();
              a5.build();
              a6.build();
              a7.build();
              a8.build();
              a9.build();
              a10.build();
              a11.build();
              a12.build();
              a13.build();
              a14.build();
              a15.build();
            */
            pool3.put(a1);
            pool3.put(a2);
            pool3.put(a3);
            pool3.put(a4);
            pool3.put(a5);
            pool3.put(a6);
            pool3.put(a7);
            pool3.put(a8);
            pool3.put(a9);
            pool3.put(a10);
            pool3.put(a11);
            pool3.put(a12);
            pool3.put(a13);
            pool3.put(a14);
            pool3.put(a15);
        }
        final long pool3End = System.currentTimeMillis();
        final long pool3Duration = pool3End - pool3Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep(2);

        System.out.println("FreeMem post 3: " + Runtime.getRuntime().freeMemory() );

        final HardResourceLimitingPool pool4 = new HardResourceLimitingPool( C.class, 5, 10 );
        final long pool4Start = System.currentTimeMillis();
        final int pool4Factor = 20;
        final int pool4Loops = TEST_SIZE / pool4Factor;
        for( int i = 0; i < pool4Loops; i++ )
        {
            final Poolable a1 = pool4.get();
            final Poolable a2 = pool4.get();
            final Poolable a3 = pool4.get();
            final Poolable a4 = pool4.get();
            final Poolable a5 = pool4.get();
            final Poolable a6 = pool4.get();
            final Poolable a7 = pool4.get();
            final Poolable a8 = pool4.get();
            final Poolable a9 = pool4.get();
            final Poolable a10 = pool4.get();
            final Poolable a11 = pool4.get();
            final Poolable a12 = pool4.get();
            final Poolable a13 = pool4.get();
            final Poolable a14 = pool4.get();
            final Poolable a15 = pool4.get();
            final Poolable a16 = pool4.get();
            final Poolable a17 = pool4.get();
            final Poolable a18 = pool4.get();
            final Poolable a19 = pool4.get();
            final Poolable a20 = pool4.get();
            pool4.put(a1);
            pool4.put(a2);
            pool4.put(a3);
            pool4.put(a4);
            pool4.put(a5);
            pool4.put(a6);
            pool4.put(a7);
            pool4.put(a8);
            pool4.put(a9);
            pool4.put(a10);
            pool4.put(a11);
            pool4.put(a12);
            pool4.put(a13);
            pool4.put(a14);
            pool4.put(a15);
            pool4.put(a16);
            pool4.put(a17);
            pool4.put(a18);
            pool4.put(a19);
            pool4.put(a20);
        }
        final long pool4End = System.currentTimeMillis();
        final long pool4Duration = pool4End - pool4Start;

        System.out.println("FreeMem post 4: " + Runtime.getRuntime().freeMemory() );

        final long createStart = System.currentTimeMillis();
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = new C();
        }
        final long createEnd = System.currentTimeMillis();
        final long createDuration = createEnd - createStart;

        System.out.println("FreeMem post create: " + Runtime.getRuntime().freeMemory() );

        final double pool1Efficiancy = (double)createDuration/(double)pool1Duration * 100.0;
        final double pool2Efficiancy = (double)createDuration/(double)pool2Duration * 100.0;
        final double pool3Efficiancy = (double)createDuration/(double)pool3Duration * 100.0;
        final double pool4Efficiancy = (double)createDuration/(double)pool4Duration * 100.0;

        System.out.println("Pool Efficiancy for 100% hits: " + pool1Efficiancy + "ms ");
        System.out.println("Pool Efficiancy for 100% hits and saturated: " + pool2Efficiancy + "ms ");
        System.out.println("Pool Efficiancy for 60% hits: " + pool3Efficiancy + "ms ");
        System.out.println("Pool Efficiancy for 50% hits: " + pool4Efficiancy + "ms ");
    }
}
