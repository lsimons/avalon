/**
 * Created on Jun 20, 2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 */
package org.apache.avalon.excalibur.collections.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.collections.BucketMap;

/**
 * @author bloritsch
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class ThreadedMapTest extends TestCase
{
    private static final int ITERATIONS = 4000;
    private static final int THREADS = 50;

    private MapStart[] stages;

    public ThreadedMapTest( String name )
    {
        super( name );
    }
    
    public void setUp()
    {
        System.gc();
        try {Thread.sleep(100);} catch(Exception e){}
    }
    
    public void tearDown()
    {
        System.gc();
    }
    
    public void testBucketMapSizedThreaded() throws Exception
    {
        initialize( ITERATIONS, THREADS, new BucketMap( ITERATIONS / 2 ) );
        long start = System.currentTimeMillis();
        start();
        long end = System.currentTimeMillis();

        System.out.println("BucketMap (" + ITERATIONS / 2 + " buckets) took " + (end - start) + "ms");
        System.out.println("Thats " + ( (float)(end - start) / (float)(ITERATIONS * THREADS) ) + "ms per operation");
    }
    
    public void testBucketMapUnsizedThreaded() throws Exception
    {
        initialize( ITERATIONS, THREADS, new BucketMap() );
        long start = System.currentTimeMillis();
        start();
        long end = System.currentTimeMillis();

        System.out.println("Unsized BucketMap (256 buckets) took " + (end - start) + "ms");
        System.out.println("Thats " + ( (float)(end - start) / (float)(ITERATIONS * THREADS) ) + "ms per operation");
    }

    public void testSynchronizedHashMapSizedThreaded() throws Exception
    {
        initialize( ITERATIONS, THREADS, Collections.synchronizedMap(new HashMap(ITERATIONS / 2)) );
        long start = System.currentTimeMillis();
        start();
        long end = System.currentTimeMillis();

        System.out.println("Synchronized (sized) HashMap took " + (end - start) + "ms");
        System.out.println("Thats " + ( (float)(end - start) / (float)(ITERATIONS * THREADS) ) + "ms per operation");
    }

    public void testSynchronizedHashMapUnsizedThreaded() throws Exception
    {
        initialize( ITERATIONS, THREADS, Collections.synchronizedMap(new HashMap()) );
        long start = System.currentTimeMillis();
        start();
        long end = System.currentTimeMillis();

        System.out.println("Synchronized (unsized) HashMap took " + (end - start) + "ms");
        System.out.println("Thats " + ( (float)(end - start) / (float)(ITERATIONS * THREADS) ) + "ms per operation");
    }

    public void initialize( int count, int threads, Map map ) throws Exception
    {
        this.stages = new MapStart[ threads ];

        for (int i = 0; i < threads; i++)
        {
            this.stages[ i ] = new MapStart( count, map );
        }
    }

    public void start() throws Exception
    {
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
                assertEquals(ITERATIONS, this.stages[i].getCount());
            }
            catch( InterruptedException e )
            {
                throw new RuntimeException( "Stage unexpectedly interrupted: " + e );
            }
        }
    }

    private class MapStart extends Thread
    {
        private final Map map;
        private final int mapCount;
        private int count;

        public MapStart( int mapCount, Map newmap )
        {
            this.mapCount = mapCount;
            this.map = newmap;
        }

        public int getCount()
        {
            return count;
        }

        public void run()
        {
            int seed = 0;
            Random rnd = new Random(seed);

            for( int i = 0; i < this.mapCount / 4; i++ )
            {
                this.map.put(new Integer( i ), new Integer( i * 5 ) );
                this.count++;
                this.map.put(new Integer(rnd.nextInt()), new Integer(rnd.nextInt()) );
                this.count++;
            }

            rnd.setSeed(seed);
            
            for( int i = 0; i < this.mapCount / 4; i++ )
            {
                Integer value = (Integer)this.map.get( new Integer( i * 5 ));
                this.count++;
                value = (Integer)this.map.get( new Integer( rnd.nextInt() ));
                this.count++;
            }
        }
    }
}
