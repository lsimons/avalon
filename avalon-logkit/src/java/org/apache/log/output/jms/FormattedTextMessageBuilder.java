/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.log.LogEvent;
import org.apache.log.format.Formatter;

/**
 * Basic message factory that stores LogEvent in Message.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 */
public class FormattedTextMessageBuilder 
    implements MessageBuilder
{
    private final Formatter m_formatter;

    public FormattedTextMessageBuilder( final Formatter formatter )
    {
        m_formatter = formatter;
    }

    public Message buildMessage( final Session session, final LogEvent event )
        throws JMSException
    {
        synchronized( session )
        {
            final TextMessage message = session.createTextMessage();
            final String text = m_formatter.format( event );
            message.setText( text );

            return message;
        }
    }
}
