/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Topic;
import javax.jms.Session;
import javax.jms.Message;

/**
 * A target that writes to a JMS Topic.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class JMSTopicTarget
    extends AbstractJMSTarget
{
    ///ConnectionFactory to use
    private TopicConnectionFactory m_factory;

    ///Topic we will send messages to
    private Topic            m_topic;

    ///Session associated with topic
    private TopicSession     m_session;

    ///Publisher for topic
    private TopicPublisher   m_publisher;

    ///JMS topic Connection
    private TopicConnection  m_connection;

    public JMSTopicTarget( final MessageBuilder builder,
                           final TopicConnectionFactory factory,
                           final Topic topic )
    {
        super( builder );
        m_factory = factory;
        m_topic = topic;
        open();
    }

    protected void send( final Message message )
    {
        try 
        {
            m_publisher.publish( message );
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

    protected void openConnection()
    {
        try
        {
            m_connection = m_factory.createTopicConnection();
            m_connection.start();
            
            m_session = 
                m_connection.createTopicSession( false, Session.AUTO_ACKNOWLEDGE );
            
            m_publisher = m_session.createPublisher( m_topic );
            //if( m_persistent ) publisher.setDeliveryMode( DeliveryMode.PERSISTENT );
            //else publisher.setDeliveryMode( DeliveryMode.NON_PERSISTENT );
            //publisher.setPriority( m_priority );
            //publisher.setTimeToLive( m_timeToLive );
        }
        catch( final Exception e )
        {
            getErrorHandler().error( "Error starting connection", e, null );
        }
    }

    protected void closeConnection()
    {
        try
        {
            if( null != m_publisher ) m_publisher.close();
            if( null != m_session ) m_session.close();
            if( null != m_connection ) m_connection.close();
        }
        catch( Exception e )
        {
            getErrorHandler().error( "Error closing connection", e, null );
        }

        m_publisher = null;
        m_session = null;
        m_connection = null;
    }
}
