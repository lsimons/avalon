/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Session;
import javax.jms.JMSException;
import org.apache.log.LogEvent;

/**
 * Basic message factory that stores LogEvent in Message.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class TextMessageBuilder
    implements MessageBuilder
{
    public Message buildMessage( Session session, LogEvent event )
        throws JMSException
    {
        synchronized( session )
        {
            final TextMessage message = session.createTextMessage();
            message.setText( event.getMessage() );
            return message;
        }
    }
}
