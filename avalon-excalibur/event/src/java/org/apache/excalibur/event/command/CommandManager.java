/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.command;

import org.apache.avalon.excalibur.collections.Buffer;
import org.apache.avalon.excalibur.collections.VariableSizeBuffer;
import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.avalon.excalibur.event.DefaultQueue;
import org.apache.avalon.excalibur.event.EventHandler;
import org.apache.avalon.excalibur.event.Queue;
import org.apache.avalon.excalibur.event.QueueElement;
import org.apache.avalon.excalibur.event.Signal;
import org.apache.avalon.excalibur.event.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The CommandManager handles asynchronous commands from the rest of the system.
 * The only exposed piece is the Queue that other components use to give Commands
 * to this system.  You <strong>must</strong> register this with a ThreadManager
 * for it to work.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class CommandManager implements EventPipeline
{
    private final Queue        m_queue          = new DefaultQueue();
    private final HashMap      m_signalHandlers = new HashMap();
    private final Mutex        m_mutex          = new Mutex();
    private final EventHandler m_eventHandler   = new CommandEventHandler(
            Collections.unmodifiableMap( m_signalHandlers ) );
    private final Source[]     m_sources        = new Source[] { m_queue };

    public CommandManager()
    {
    }

    public final Queue getCommandQueue()
    {
        return m_queue;
    }

    public final void registerSignalHandler( Signal signal, EventHandler handler )
    {
        try
        {
            m_mutex.acquire();
            ArrayList handlers = (ArrayList) m_signalHandlers.get( signal.getClass() );

            if ( null == handlers )
            {
                handlers = new ArrayList();
            }

            if ( ! handlers.contains( handler ) )
            {
                handlers.add( handler );

                m_signalHandlers.put( signal.getClass(), handlers );
            }
        }
        catch (InterruptedException ie)
        {
            // ignore for now
        }
        finally
        {
            m_mutex.release();
        }
    }

    public final void deregisterSignalHandler( Signal signal, EventHandler handler )
    {
        try
        {
            m_mutex.acquire();
            ArrayList handlers = (ArrayList) m_signalHandlers.get( signal.getClass() );

            if ( null != handlers )
            {
                if ( handlers.remove( handler ) )
                {
                    m_signalHandlers.put( signal.getClass(), handlers );
                }

                if ( 0 == handlers.size() )
                {
                    m_signalHandlers.remove( signal.getClass() );
                }
            }
        }
        catch (InterruptedException ie)
        {
            // ignore for now
        }
        finally
        {
            m_mutex.release();
        }
    }

    public final Source[] getSources()
    {
        return m_sources;
    }

    public final EventHandler getEventHandler()
    {
        return m_eventHandler;
    }

    private final static class CommandEventHandler implements EventHandler
    {
        private final Map    m_signalHandlers;
        private final Buffer m_delayedCommands = new VariableSizeBuffer();

        protected CommandEventHandler( Map signalHandlers )
        {
            m_signalHandlers = signalHandlers;
        }

        public final void handleEvents( QueueElement[] elements )
        {
            for (int i = 0; i < elements.length; i++)
            {
                handleEvent( elements[i] );
            }

            int size = m_delayedCommands.size();
            for (int i = 0; i < size; i++)
            {
                DelayedCommandInfo command = (DelayedCommandInfo) m_delayedCommands.remove();

                if ( System.currentTimeMillis() >= command.m_nextRunTime )
                {
                    try
                    {
                        command.m_command.execute();
                    } catch (Exception e)
                    {
                        // ignore for now
                    }

                    command.m_numExecutions++;

                    if ( command.m_repeatable )
                    {
                        RepeatedCommand cmd = (RepeatedCommand) command.m_command;
                        int numRepeats = cmd.getNumberOfRepeats();

                        if ( numRepeats < 1 || numRepeats < command.m_numExecutions )
                        {
                            command.m_nextRunTime = System.currentTimeMillis() +
                                    cmd.getRepeatInterval();
                            m_delayedCommands.add( command );
                        }
                    }
                }
            }
        }

        public final void handleEvent( QueueElement element )
        {
            if ( ! ( element instanceof Signal ) )
            {
                return;
            }

            if ( ! ( element instanceof Command ) )
            {
                ArrayList handlers = (ArrayList) m_signalHandlers.get( element.getClass() );

                if ( null != handlers )
                {
                    Iterator i = handlers.iterator();

                    while ( i.hasNext() )
                    {
                        EventHandler handler = (EventHandler) i.next();
                        handler.handleEvent( element );
                    }
                }

                return;
            }

            if ( element instanceof DelayedCommand )
            {
                DelayedCommandInfo commandInfo = new DelayedCommandInfo();
                commandInfo.m_command = (DelayedCommand) element;
                commandInfo.m_nextRunTime = System.currentTimeMillis() +
                        commandInfo.m_command.getDelayInterval();
                commandInfo.m_numExecutions = 0;
                commandInfo.m_repeatable = element instanceof RepeatedCommand;

                m_delayedCommands.add( commandInfo );
                return;
            }

            try
            {
                ((Command) element).execute();
            } catch (Exception e)
            {
                // ignore for now
            }
        }
    }

    private final static class DelayedCommandInfo
    {
        protected DelayedCommand m_command;
        protected long m_nextRunTime;
        protected int m_numExecutions;
        protected boolean m_repeatable;
    }
}