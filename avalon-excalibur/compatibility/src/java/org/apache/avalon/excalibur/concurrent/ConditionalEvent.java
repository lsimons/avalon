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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
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
