/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.concurrent;

/**
 * A class to perform a blocking lock.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 * @since 4.0
 * @deprecated use the Mutex class instead
 */
public class Lock
{
    /**
     * Is this locked?.
     */
    private boolean m_isLocked;

    /**
     * Locks.
     *
     * @throws InterruptedException if the thread is interrupted while waiting on a lock
     */
    public final void lock()
        throws InterruptedException
    {
        synchronized( this )
        {
            while( m_isLocked )
            {
                wait();
            }
            m_isLocked = true;
        }
    }

    /**
     * Unlocks.
     */
    public final void unlock()
    {
        synchronized( this )
        {
            m_isLocked = false;
            notify();
        }
    }
}
