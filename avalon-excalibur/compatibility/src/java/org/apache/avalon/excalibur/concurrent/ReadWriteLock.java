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
 * Class implementing a read/write lock. The lock has three states -
 * unlocked, locked for reading and locked for writing. If the lock
 * is unlocked, anyone can acquire a read or write lock. If the lock
 * is locked for reading, anyone can acquire a read lock, but no one
 * can acquire a write lock. If the lock is locked for writing, no one
 * can quire any type of lock.
 * <p>
 * When the lock is released, those threads attempting to acquire a write lock
 * will take priority over those trying to get a read lock.
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.ReadWriteLock instead
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public class ReadWriteLock
{
    /**
     * The number of read locks currently held.
     */
    private int m_numReadLocksHeld = 0;

    /**
     * The number of threads waiting for a write lock.
     */
    private int m_numWaitingForWrite = 0;

    /**
     * Synchronization primitive.
     */
    private Object m_lock = new Object();

    /**
     * Default constructor.
     */
    public ReadWriteLock()
    {
    }

    /**
     * Attempts to acquire a read lock. If no lock could be acquired
     * the thread will wait until it can be obtained.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for
     * a lock.
     */
    public void acquireRead()
            throws InterruptedException
    {
        synchronized( m_lock )
        {
            while( !(m_numReadLocksHeld != -1 && m_numWaitingForWrite == 0) )
            {
                m_lock.wait();
            }
            m_numReadLocksHeld++;
        }
    }

    /**
     * @deprecated It's spelled <code>a<b>c</b>quire</code>...
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     * for a lock.
     */
    public void aquireRead()
            throws InterruptedException
    {
        acquireRead();
    }


    /**
     * Attempts to acquire a write lock. If no lock could be acquired
     * the thread will wait until it can be obtained.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for
     * a lock.
     */
    public void acquireWrite()
            throws InterruptedException
    {
        synchronized( m_lock )
        {
            m_numWaitingForWrite++;
            try
            {
                while( m_numReadLocksHeld != 0 )
                {
                    m_lock.wait();
                }
                m_numReadLocksHeld = -1;
            }
            finally
            {
                m_numWaitingForWrite--;
            }
        }
    }

    /**
     * @deprecated It's spelled <code>a<b>c</b>quire</code>...
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     * for a lock.
     */
    public void aquireWrite()
            throws InterruptedException
    {
        acquireWrite();
    }

    /**
     * Releases a lock. This method will release both types of locks.
     *
     * @throws IllegalStateException when an attempt is made to release
     * an unlocked lock.
     */
    public void release()
    {
        synchronized( m_lock )
        {
            if( m_numReadLocksHeld == 0 )
            {
                throw new IllegalStateException( "Attempted to release an unlocked ReadWriteLock." );
            }

            if( m_numReadLocksHeld == -1 )
            {
                m_numReadLocksHeld = 0;
            }
            else
            {
                m_numReadLocksHeld--;
            }

            m_lock.notifyAll();
        }
    }

    /**
     * Attempts to acquire a read lock. This method returns immediately.
     *
     * @return <code>true</code> iff the lock was successfully obtained.
     */
    public boolean tryAcquireRead()
    {
        synchronized( m_lock )
        {
            if( m_numReadLocksHeld != -1 && m_numWaitingForWrite == 0 )
            {
                m_numReadLocksHeld++;
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * @deprecated It's spelled <code>a<b>c</b>quire</code>...
     *
     * @return <code>true</code> iff the lock was successfully obtained.
     */
    public boolean tryAquireRead()
    {
        return tryAcquireRead();
    }

    /**
     * Attempts to acquire a write lock. This method returns immediately.
     *
     * @return <code>true</code> iff the lock was successfully obtained.
     */
    public boolean tryAcquireWrite()
    {
        synchronized( m_lock )
        {
            if( m_numReadLocksHeld == 0 )
            {
                m_numReadLocksHeld = -1;
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * @deprecated It's spelled <code>a<b>c</b>quire</code>...
     *
     * @return <code>true</code> iff the lock was successfully obtained.
     */
    public boolean tryAquireWrite()
    {
        return tryAcquireWrite();
    }
    
    //
    // Methods used for unit tests
    //
    
    /**
     * Returns the number of read locks held.
     */
    protected synchronized int getNumReadLocksHeld()
    {
        return m_numReadLocksHeld;
    }

    /**
     * Returns the number of write locks held.
     */
    protected synchronized int getNumWaitingForWrite()
    {
        return m_numWaitingForWrite;
    }
}
