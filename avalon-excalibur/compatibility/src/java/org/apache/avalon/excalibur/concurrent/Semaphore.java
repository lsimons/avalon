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
 * This class implements a counting semaphore, also known as a
 * Dijkstra semaphore.  A semaphore is used to control access to
 * resources.  A counting semaphore has a count associated with it and
 * each acquire() call reduces the count.  A thread that tries to
 * acquire() a semaphore with a zero count blocks until someone else
 * calls release(), which increases the count.
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.Semaphore instead
 *
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 15:31:39 $
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
                    wait = msecs - ( System.currentTimeMillis() - start );
                }
            }

            return false;
        }
    }
}
