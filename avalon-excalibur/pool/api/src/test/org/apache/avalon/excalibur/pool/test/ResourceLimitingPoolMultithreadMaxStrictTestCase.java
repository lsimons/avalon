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

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.ResourceLimitingPool;
import org.apache.avalon.excalibur.testcase.BufferedLogger;

import com.clarkware.junitperf.ConstantTimer;
import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TimedTest;
import com.clarkware.junitperf.Timer;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 14:44:01 $
 * @since 4.1
 */
public final class ResourceLimitingPoolMultithreadMaxStrictTestCase extends TestCase
{
    private static BufferedLogger m_logger;
    private static ClassInstanceObjectFactory m_factory;
    private static ResourceLimitingPool m_pool;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ResourceLimitingPoolMultithreadMaxStrictTestCase()
    {
        this( "ResourceLimitingPool Multithreaded Max Size Strict Test Case" );
    }

    public ResourceLimitingPoolMultithreadMaxStrictTestCase( final String name )
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
        int maxUsers = 50;
        int iterations = 10;
        long maxElapsedTime = 20000;

        Test testCase = new ResourceLimitingPoolMultithreadMaxStrictTestCase( "testGetPut" );
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
        m_pool = new ResourceLimitingPool( m_factory, 3, true, false, 0, 0 );

        m_pool.enableLogging( m_logger );
    }

    public static void oneTimeTearDown() throws Exception
    {
        // The timing of this test makes it so the pool should grow to 4 elements
        assertEquals( "1) Pool Ready Size", 3, m_pool.getReadySize() );
        assertEquals( "1) Pool Size", 3, m_pool.getSize() );

        // Make sure that each of the objects are uniqe by checking them all back out.
        Poolable p1 = m_pool.get();
        Poolable p2 = m_pool.get();
        Poolable p3 = m_pool.get();

        assertEquals( "2) Pool Ready Size", 0, m_pool.getReadySize() );
        assertEquals( "2) Pool Size", 3, m_pool.getSize() );

        assertTrue( "p1 != p2", p1 != p2 );
        assertTrue( "p1 != p3", p1 != p3 );
        assertTrue( "p2 != p3", p2 != p3 );

        m_pool.put( p1 );
        m_pool.put( p2 );
        m_pool.put( p3 );

        assertEquals( "3) Pool Ready Size", 3, m_pool.getReadySize() );
        assertEquals( "3) Pool Size", 3, m_pool.getSize() );

        m_pool.dispose();

        assertEquals( "4) Pool Ready Size", 0, m_pool.getReadySize() );
        assertEquals( "4) Pool Size", 0, m_pool.getSize() );
    }

    /*---------------------------------------------------------------
     * TestCases
     *-------------------------------------------------------------*/
    public void testGetPut() throws Exception
    {
        Poolable p;
        try
        {
            p = m_pool.get();
        }
        catch( Exception e )
        {
            // We will sometimes fail to get an object
            assertEquals( "Exception calling get()",
                          "Could not create enough Components to service your request.",
                          e.getMessage() );
            return;
        }

        // We can't check the exact pool size, but make sure that it never gets over 3
        assertTrue( "Pool size <= 3", m_pool.getSize() <= 3 );

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

