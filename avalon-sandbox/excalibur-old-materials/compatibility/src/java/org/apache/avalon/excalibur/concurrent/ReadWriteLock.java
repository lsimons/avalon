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
 * @version CVS $Revision: 1.5 $ $Date: 2003/03/31 11:42:03 $
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
                System.out.println( "m_numReadLocksHeld = " + m_numReadLocksHeld );
                System.out.println( "m_numWaitingForWrite = " + m_numWaitingForWrite );
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