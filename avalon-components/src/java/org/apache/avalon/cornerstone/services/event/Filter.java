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
 * A Filter allows subscribers to specify which events
 * they should be informed of.  
 * It is Serializable to allow events to be published and received 
 * in a distributed architecture.
 *
 * @author Mauro Talevi
 */
public interface Filter extends Serializable
{
     /**
      * Filters event, discarding those not of interest to the subscriber.
      * 
      * @param event the <tt>Event</tt>
      * @return boolean <code>true</code> if Event passes filter
      */
     public boolean filter( Event event );

}



