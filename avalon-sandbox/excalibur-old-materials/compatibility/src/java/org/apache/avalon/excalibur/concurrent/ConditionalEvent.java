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
 * This class implements a POSIX style "Event" object. The difference
 * between the ConditionalEvent and the java wait()/notify() technique is in
 * handling of event state. If a ConditionalEvent is signalled, a thread
 * that subsequently waits on it is immediately released. In case of auto
 * reset EventObjects, the object resets (unsignalled) itself as soon as it
 * is signalled and waiting thread(s) are released (based on whether signal()
 * or signalAll() was called).
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.CondVar instead
 *
 * @author <a href="mailto:kranga@sapient.com">Karthik Rangaraju</a>
 * @version CVS $Revision: 1.4 $ $Date: 2003/03/22 12:46:23 $
 * @since 4.0
 */
public class ConditionalEvent
{
    private boolean m_state = false;
    private boolean m_autoReset = false;

    // TODO: Need to add methods that block until a specified time and
    // return (though in real-life, I've never known what to do if a thread
    // timesout other than call the method again)!

    /**
     * Creates a manual reset ConditionalEvent with a specified initial state
     *
     * @param initialState Sets the initial state of the ConditionalEvent.
     * Signalled if pInitialState is true, unsignalled otherwise.
     */
    public ConditionalEvent( boolean initialState )
    {
        m_state = initialState;
    }

    /**
     * Creates a ConditionalEvent with the defined initial state.
     *
     * @param initialState if true, the ConditionalEvent is signalled when
     * created.
     * @param autoReset if true creates an auto-reset ConditionalEvent
     */
    public ConditionalEvent( boolean initialState, boolean autoReset )
    {
        m_state = initialState;
        m_autoReset = autoReset;
    }

    /**
     * Checks if the event is signalled. Does not block on the operation.
     *
     * @return true is event is signalled, false otherwise. Does not reset
     * an autoreset event
     */
    public boolean isSignalled()
    {
        return m_state;
    }

    /**
     * Signals the event. A single thread blocked on waitForSignal() is released.
     *
     * @see #signalAll()
     * @see #waitForSignal()
     */
    public void signal()
    {
        synchronized( this )
        {
            m_state = true;
            notify();
        }
    }

    /**
     * Current implementation only works with manual reset events. Releases.
     *
     * all threads blocked on waitForSignal()
     * @see #waitForSignal()
     */
    public void signalAll()
    {
        synchronized( this )
        {
            m_state = true;
            notifyAll();
        }
    }

    /**
     * Resets the event to an unsignalled state
     */
    public void reset()
    {
        synchronized( this )
        {
            m_state = false;
        }
    }

    /**
     * If the event is signalled, this method returns immediately resetting the
     * signal, otherwise it blocks until the event is signalled.
     *
     * @throws InterruptedException if the thread is interrupted when blocked
     */
    public void waitForSignal()
        throws InterruptedException
    {
        synchronized( this )
        {
            while( !m_state )
            {
                wait();
            }
            if( m_autoReset )
            {
                m_state = false;
            }
        }
    }
}
