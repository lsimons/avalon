/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.log.output.jms;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import org.apache.log.ErrorHandler;

/**
 * A target that writes to a JMS Queue.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 */
public class JMSQueueTarget
    extends AbstractJMSTarget
{
    ///ConnectionFactory to use
    private QueueConnectionFactory m_factory;

    ///Queue we will send messages to
    private Queue m_queue;

    ///Session associated with queue
    private QueueSession m_session;

    ///Sender for queue
    private QueueSender m_sender;

    ///JMS queue Connection
    private QueueConnection m_connection;

    public JMSQueueTarget( final MessageBuilder builder,
                           final QueueConnectionFactory factory,
                           final Queue queue )
    {
        super( builder );
        m_factory = factory;
        m_queue = queue;
        open();
    }

    public JMSQueueTarget( final MessageBuilder builder,
                           final QueueConnectionFactory factory,
                           final Queue queue,
                           final ErrorHandler handler )
    {
        super( builder, handler );
        m_factory = factory;
        m_queue = queue;
        open();
    }

    protected void send( final Message message )
    {
        try
        {
            m_sender.send( message );
        }
        catch( final Exception e )
        {
            getErrorHandler().error( "Error publishing message", e, null );
        }
    }

    protected Session getSession()
    {
        return m_session;
    }

    protected synchronized void openConnection()
    {
        try
        {
            m_connection = m_factory.createQueueConnection();
            m_connection.start();

            m_session =
                m_connection.createQueueSession( false, Session.AUTO_ACKNOWLEDGE );

            m_sender = m_session.createSender( m_queue );
        }
        catch( final Exception e )
        {
            getErrorHandler().error( "Error starting connection", e, null );
        }
    }

    protected synchronized void closeConnection()
    {
        try
        {
            if( null != m_sender ) m_sender.close();
            if( null != m_session ) m_session.close();
            if( null != m_connection ) m_connection.close();
        }
        catch( Exception e )
        {
            getErrorHandler().error( "Error closing connection", e, null );
        }

        m_sender = null;
        m_session = null;
        m_connection = null;
    }
}

