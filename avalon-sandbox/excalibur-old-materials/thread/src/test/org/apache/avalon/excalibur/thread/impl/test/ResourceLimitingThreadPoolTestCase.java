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
package org.apache.avalon.excalibur.thread.impl.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.testcase.BufferedLogger;
import org.apache.avalon.excalibur.thread.impl.ResourceLimitingThreadPool;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.8 $ $Date: 2003/03/29 18:53:26 $
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

    protected void incCompleteCount()
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

