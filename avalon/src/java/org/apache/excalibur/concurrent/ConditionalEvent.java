/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.concurrent;

/**
 * This class implements a POSIX style "Event" object. The difference
 * between the ConditionalEvent and the java wait()/notify() technique is in
 * handling of event state. If a ConditionalEvent is signalled, a thread
 * that subsequently waits on it is immediately released. In case of auto
 * reset EventObjects, the object resets (unsignalled) itself as soon as it
 * is signalled and waiting thread(s) are released (based on whether signal()
 * or signalAll() was called).
 *
 * @author <a href="mailto:kranga@sapient.com">Karthik Rangaraju</a>
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
     * @param pInitialState Sets the initial state of the ConditionalEvent.
     * Signalled if pInitialState is true, unsignalled otherwise.
     */
    public ConditionalEvent( boolean initialState )
    {
        m_state = initialState;
    }

    /**
     * Creates a ConditionalEvent with the defined initial state
     * @param pInitialState if true, the ConditionalEvent is signalled when
     * created.
     * @param pAutoReset if true creates an auto-reset ConditionalEvent
     */
    public ConditionalEvent( boolean initialState, boolean autoReset )
    {
        m_state = initialState;
        m_autoReset = autoReset;
    }

    /**
     * Checks if the event is signalled. Does not block on the operation
     * @return true is event is signalled, false otherwise. Does not reset
     * an autoreset event
     */
    public boolean isSignalled()
    {
        return m_state;
    }

    /**
     * Signals the event. A single thread blocked on waitForSignal() is released
     * @see #signalAll()
     * @see #waitForSignal()
     */
    public void signal()
    {
        synchronized ( this )
        {
            m_state = true;
            notify();
        }
    }

    /**
     * Current implementation only works with manual reset events. Releases
     * all threads blocked on waitForSignal()
     * @see #waitForSignal()
     */
    public void signalAll()
    {
        synchronized ( this )
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
        synchronized ( this )
        {
            m_state = false;
        }
    }

    /**
     * If the event is signalled, this method returns immediately resetting the
     * signal, otherwise it blocks until the event is signalled.
     * @throws InterruptedException if the thread is interrupted when blocked
     */
    public void waitForSignal()
        throws InterruptedException
    {
        synchronized ( this )
        {
            while ( m_state == false )
            {
                wait();
            }
            if ( m_autoReset == true )
            {
                m_state = false;
            }
        }
    }

}
