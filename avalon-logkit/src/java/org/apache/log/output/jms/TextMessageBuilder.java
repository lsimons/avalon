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

            /*
              message.setStringProperty(JMS_PROP_MSG_TYPE, JMS_PROP_MSG_TYPE_VALUE);
              message.setStringProperty(JMS_PROP_HOST, ip);
              message.setLongProperty(JMS_PROP_TIME, smessage.time);
              message.setStringProperty(JMS_PROP_CHANNEL, sm.channel);
              message.setStringProperty(JMS_PROP_LOGGER, sm.loggerClassname);
              message.setStringProperty(JMS_PROP_MESSAGE, msg);
              message.setIntProperty( JP_PRIORITY, event.getsm.level );
              message.setStringProperty(JMS_PROP_THREAD, threadName);
            */

            return message;
        }
    }
}
