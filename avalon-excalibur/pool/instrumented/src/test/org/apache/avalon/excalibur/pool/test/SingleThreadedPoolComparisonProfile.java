/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
package org.apache.avalon.excalibur.pool.test;

import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.ResourceLimitingPool;
import org.apache.avalon.excalibur.pool.SingleThreadedPool;
import org.apache.avalon.framework.activity.Disposable;

/**
 * This is used to profile and compare various pool implementations
 *  given a single access thread.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version $Id: SingleThreadedPoolComparisonProfile.java,v 1.1 2003/11/09 14:44:01 leosimons Exp $
 */
public class SingleThreadedPoolComparisonProfile
    extends PoolComparisonProfileAbstract
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public SingleThreadedPoolComparisonProfile( String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * SingleThreadedPool vs ResourceLimitingPool TestCases
     *-------------------------------------------------------------*/
    /**
     * Compare the SingleThreadedPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SingleThreadedPool.
     * <p>
     * Test will use pools with a max size of 10, while getting up to 10 at a time,
     *  Poolables are small objects.
     */
    public void testCompare_SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets10_SmallPoolables()
        throws Exception
    {
        String name = "SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets10_SmallPoolables";

        Class poolableClass = SmallPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 10;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SingleThreadedPool poolA = new SingleThreadedPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 10, factory );
    }

    /**
     * Compare the SingleThreadedPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SingleThreadedPool.
     * <p>
     * Test will use pools with a max size of 10, while getting up to 20 at a time,
     *  Poolables are small objects.
     */
    public void testCompare_SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets20_SmallPoolables()
        throws Exception
    {
        String name = "SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets20_SmallPoolables";

        Class poolableClass = SmallPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 10;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SingleThreadedPool poolA = new SingleThreadedPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 20, factory );
    }

    /**
     * Compare the SingleThreadedPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SingleThreadedPool.
     * <p>
     * Test will use pools with a max size of 10, while getting up to 10 at a time,
     *  Poolables are medium objects.
     */
    public void testCompare_SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets10_MediumPoolables()
        throws Exception
    {
        String name = "SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets10_MediumPoolables";

        Class poolableClass = MediumPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 10;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SingleThreadedPool poolA = new SingleThreadedPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 10, factory );
    }

    /**
     * Compare the SingleThreadedPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SingleThreadedPool.
     * <p>
     * Test will use pools with a max size of 10, while getting up to 20 at a time,
     *  Poolables are medium objects.
     */
    public void testCompare_SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets20_MediumPoolables()
        throws Exception
    {
        String name = "SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets20_MediumPoolables";

        Class poolableClass = MediumPoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 10;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SingleThreadedPool poolA = new SingleThreadedPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 20, factory );
    }

    /**
     * Compare the SingleThreadedPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SingleThreadedPool.
     * <p>
     * Test will use pools with a max size of 10, while getting up to 10 at a time,
     *  Poolables are large objects.
     */
    public void testCompare_SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets10_LargePoolables()
        throws Exception
    {
        String name = "SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets10_LargePoolables";

        Class poolableClass = LargePoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 10;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SingleThreadedPool poolA = new SingleThreadedPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 10, factory );
    }

    /**
     * Compare the SingleThreadedPool and ResourceLimitingPool when the
     *  ResourceLimitingPool is configured to act like a SingleThreadedPool.
     * <p>
     * Test will use pools with a max size of 10, while getting up to 20 at a time,
     *  Poolables are large objects.
     */
    public void testCompare_SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets20_LargePoolables()
        throws Exception
    {
        String name = "SingleThreadedPool_And_ResourceLimitingPool_Max10_Gets20_LargePoolables";

        Class poolableClass = LargePoolable.class;
        ObjectFactory factory = new ClassInstanceObjectFactory( poolableClass, m_poolLogger );
        int min = 0;
        int max = 10;
        boolean maxStrict = false;
        boolean blocking = false;
        long blockTimeout = 0;
        long trimInterval = 0;

        SingleThreadedPool poolA = new SingleThreadedPool( factory, min, max );
        poolA.enableLogging( m_poolLogger );
        poolA.initialize();

        ResourceLimitingPool poolB = new ResourceLimitingPool( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
        poolB.enableLogging( m_poolLogger );

        generalTest( name, poolA, poolB, 20, factory );
    }

    /*---------------------------------------------------------------
     * PoolComparisonProfileAbstract Methods
     *-------------------------------------------------------------*/
    protected long getPoolRunTime( Pool pool, int gets )
        throws Exception
    {
        // Start clean
        resetMemory();

        final long startTime = System.currentTimeMillis();
        final Poolable[] poolTmp = new Poolable[ gets ];
        final int loops = TEST_SIZE / gets;
        for( int i = 0; i < loops; i++ )
        {
            // Get some Poolables
            for( int j = 0; j < gets; j++ )
            {
                poolTmp[ j ] = pool.get();
            }

            // Put the Poolables back
            for( int j = 0; j < gets; j++ )
            {
                pool.put( poolTmp[ j ] );
                poolTmp[ j ] = null;
            }
        }
        final long duration = System.currentTimeMillis() - startTime;

        // Dispose if necessary
        if( pool instanceof Disposable )
        {
            ( (Disposable)pool ).dispose();
        }

        return duration;
    }
}
