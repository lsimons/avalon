/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import org.apache.log.LogEvent;
import org.apache.log.output.AbstractTarget;

/**
 * A target that writes to a JMS Topic.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class AbstractJMSTarget
    extends AbstractTarget
{
    ///Appropriate MessageBuilder
    private MessageBuilder m_builder;

    public AbstractJMSTarget( final MessageBuilder builder )
    {
        m_builder = builder;
    }

    protected abstract void send( Message message );

    protected abstract Session getSession();

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    protected void doProcessEvent( final LogEvent event )
        throws Exception
    {
        final Message message =
            m_builder.buildMessage( getSession(), event );
        send( message );
    }

    /**
     * Startup log session.
     *
     */
    protected synchronized void open()
    {
        if( !isOpen() )
        {
            super.open();
            openConnection();
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public synchronized void close()
    {
        if( isOpen() )
        {
            closeConnection();
            super.close();
        }
    }

    protected abstract void openConnection();

    protected abstract void closeConnection();
}
