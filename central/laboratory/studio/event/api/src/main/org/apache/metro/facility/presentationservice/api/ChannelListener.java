/*
 * TemperatureChangeListener.java
 *
 * Created on January 2, 2002, 7:34 PM
 */

package org.apache.metro.facility.presentationservice.api;

import org.apache.metro.facility.presentationservice.impl.ChannelEvent;
import org.apache.metro.facility.presentationservice.impl.ChannelException;
/**
 *
 * @author  default
 * @version 
 */
public interface ChannelListener extends java.util.EventListener {

    public ChannelEvent notify(ChannelEvent evt) throws ChannelException;
    
}

