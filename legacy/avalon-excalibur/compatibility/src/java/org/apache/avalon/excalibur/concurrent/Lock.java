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
 * A class to perform a blocking lock.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
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
