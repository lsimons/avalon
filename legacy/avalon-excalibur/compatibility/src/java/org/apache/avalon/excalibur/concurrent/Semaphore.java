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
 * This class implements a counting semaphore, also known as a
 * Dijkstra semaphore.  A semaphore is used to control access to
 * resources.  A counting semaphore has a count associated with it and
 * each acquire() call reduces the count.  A thread that tries to
 * acquire() a semaphore with a zero count blocks until someone else
 * calls release(), which increases the count.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.Semaphore instead
 * @since 4.0
 */
public class Semaphore
        implements Sync
{
    private long m_tokens;

    /**
     * Creates a semaphore with the specified number of tokens, which
     * determines the maximum number of acquisitions to allow.
     *
     * @param tokens the maximum number of acquisitions to allow
     */
    public Semaphore( final long tokens )
    {
        m_tokens = tokens;
    }

    public synchronized void acquire()
            throws InterruptedException
    {
        //TODO: check for interuption outside sync block?
        if( Thread.interrupted() ) throw new InterruptedException();

        //While there is no more tokens left wait
        while( 0 >= m_tokens )
        {
            wait();
        }
        m_tokens--;
    }

    public synchronized void release()
    {
        m_tokens++;
        notify();
    }

    public synchronized boolean attempt( final long msecs )
            throws InterruptedException
    {
        if( Thread.interrupted() ) throw new InterruptedException();

        if( m_tokens > 0 )
        {
            m_tokens--;
            return true;
        }
        else
        {
            final long start = System.currentTimeMillis();
            long wait = msecs;

            while( wait > 0 )
            {
                wait( wait );

                if( m_tokens > 0 )
                {
                    m_tokens--;
                    return true;
                }
                else
                {
                    wait = msecs - (System.currentTimeMillis() - start);
                }
            }

            return false;
        }
    }
}
