/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.event;

import java.io.Serializable;
/**
 * <tt>Event</tt> is a common ancestor type for all events.
 *
 * It is Serializable to allow events to be published and received 
 * in a distributed architecture.
 *
 * @author Mauro Talevi
 */
public interface Event extends Serializable
{
    
   /** 
     * Returns the event type
     * @return int representing the event type
     */
    public int getType();
    
    /** 
     * Returns the event source
     * @return Object representing the event source
     */
    public Object getSource();

}


