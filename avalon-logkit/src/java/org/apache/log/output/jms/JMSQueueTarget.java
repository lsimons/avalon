/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

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
    private Queue            m_queue;

    ///Session associated with queue
    private QueueSession     m_session;

    ///Sender for queue
    private QueueSender   m_sender;

    ///JMS queue Connection
    private QueueConnection  m_connection;

    public JMSQueueTarget( final MessageBuilder builder,
                           final QueueConnectionFactory factory,
                           final Queue queue )
    {
        super( builder );
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
                m_connection.createQueueSession( false, Session.AUTO_ACKNOWLEDGE);
            
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

