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

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.log.ContextMap;
import org.apache.log.LogEvent;
import org.apache.log.format.Formatter;

/**
 * Basic message factory that stores LogEvent in Message.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class TextMessageBuilder
    implements MessageBuilder
{
    private final PropertyInfo[] m_properties;
    private final Formatter m_formatter;

   /**
    * Creation of a new text message builder.
    * @param formatter the message formatter
    */
    public TextMessageBuilder( final Formatter formatter )
    {
        m_properties = new PropertyInfo[ 0 ];
        m_formatter = formatter;
    }

   /**
    * Creation of a new text message builder.
    * @param properties the property info set
    * @param formatter the message formatter
    */
    public TextMessageBuilder( final PropertyInfo[] properties,
                               final Formatter formatter )
    {
        m_properties = properties;
        m_formatter = formatter;
    }

   /**
    * Build a message from the supplied session for the supplied event
    * @param session the session
    * @param event the log event
    * @return the message
    * @exception JMSException if a messaging related error occurs
    */
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

   /**
    * Set a property
    * @param message the text message
    * @param index the index
    * @param event the log event
    */
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
        if( null == m_formatter )
        {
            return event.getMessage();
        }
        else
        {
            return m_formatter.format( event );
        }
    }

    private String getStackTrace( final Throwable throwable )
    {
        if( null == throwable ) 
        {
            return "";
        }

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter( stringWriter );
        throwable.printStackTrace( printWriter );

        return stringWriter.getBuffer().toString();
    }

    private String getContextMap( final ContextMap map, final String aux )
    {
        if( null == map ) 
        {
            return "";
        }
        return map.get( aux, "" ).toString();
    }
}
