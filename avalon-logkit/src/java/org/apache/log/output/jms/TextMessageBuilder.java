/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.log.ContextMap;
import org.apache.log.Hierarchy;
import org.apache.log.LogEvent;

/**
 * Basic message factory that stores LogEvent in Message.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class TextMessageBuilder
    implements MessageBuilder
{
    private final PropertyInfo[]  m_properties;
    private final ContentInfo     m_content;
    
    public TextMessageBuilder( final PropertyInfo[] properties, 
                               final ContentInfo content )
    {
        m_properties = properties;
        m_content = content;
    }
    
    public Message buildMessage( final Session session, final LogEvent event )
        throws JMSException
    {
        synchronized( session )
        {
            final TextMessage message = session.createTextMessage();

            message.setText( getText( event ) );
            for( int i = 0; i < m_properties.length; i++ )
            {
                setProperty( message, i, event );
            }

            return message;
        }
    }

    private void setProperty( final TextMessage message,
                              final int index,
                              final LogEvent event )
        throws JMSException
    {
        final PropertyInfo info = m_properties[ index ];
        final String name = info.getName();

        switch( info.getType() )
        {
        case PropertyType.MESSAGE:
            message.setStringProperty( name, event.getMessage() );
            break;

        case PropertyType.RELATIVE_TIME:
            message.setLongProperty( name, event.getRelativeTime() );
            break;

        case PropertyType.TIME:
            message.setLongProperty( name, event.getTime() );
            break;

        case PropertyType.CATEGORY:
            message.setStringProperty( name, event.getCategory() );
            break;

        case PropertyType.PRIORITY:
            message.setStringProperty( name, event.getPriority().getName() );
            break;

        case PropertyType.CONTEXT:
            message.setStringProperty( name, getContextMap( event.getContextMap(),
                                                            info.getAux() ) );
            break;

        case PropertyType.STATIC:
            message.setStringProperty( name, info.getAux() );
            break;

        case PropertyType.THROWABLE:
            message.setStringProperty( name, getStackTrace( event.getThrowable() ) );
            break;

        default:
            throw new IllegalStateException( "Unknown PropertyType: " + info.getType() );
        }

    }

    private String getText( final LogEvent event )
    {
        switch( m_content.getType() )
        {
        case PropertyType.MESSAGE:
            return event.getMessage();

        case PropertyType.RELATIVE_TIME:
            return String.valueOf( event.getRelativeTime() );

        case PropertyType.TIME:
            return String.valueOf( event.getTime() );

        case PropertyType.CATEGORY:
            return event.getCategory();

        case PropertyType.PRIORITY:
            return event.getPriority().getName();

        case PropertyType.CONTEXT:
            return getContextMap( event.getContextMap(), m_content.getAux() );

        case PropertyType.STATIC:
            return m_content.getAux();

        case PropertyType.THROWABLE:
            return getStackTrace( event.getThrowable() );

        default:
            throw new IllegalStateException( "Unknown PropertyType: " + m_content.getType() );
        }
    }

    private String getStackTrace( final Throwable throwable )
    {
        if ( null != throwable ) return "";

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter( stringWriter );
        throwable.printStackTrace( printWriter );

        return stringWriter.getBuffer().toString();
    }

    private String getContextMap( final ContextMap map, final String aux )
    {
        if( null == map ) return "";
        return map.get( aux, "" ).toString();
    }
}
