/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.event;

/**
 * <tt>Publisher</tt> produces or publishes events that are brokered by <tt>EventManager</tt>
 * and of which the appropriate subscribers are informed of.
 *
 * @author Mauro Talevi  
 */
public interface Publisher
{
    /**
     *  Publishes an event for subscribers to be informed of.
     *
     *  @param event the <tt>Event</tt> being published
     */
    public void publish( Event event );

} 



