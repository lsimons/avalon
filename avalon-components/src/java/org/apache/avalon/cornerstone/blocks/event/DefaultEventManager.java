/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.event;

import java.util.Hashtable;
import java.util.Enumeration;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.phoenix.BlockContext;

import org.apache.avalon.cornerstone.services.event.Event;
import org.apache.avalon.cornerstone.services.event.Filter;
import org.apache.avalon.cornerstone.services.event.EventManager;
import org.apache.avalon.cornerstone.services.event.Publisher;
import org.apache.avalon.cornerstone.services.event.Register;
import org.apache.avalon.cornerstone.services.event.Subscriber;

/**
 * EventManager Block
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.event.EventManager"
 *
 * @author Mauro Talevi
 */
public class DefaultEventManager extends AbstractLogEnabled
    implements EventManager,
               Contextualizable, Configurable, Serviceable, Initializable, Disposable
{
    private final String m_rootEventType = "Event";
    private Class m_eventClass;
    private Publisher m_publisher = new DefaultPublisher();
    private Register m_register = new DefaultRegister();
    private Hashtable m_subscribers = new Hashtable();
    
    public Publisher getPublisher(){
        return m_publisher;
    }
    public Register getRegister(){
        return m_register;
    }
    
    public void contextualize( final Context context )
    {
    }
    
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
    }

    /**
     * ServiceManager dependencies
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
    }

    public void initialize()
        throws Exception
    {            
        m_eventClass = Class.forName( m_rootEventType );
        getLogger().info("Initialising eventClass " + m_eventClass);
    }

    public void dispose()
    {
    }
    
    class DefaultPublisher implements Publisher
    {
        public void publish( final Event event ) 
        {
            getLogger().info("Publishing event " + event.getClass());
            System.out.println("Publishing event " + event.getClass());
            for ( Enumeration e = m_subscribers.elements(); e.hasMoreElements(); ){
                Subscriber subscriber = (Subscriber)e.nextElement();
                if (subscriber.getEventType().isAssignableFrom(event.getClass())
                && (subscriber.getFilter() != null ||
                subscriber.getFilter().filter(event))){
                    getLogger().info("Informing subscriber "+subscriber+" of event "+event.getClass());
                    subscriber.inform(event);
                }
            }
        }
    }
    
    class DefaultRegister implements Register
    {
        public void subscribe( final Subscriber subscriber )
            throws InvalidEventTypeException
        {
            if ( !m_eventClass.isAssignableFrom( subscriber.getEventType() ) )
                throw new InvalidEventTypeException();
            
            getLogger().info( "Subscribing event " + subscriber.getEventType().getName() );
            // Add to list but prevent duplicate subscriptions
            if ( !m_subscribers.containsKey( subscriber.getUID() ) ){
                m_subscribers.put( subscriber.getUID(), subscriber );
                getLogger().info( "Subscribed Event " + subscriber.getEventType().getName() );
                if ( getLogger().isDebugEnabled() ){
                    getLogger().debug( "Subscribers now active: " + m_subscribers.size() );
                }
            }
        }
        public void unsubscribe( Subscriber subscriber )
            throws InvalidEventTypeException 
        {
            if ( !m_eventClass.isAssignableFrom( subscriber.getEventType() ) )
                throw new InvalidEventTypeException();
            if ( m_subscribers.containsKey( subscriber.getUID() ) ){
                m_subscribers.remove( subscriber.getUID() );
                getLogger().info( "Unsubscribed Event " + subscriber.getEventType().getName() );
                if ( getLogger().isDebugEnabled() ){
                    getLogger().debug( "Subscribers now active: " + m_subscribers.size() );
                }
            } else {
                getLogger().warn( "Subscriber " + subscriber.getUID() + " not found" );
            }
        }
    }
    
}
