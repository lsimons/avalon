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
package org.apache.excalibur.event.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.excalibur.collections.Buffer;
import org.apache.avalon.excalibur.collections.VariableSizeBuffer;
import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.excalibur.event.DefaultQueue;
import org.apache.excalibur.event.EventHandler;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.Signal;
import org.apache.excalibur.event.Source;

/**
 * The CommandManager handles asynchronous commands from the rest of the
 * system.  The only exposed piece is the Queue that other components use to
 * give Commands to this system.  You <strong>must</strong> register this
 * with a ThreadManager for it to work.
 *
 * <p><strong>Source Example</strong></p>
 * <pre>

      //
      // Set up the ThreadManager that the CommandManager will use
      //

      ThreadManager threadManager = new TPCThreadManager();
      threadManager.enableLogging( getLogger().getChildLogger("threadmanager") );
      Parameters params = new Parameters();
      params.setParameter( "threads-per-processor", "2" );
      params.setParameter( "sleep-time", "1000" );
      params.setParameter( "block-timeout", "250" );
      threadManager.parameterize( params );
      threadManager.initialize();

      //
      // Set up the CommandManager
      //

      CommandManager commandManager = new CommandManager();
      threadManager.register( commandManager );
 * </pre>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class CommandManager implements EventPipeline
{
    private final Queue m_queue = new DefaultQueue();
    private final HashMap m_signalHandlers = new HashMap();
    private final Mutex m_mutex = new Mutex();
    private final EventHandler m_eventHandler = new CommandEventHandler(
        Collections.unmodifiableMap( m_signalHandlers ) );
    private final Source[] m_sources = new Source[]{m_queue};

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
            ArrayList handlers = (ArrayList)m_signalHandlers.get( signal.getClass() );

            if( null == handlers )
            {
                handlers = new ArrayList();
            }

            if( !handlers.contains( handler ) )
            {
                handlers.add( handler );

                m_signalHandlers.put( signal.getClass(), handlers );
            }
        }
        catch( InterruptedException ie )
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
            ArrayList handlers = (ArrayList)m_signalHandlers.get( signal.getClass() );

            if( null != handlers )
            {
                if( handlers.remove( handler ) )
                {
                    m_signalHandlers.put( signal.getClass(), handlers );
                }

                if( 0 == handlers.size() )
                {
                    m_signalHandlers.remove( signal.getClass() );
                }
            }
        }
        catch( InterruptedException ie )
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

    private static final class CommandEventHandler implements EventHandler
    {
        private final Map m_signalHandlers;
        private final Buffer m_delayedCommands = new VariableSizeBuffer();

        protected CommandEventHandler( Map signalHandlers )
        {
            m_signalHandlers = signalHandlers;
        }

        public final void handleEvents( Object[] elements )
        {
            for( int i = 0; i < elements.length; i++ )
            {
                handleEvent( elements[ i ] );
            }

            int size = m_delayedCommands.size();
            for( int i = 0; i < size; i++ )
            {
                DelayedCommandInfo command = (DelayedCommandInfo)m_delayedCommands.remove();

                if( System.currentTimeMillis() >= command.m_nextRunTime )
                {
                    try
                    {
                        command.m_command.execute();
                    }
                    catch( Exception e )
                    {
                        // ignore for now
                    }

                    command.m_numExecutions++;

                    if( command.m_repeatable )
                    {
                        RepeatedCommand cmd = (RepeatedCommand)command.m_command;
                        int numRepeats = cmd.getNumberOfRepeats();

                        if( numRepeats < 1 || numRepeats < command.m_numExecutions )
                        {
                            command.m_nextRunTime = System.currentTimeMillis() +
                                cmd.getRepeatInterval();
                            m_delayedCommands.add( command );
                        }
                    }
                }
            }
        }

        public final void handleEvent( Object element )
        {
            if( !( element instanceof Signal ) )
            {
                return;
            }

            if( !( element instanceof Command ) )
            {
                ArrayList handlers = (ArrayList)m_signalHandlers.get( element.getClass() );

                if( null != handlers )
                {
                    Iterator i = handlers.iterator();

                    while( i.hasNext() )
                    {
                        EventHandler handler = (EventHandler)i.next();
                        handler.handleEvent( element );
                    }
                }

                return;
            }

            if( element instanceof DelayedCommand )
            {
                DelayedCommandInfo commandInfo = new DelayedCommandInfo();
                commandInfo.m_command = (DelayedCommand)element;
                commandInfo.m_nextRunTime = System.currentTimeMillis() +
                    commandInfo.m_command.getDelayInterval();
                commandInfo.m_numExecutions = 0;
                commandInfo.m_repeatable = element instanceof RepeatedCommand;

                m_delayedCommands.add( commandInfo );
                return;
            }

            try
            {
                ( (Command)element ).execute();
            }
            catch( Exception e )
            {
                // ignore for now
            }
        }
    }

    private static final class DelayedCommandInfo
    {
        protected DelayedCommand m_command;
        protected long m_nextRunTime;
        protected int m_numExecutions;
        protected boolean m_repeatable;
    }
}
