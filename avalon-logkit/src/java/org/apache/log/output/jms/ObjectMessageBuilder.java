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
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.log.LogEvent;

/**
 * Basic message factory that stores LogEvent in Message.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ObjectMessageBuilder
    implements MessageBuilder
{
    public Message buildMessage( Session session, LogEvent event )
        throws JMSException
    {
        //session access is single threaded
        synchronized( session )
        {
            final ObjectMessage message = session.createObjectMessage();
            message.setObject( event );
            return message;
        }
    }
}
