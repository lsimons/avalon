/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Session;
import javax.jms.Message;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.Hierarchy;

/**
 * A target that writes to a JMS Topic.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractJMSTarget
    implements LogTarget
{
    ///Appropriate MessageBuilder
    private MessageBuilder   m_builder;

    ///Flag indicating that log session is finished (aka target has been closed)
    private boolean        m_isOpen;

    public AbstractJMSTarget( final MessageBuilder builder )
    {
        m_builder = builder;
    }

    protected boolean isOpen()
    {
        return m_isOpen;
    }

    protected abstract void send( Message message );
    protected abstract Session getSession();

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    public void processEvent( final LogEvent event )
    {
        if( !isOpen() )
        {
            error( "Writing event to closed stream.", null );
            return;
        }

        try
        {
            final Message message =
                m_builder.buildMessage( getSession(), event );
            send( message );
        }
        catch( final Throwable throwable )
        {
            error( "Unknown error writing event.", throwable );
        }
    }

    /**
     * Startup log session.
     *
     */
    protected void open()
    {
        if( !isOpen() )
        {
            m_isOpen = true;
            openConnection();
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public void close()
    {
        if( isOpen() )
        {
            closeConnection();
        }
    }

    protected abstract void openConnection();
    protected abstract void closeConnection();

    /**
     * Helper method to write error messages to error handler.
     *
     * @param message the error message
     * @param throwable the exception if any
     */
    protected final void error( final String message, final Throwable throwable )
    {
        Hierarchy.getDefaultHierarchy().log( message, throwable );
        //TODO:
        //Can no longer route to global error handler - somehow need to pass down error
        //handler from engine...
    }
}
