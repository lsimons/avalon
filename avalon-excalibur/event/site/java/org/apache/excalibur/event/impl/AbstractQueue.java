/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.excalibur.event.impl;

import org.apache.excalibur.event.Queue;

/**
 * Provides the base functionality for the other <code>Queue</code> types.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 */
public abstract class AbstractQueue implements Queue
{
    /** An empty array used as a return value when the Queue is empty */
    protected final static Object[] EMPTY_ARRAY = new Object[ 0 ];
    /** The number of milliseconds to wait */
    protected long m_timeout = 0;

    /**
     * Default for canAccept()
     *
     * @return how many elements we can enqueue
     */
    public int canAccept()
    {
        return ( maxSize() > 0 ) ? maxSize() - size() : maxSize();
    }

    /**
     * Default maxSize to -1 which is unbounded
     *
     * @return the maximum number of elements
     */
    public int maxSize()
    {
        return -1;
    }

    /**
     * Check to see if the <code>Queue</code> is full. The method uses the
     * <code>maxSize</code> and <code>size</code> methods to determine
     * whether the queue is full.
     *
     * @return true if there is no room in the Queue
     */
    public boolean isFull()
    {
        return maxSize() != -1  /* There exists an upper bound... */
            && maxSize() - size() <= 0; /* ...and it is reached. */
    }

    /**
     * Set the timeout for the <code>Queue</code> in milliseconds.  The
     * default timeout is 0, which means that we don't wait at all.
     *
     * @param millis  The number of milliseconds to block waiting for events to be enqueued
     */
    public void setTimeout( final long millis )
    {
        if( millis > 0 )
        {
            m_timeout = millis;
        }
        else
        {
            m_timeout = 0;
        }
    }

    /**
     * Encapsulates the logic to block the <code>Queue</code> for the amount
     * of time specified by the timeout.
     *
     * @param lock  The object used as the mutex.
     */
    protected void block( Object lock )
    {
        if( m_timeout > 0 )
        {
            long start = System.currentTimeMillis();
            long end = start + m_timeout;

            while( start < end || size() > 0 )
            {
                try
                {
                    lock.wait( m_timeout );
                }
                catch( InterruptedException ie )
                {
                    // ignore
                }
            }
        }
    }
}
