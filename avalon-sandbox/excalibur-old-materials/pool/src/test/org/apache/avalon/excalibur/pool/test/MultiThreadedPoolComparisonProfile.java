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
 * @version $Id: MultiThreadedPoolComparisonProfile.java,v 1.5 2003/03/29 18:53:25 bloritsch Exp $
 */
public class MultiThreadedPoolComparisonProfile
    extends PoolComparisonProfileAbstract
{
    protected static final int THREADS = 100;

    protected Object m_semaphore = new Object();
    protected int m_getCount;
    protected Throwable m_throwable;

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
