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
import javax.jms.JMSException;
import org.apache.log.LogEvent;

/**
 * Interface that classes implement to build JMS Messages.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface MessageBuilder
{
    Message buildMessage( Session session, LogEvent event )        
        throws JMSException;
}
