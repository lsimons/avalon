/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
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
            while( !( m_numReadLocksHeld != -1 && m_numWaitingForWrite == 0 ) )
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
        acquireRead ();
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
        acquireWrite ();
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
}