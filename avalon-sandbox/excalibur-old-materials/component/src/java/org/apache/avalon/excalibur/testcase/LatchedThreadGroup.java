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
package org.apache.avalon.excalibur.testcase;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * This class is useful for writing MultiThreaded test cases where you need to perform
 *  multithreaded load testing on a component.
 * <p>
 * An instance of will create a block of threads of the specified size.  Each thread will be
 *  assigned to run a specified Runnable instance.  The threads will then all wait at a latch
 *  until the go method is called.  The go method will not return until all of the
 *  Runnables have completed.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version $Id: LatchedThreadGroup.java,v 1.4 2003/03/22 12:46:25 leosimons Exp $
 */
public class LatchedThreadGroup
    extends AbstractLogEnabled
{
    private Thread[] m_threads;
    private Object m_semaphore = new Object();
    private int m_startedCount;
    private boolean m_latched;
    private int m_completedCount;
    private Throwable m_exception;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a LatchedThreadGroup with a thread for each Runnable in the runnables array.
     */
    public LatchedThreadGroup( Runnable[] runnables )
    {
        int threadCount = runnables.length;
        m_threads = new Thread[ threadCount ];
        for( int i = 0; i < threadCount; i++ )
        {
            m_threads[ i ] = new Runner( runnables[ i ], "Latched_Thread_" + i );
        }
    }

    /**
     * Creates a LatchedThreadGroup with threadCount threads each running runnable.
     */
    public LatchedThreadGroup( Runnable runnable, int threadCount )
    {
        m_threads = new Thread[ threadCount ];
        for( int i = 0; i < threadCount; i++ )
        {
            m_threads[ i ] = new Runner( runnable, "Latched_Thread_" + i );
        }
    }

    /*---------------------------------------------------------------
     * Methods
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
        getLogger().debug( "Memory: " + ( runtime.totalMemory() - runtime.freeMemory() ) );
    }

    /**
     * Causes all of the Runnables to start at the same instance.  This method will return
     *  once all of the Runnables have completed.
     *
     * @return time, in milliseconds, that it took for all of the Runnables to complete.
     */
    public long go()
        throws Exception
    {
        // Start each of the threads.  They will block until the latch is released.  This is
        //  necessary because it takes some time for the threads to each allocate their required
        //  system resources and actually be ready to run.
        int threadCount = m_threads.length;
        for( int i = 0; i < threadCount; i++ )
        {
            m_threads[ i ].start();
        }

        // Wait for all of the threads to start before starting to time the test
        synchronized( m_semaphore )
        {
            while( m_startedCount < threadCount )
            {
                m_semaphore.wait();
            }

            // Start clean
            resetMemory();

            // Release the threads.
            m_latched = true;
            getLogger().debug( "Main thread released the test thread latch." );
            m_semaphore.notifyAll();
        }
        // Start timing
        long startTime = System.currentTimeMillis();

        // Wait for all of the threads to complete
        synchronized( m_semaphore )
        {
            getLogger().debug( "Waiting for test threads to all complete." );
            while( m_completedCount < threadCount )
            {
                try
                {
                    m_semaphore.wait();
                }
                catch( InterruptedException e )
                {
                }
            }
        }
        final long duration = System.currentTimeMillis() - startTime;
        getLogger().debug( "All test threads completed." );

        if( m_exception != null )
        {
            throw new CascadingAssertionFailedError( "Exception in test thread.", m_exception );
        }
        return duration;
    }

    /**
     * Inner access method to getLogger() to work around a bug in the Javac compiler
     *  when getLogger() is called from the method of an inner class.  Jikes seems to
     *  handle it Ok. :-/
     */
    private Logger getInnerLogger()
    {
        return getLogger();
    }

    /*---------------------------------------------------------------
     * Inner Classes
     *-------------------------------------------------------------*/
    private class Runner extends Thread
    {
        private Runnable m_runnable;

        protected Runner( Runnable runnable, String name )
        {
            super( name );
            m_runnable = runnable;
        }

        public void run()
        {
            try
            {
                // Need all threads to wait until all the others are ready.
                synchronized( m_semaphore )
                {
                    m_startedCount++;
                    getInnerLogger().debug( "Started " + m_startedCount + " test threads." );
                    if( m_startedCount >= m_threads.length )
                    {
                        m_semaphore.notifyAll();
                    }
                    while( !m_latched )
                    {
                        try
                        {
                            m_semaphore.wait();
                        }
                        catch( InterruptedException e )
                        {
                        }
                    }
                }

                // Run the runnable
                try
                {
                    m_runnable.run();
                }
                catch( Throwable t )
                {
                    synchronized( m_semaphore )
                    {
                        getInnerLogger().error( "Error in " + Thread.currentThread().getName(), t );
                        if( m_exception != null )
                        {
                            m_exception = t;
                        }
                    }
                }
            }
            finally
            {
                // Say that we are done
                synchronized( m_semaphore )
                {
                    m_completedCount++;
                    getInnerLogger().debug( m_completedCount + " test threads completed." );
                    m_semaphore.notifyAll();
                }
            }
        }
    }
}
