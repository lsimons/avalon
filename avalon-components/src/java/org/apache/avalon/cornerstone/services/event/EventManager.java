/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.event;

/**
 * <p>Service to manage event notification. The designed has been inspired by the paper by 
 * Gupta, S., J. M. Hartkopf, and S. Ramaswamy, in Java Report, Vol. 3, No. 7, July 1998, 19-36,
 * "Event Notifier: A Pattern for Event Notification".</p>  
 * 
 * <p>EventManager brokers events between a <tt>Publisher</tt>, which produces events,
 * and a <tt>Subscriber</tt>, which handles the notification of events.
 * A <tt>Filter</tt> discards events not of interest to a subscriber.
 * All Events have a common ancestor type <tt>Event</tt> and the event types are 
 * identified by a <tt>Class</tt>.</p>
 * 
 * @author Mauro Talevi
 */
public interface EventManager
{
 
    /**
     * Represents Role of the service
     */
     String ROLE = EventManager.class.getName(); 
    
     /**
      *  Returns the Publisher with which events can be published.
      */
     public Publisher getPublisher();
    
     /**
      *  Returns the Register with which subscribers can 
      *  subscribe and unsubscribe interest to given Events.
      */
     public Register getRegister();
     
}



