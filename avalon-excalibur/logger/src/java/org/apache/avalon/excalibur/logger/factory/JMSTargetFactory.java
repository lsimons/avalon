/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.logger.factory;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.avalon.excalibur.logger.LogTargetFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log.LogTarget;
import org.apache.log.format.Formatter;
import org.apache.log.output.jms.JMSQueueTarget;
import org.apache.log.output.jms.JMSTopicTarget;
import org.apache.log.output.jms.MessageBuilder;
import org.apache.log.output.jms.ObjectMessageBuilder;
import org.apache.log.output.jms.PropertyInfo;
import org.apache.log.output.jms.PropertyType;
import org.apache.log.output.jms.TextMessageBuilder;

/**
 * Factory for JMS LogTarget-s. The configuration looks like this:
 *
 * <pre>
 *   &lt;jms id="name"&gt;
 *           &lt;connection-factory&gt;java:/TopicConectionFactory&lt;/connection-factory&gt;
 *           &lt;destination type="topic|queue"&gt;jms/LogDestination&lt;/destination&gt;
 *           &lt;message type="object|text"&gt;
 *
 * -if type="text":
 *                   &lt;property&gt;
 *                           &lt;category&gt;CATEGORY&lt;/category&gt;
 *                           &lt;priority&gt;PRIORITY&lt;/priority&gt;
 *                           &lt;time&gt;TIME&lt;/time&gt;
 *                           &lt;rtime&gt;RTIME&lt;/rtime&gt;
 *                           &lt;throwable&gt;THROWABLE&lt;/throwable&gt;
 *                           &lt;hostname&gt;HOSTNAME&lt;/hostname&gt;
 *                           &lt;static aux="234523454325"&gt;SYSTEM&lt;/static&gt;
 *                           &lt;context aux="principal"&gt;PRINCIPAL&lt;/context&gt;
 *                           &lt;context aux="ipaddress"&gt;IPADDRESS&lt;/context&gt;
 *                           &lt;context aux="username"&gt;USERNAME&lt;/context&gt;
 *                   &lt;/property&gt;
 *                   &lt;format type="exteded"&gt;%7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\n%{throwable}&lt;/format&gt;
 *           &lt;/message&gt;
 *   &lt;/jms&gt;
 * </pre>
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>;
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/07 13:37:00 $
 */
public class JMSTargetFactory implements LogTargetFactory
{

    public LogTarget createTarget( final Configuration configuration )
        throws ConfigurationException
    {
        final String factoryName =
            configuration.getChild( "connection-factory", true ).getValue();

        final Configuration destinationConf =
            configuration.getChild( "destination", true );

        final String destinationName = destinationConf.getValue();
        final String destinationType =
            destinationConf.getAttribute( "type", "topic" );

        final Configuration messageConf =
            configuration.getChild( "message", true );

        final MessageBuilder messageBuilder = getMessageBuilder( messageConf );
        final ConnectionFactory factory;
        final Destination destination;
        final LogTarget logTarget;

        try
        {
            Context ctx = new InitialContext();
            factory = (ConnectionFactory)ctx.lookup( factoryName );
            destination = (Destination)ctx.lookup( destinationName );
        }
        catch( NameNotFoundException nnfe )
        {
            throw new ConfigurationException( "Cannot lookup object", nnfe );
        }
        catch( NamingException ne )
        {
            throw new ConfigurationException( "Cannot get naming context", ne );
        }

        if( "queue".equals( destinationType ) )
        {
            logTarget = new JMSQueueTarget( messageBuilder,
                                            (QueueConnectionFactory)factory, (Queue)destination );
        }
        else
        {
            logTarget = new JMSTopicTarget( messageBuilder,
                                            (TopicConnectionFactory)factory, (Topic)destination );
        }

        return logTarget;
    }

    private MessageBuilder getMessageBuilder( final Configuration configuration )
        throws ConfigurationException
    {
        final String messageType = configuration.getAttribute( "type", "object" );

        if( "text".equals( messageType ) )
        {
            final Configuration[] propertyConf =
                configuration.getChild( "property", true ).getChildren();
            final Configuration formatterConf = configuration.getChild( "format" );

            final PropertyInfo[] properties = new PropertyInfo[ propertyConf.length ];

            for( int i = 0; i < properties.length; i++ )
            {
                final String name = propertyConf[ i ].getValue();
                final int type = PropertyType.getTypeIdFor( propertyConf[ i ].getName() );
                final String aux = propertyConf[ i ].getAttribute( "aux", null );

                properties[ i ] = new PropertyInfo( name, type, aux );
            }

            final Formatter formatter = getFormatter( formatterConf );

            return new TextMessageBuilder( properties, formatter );
        }

        return new ObjectMessageBuilder();
    }

    protected Formatter getFormatter( final Configuration conf )
    {
        Formatter formatter = null;

        if( null != conf )
        {
            final FormatterFactory formatterFactory = new FormatterFactory();
            formatter = formatterFactory.createFormatter( conf );
        }

        return formatter;
    }
}
