/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.command;

import org.apache.avalon.excalibur.event.DefaultQueue;
import org.apache.avalon.excalibur.event.Queue;
import org.apache.avalon.excalibur.event.QueueElement;
import org.apache.avalon.excalibur.event.Signal;
import org.apache.avalon.excalibur.event.EventHandler;

/**
 * The CommandManager handles asynchronous commands from the rest of the system.
 * The only exposed piece is the Queue that other components use to give Commands
 * to this system.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class CommandManager implements EventHandler
{
    private final Queue m_queue = new DefaultQueue();
    //private final ThreadManager;

    public CommandManager()
    {
    }

    /*
    public CommandManager( ThreadManager threadManager )
    {
    }
    */

    public final Queue getCommandQueue()
    {
        return m_queue;
    }

    public final void registerSignalHandler( Signal signal, EventHandler handler )
    {
    }

    public final void deregisterSignalHandler( Signal signal, EventHandler handler )
    {
    }

    public final void handleEvents( QueueElement[] elements )
    {
        for (int i = 0; i < elements.length; i++)
        {
            handleEvent( elements[i] );
        }
    }

    public final void handleEvent( QueueElement element )
    {
    }

    private final static class Runner implements Runnable
    {
        private final Queue m_queue;

        private Runner( Queue queue )
        {
            m_queue = queue;
        }

        public void run()
        {
        }
    }
}