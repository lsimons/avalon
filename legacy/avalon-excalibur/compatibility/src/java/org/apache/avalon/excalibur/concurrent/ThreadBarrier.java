/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.concurrent;

/**
 * A thread barrier blocks all threads hitting it until a pre-defined number
 * of threads arrive at the barrier. This is useful for implementing release
 * consistent concurrency where you don't want to take the performance penalty
 * of providing mutual exclusion to shared resources
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.CyclicBarrier instead
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public class ThreadBarrier
{
    private int m_threshold;
    private int m_count;

    /**
     * Initializes a thread barrier object with a given thread count.
     *
     * @param count is the number of threads that need to block on
     * barrierSynchronize() before they will be allowed to pass through
     * @see #barrierSynchronize()
     */
    public ThreadBarrier( int count )
    {
        m_threshold = count;
        m_count = 0;
    }

    /**
     * This method blocks all threads calling it until the threshold number of
     * threads have called it. It then releases all threads blocked by it.
     *
     * @throws InterruptedException if any thread blocked during the call is
     * interrupted
     */
    public void barrierSynchronize()
            throws InterruptedException
    {
        synchronized( this )
        {
            if( m_count != m_threshold - 1 )
            {
                m_count++;
                wait();
            }
            else
            {
                m_count = 0;
                notifyAll();
            }
        }
    }
}
