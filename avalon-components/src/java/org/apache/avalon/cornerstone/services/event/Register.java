/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.event;

/**
 * <tt>Register</tt> allows a <tt>Subscriber</tt> to subscribe to 
 * and unsubscribe from <tt>EventManager</tt>.
 *  
 * @author Mauro Talevi
 */
public interface Register
{
    
     /**
      * Subscribes a Subscriber to the EventManager.  
      * The Subscriber abstracts all the information needed for the subscription.
      * @param subscriber the Subscriber
      * @see Subscriber
      */
     public void subscribe( Subscriber subscriber );
    
     /**
      * Unsubscribes an Subscriber from the EventManager.
      * @param subscriber the Subscriber 
      * @see Subscriber
      */
     public void unsubscribe( Subscriber subscriber );
     
} 


