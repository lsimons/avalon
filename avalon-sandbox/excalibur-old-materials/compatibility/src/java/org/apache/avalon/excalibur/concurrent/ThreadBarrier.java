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
 * A thread barrier blocks all threads hitting it until a pre-defined number
 * of threads arrive at the barrier. This is useful for implementing release
 * consistent concurrency where you don't want to take the performance penalty
 * of providing mutual exclusion to shared resources
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.CyclicBarrier instead
 *
 * @author <a href="mailto:kranga@sapient.com">Karthik Rangaraju</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/04/05 19:39:33 $
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
