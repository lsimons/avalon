/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.event;

/**
 * <tt>Subscriber</tt> registers its interest in a class of events and 
 * filters the events of which it should be notified.
 *
 * @author Mauro Talevi
 */
public interface Subscriber
{
    /** 
     *  Returns UID (Unique ID) set when the subscriber is created.
     *  The UID is required when the subscriber is remote, since the
     *  <tt>Filter</tt> will in general be deep-copied to a new object.
     * 
     *  @return the String encoding the UID
     */
    public String getUID();
    
    /**
     *  Returns the event type of the event on which the Subscriber is interested.
     *  The event type is encoded by a <tt>Class</tt>.
     *
     *  @return the <tt>Class</tt> encoding the event type  
     */
     public Class getEventType();

    /**
     *  Returns the filter used to select the events in which the subscriber is 
     *  interested.
     *
     *  @return the <tt>Filter</tt> 
     */
     public Filter getFilter();
     
    /**
     * Callback method informing the Subscriber of the occurence of an event.
     *
     * @param event the <tt>Event</tt> of which the <tt>Subscriber</tt> is informed
     */
     public void inform( Event event );
}


