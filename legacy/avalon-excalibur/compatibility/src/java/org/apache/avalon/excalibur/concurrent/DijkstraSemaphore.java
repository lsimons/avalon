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
 * Also called counting semaphores, Djikstra semaphores are used to control
 * access to a set of resources. A Djikstra semaphore has a count associated
 * with it and each acquire() call reduces the count. A thread that tries to
 * acquire() a Djikstra semaphore with a zero count blocks until someone else
 * calls release() thus increasing the count.
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.Semaphore instead
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public class DijkstraSemaphore
{
    private int m_count;
    private int m_maxCount;
    private Object m_starvationLock = new Object();

    /**
     * Creates a Djikstra semaphore with the specified max count and initial
     * count set to the max count (all resources released).
     *
     * @param maxCount is the max semaphores that can be acquired
     */
    public DijkstraSemaphore( int maxCount )
    {
        this( maxCount, maxCount );
    }

    /**
     * Creates a Djikstra semaphore with the specified max count and an initial
     * count of acquire() operations that are assumed to have already been
     * performed.
     *
     * @param maxCount is the max semaphores that can be acquired
     * @param initialCount is the current count (setting it to zero means all
     * semaphores have already been acquired). 0 <= initialCount <= maxCount
     */
    public DijkstraSemaphore( int maxCount, int initialCount )
    {
        m_count = initialCount;
        m_maxCount = maxCount;
    }

    /**
     * If the count is non-zero, acquires a semaphore and decrements the count
     * by 1, otherwise blocks until a release() is executed by some other thread.
     *
     * @throws InterruptedException is the thread is interrupted when blocked
     * @see #tryAcquire()
     * @see #acquireAll()
     */
    public void acquire()
            throws InterruptedException
    {
        synchronized( this )
        {
            // Using a spin lock to take care of rogue threads that can enter
            // before a thread that has exited the wait state acquires the monitor
            while( m_count == 0 )
            {
                wait();
            }
            m_count--;
            synchronized( m_starvationLock )
            {
                if( m_count == 0 )
                {
                    m_starvationLock.notify();
                }
            }
        }
    }

    /**
     * Non-blocking version of acquire().
     *
     * @return true if semaphore was acquired (count is decremented by 1), false
     * otherwise
     */
    public boolean tryAcquire()
    {
        synchronized( this )
        {
            if( m_count != 0 )
            {
                m_count--;
                synchronized( m_starvationLock )
                {
                    if( m_count == 0 )
                    {
                        m_starvationLock.notify();
                    }
                }
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Releases a previously acquires semaphore and increments the count by one.
     * Does not check if the thread releasing the semaphore was a thread that
     * acquired the semaphore previously. If more releases are performed than
     * acquires, the count is not increased beyond the max count specified during
     * construction.
     *
     * @see #release( int count )
     * @see #releaseAll()
     */
    public void release()
    {
        synchronized( this )
        {
            m_count++;
            if( m_count > m_maxCount )
            {
                m_count = m_maxCount;
            }
            notify();
        }
    }

    /**
     * Same as release() except that the count is increased by pCount instead
     * of 1. The resulting count is capped at max count specified in the
     * constructor.
     *
     * @param count is the amount by which the counter should be incremented
     * @see #release()
     */
    public void release( int count )
    {
        synchronized( this )
        {
            if( m_count + count > m_maxCount )
            {
                m_count = m_maxCount;
            }
            else
            {
                m_count += count;
            }
            notifyAll();
        }
    }

    /**
     * Tries to acquire all the semaphores thus bringing the count to zero.
     *
     * @throws InterruptedException if the thread is interrupted when blocked on
     * this call
     * @see #acquire()
     * @see #releaseAll()
     */
    public void acquireAll()
            throws InterruptedException
    {
        synchronized( this )
        {
            for( int index = 0; index < m_maxCount; index++ )
            {
                acquire();
            }
        }
    }

    /**
     * Releases all semaphores setting the count to max count.
     * Warning: If this method is called by a thread that did not make a
     * corresponding acquireAll() call, then you better know what you are doing!
     *
     * @see #acquireAll()
     */
    public void releaseAll()
    {
        synchronized( this )
        {
            release( m_maxCount );
            notifyAll();
        }
    }

    /**
     * This method blocks the calling thread until the count drops to zero.
     * The method is not stateful and hence a drop to zero will not be recognized
     * if a release happens before this call. You can use this method to implement
     * threads that dynamically increase the resource pool or that log occurences
     * of resource starvation. Also called a reverse-sensing semaphore.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void starvationCheck()
            throws InterruptedException
    {
        synchronized( m_starvationLock )
        {
            if( m_count != 0 )
            {
                m_starvationLock.wait();
            }
        }
    }
}
