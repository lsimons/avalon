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
package org.apache.avalon.cornerstone.blocks.event;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.avalon.cornerstone.services.event.Event;
import org.apache.avalon.cornerstone.services.event.EventManager;
import org.apache.avalon.cornerstone.services.event.Publisher;
import org.apache.avalon.cornerstone.services.event.Register;
import org.apache.avalon.cornerstone.services.event.Subscriber;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * EventManager Block
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.event.EventManager"
 *
 * @author Mauro Talevi
 */
public class DefaultEventManager extends AbstractLogEnabled implements EventManager
{
    private Class m_eventClass = Event.class;
    private Publisher m_publisher = new DefaultPublisher();
    private Register m_register = new DefaultRegister();
    private Hashtable m_subscribers = new Hashtable();

    public Publisher getPublisher()
    {
        return m_publisher;
    }

    public Register getRegister()
    {
        return m_register;
    }

    class DefaultPublisher implements Publisher
    {
        public void publish( final Event event )
        {
            if( getLogger().isInfoEnabled() )
                getLogger().info( "Publishing event " + event.getClass() );

            for( Enumeration e = m_subscribers.elements(); e.hasMoreElements(); )
            {
                Subscriber subscriber = ( Subscriber ) e.nextElement();

                if( subscriber.getEventType().isAssignableFrom( event.getClass() )
                    && ( subscriber.getFilter() == null || subscriber.getFilter().filter( event ) ) )
                {
                    if( getLogger().isInfoEnabled() )
                        getLogger().info( "Informing subscriber " + subscriber
                                          + " of event " + event.getClass() );

                    subscriber.inform( event );
                }
            }
        }
    }

    class DefaultRegister implements Register
    {
        public void subscribe( final Subscriber subscriber )
            throws InvalidEventTypeException
        {
            if( !m_eventClass.isAssignableFrom( subscriber.getEventType() ) )
            {
                throw new InvalidEventTypeException();
            }

            if( getLogger().isInfoEnabled() )
                getLogger().info( "Subscribing event " + subscriber.getEventType().getName() );

            // Add to list but prevent duplicate subscriptions
            if( !m_subscribers.containsKey( subscriber.getUID() ) )
            {
                m_subscribers.put( subscriber.getUID(), subscriber );
                if( getLogger().isInfoEnabled() )
                    getLogger().info( "Subscribed Event " + subscriber.getEventType().getName()
                                      + ", " + subscriber );

                if( getLogger().isDebugEnabled() )
                    getLogger().debug( "Subscribers now active: " + m_subscribers.size() );
            }
        }

        public void unsubscribe( Subscriber subscriber )
            throws InvalidEventTypeException
        {
            if( !m_eventClass.isAssignableFrom( subscriber.getEventType() ) )
            {
                throw new InvalidEventTypeException();
            }

            if( m_subscribers.containsKey( subscriber.getUID() ) )
            {
                m_subscribers.remove( subscriber.getUID() );

                if( getLogger().isInfoEnabled() )
                    getLogger().info( "Unsubscribed Event " + subscriber.getEventType().getName() );

                if( getLogger().isDebugEnabled() )
                    getLogger().debug( "Subscribers now active: " + m_subscribers.size() );
            }
            else
            {
                getLogger().warn( "Subscriber " + subscriber.getUID() + " not found" );
            }
        }
    }
}
