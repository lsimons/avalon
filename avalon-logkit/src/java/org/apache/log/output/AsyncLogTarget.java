/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import java.util.LinkedList;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;

/**
 * An asynchronous LogTarget that sends entries on in another thread.
 * It is the responsibility of the user of this class to start 
 * the thread etc.
 *
 * <pre>
 * LogTarget target = ...;
 * AsyncLogTarget asyncTarget = new AsyncLogTarget( target );
 * Thread thread = new Thread( asyncTarget );
 * thread.setPriority( Thread.MIN_PRIORITY );
 * thread.start();
 * LogKit.addLogTarget( "foo", asyncTarget );
 * </pre>
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class AsyncLogTarget 
    implements LogTarget, Runnable
{
    protected final LinkedList  m_list;
    protected final int         m_queueSize;  
    protected final LogTarget   m_logTarget;  

    public AsyncLogTarget( final LogTarget logTarget )
    {
        this( logTarget, 15 );
    }

    public AsyncLogTarget( final LogTarget logTarget, final int queueSize )
    {
        m_logTarget = logTarget;
        m_list = new LinkedList();
        m_queueSize = queueSize;
    }

    /**
     * Process a log event by adding it to queue.
     *
     * @param event the log event
     */
    public void processEvent( final LogEvent event )
    {
        synchronized( m_list )
        {
            final int size = m_list.size();
            while( m_queueSize <= size )
            {
                try { m_list.wait(); }
                catch( final InterruptedException ie )
                {
                    //This really should not occur ...
                    //Maybe we should log it though for 
                    //now lets ignore it
                }
            }

            m_list.addFirst( event );

            if( size == 0 )
            {
                //tell the "server" thread to wake up 
                //if it is waiting for a queue to contain some items
                m_list.notify();
            }
        }
    }

    public void run()
    {
        //set this variable when thread is interupted
        //so we know we can shutdown thread soon.
        boolean interupted = false;

        while( true )
        {
            LogEvent event = null;

            synchronized( m_list )
            {
                while( null == event )
                {
                    final int size = m_list.size();

                    if( size > 0 )
                    {
                        event = (LogEvent)m_list.removeLast();
                        
                        if( size == m_queueSize )
                        {
                            //tell the "client" thread to wake up 
                            //if it is waiting for a queue position to open up
                            m_list.notify();
                        }

                    }
                    else if( interupted || Thread.interrupted() )
                    {
                        //ie there is nothing in queue and thread is interrupted
                        //thus we stop thread
                        return;
                    }
                    else
                    {
                        try { m_list.wait(); }
                        catch( final InterruptedException ie )
                        {
                            //Ignore this and let it be dealt in next loop 
                            //Need to set variable as the exception throw cleared status
                            interupted = true;
                        }
                    }
                }
            }

            //actually process an event
            m_logTarget.processEvent( event );
        }
    }
}
