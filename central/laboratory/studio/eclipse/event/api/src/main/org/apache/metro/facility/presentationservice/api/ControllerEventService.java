/*
 * EventServiceImpl.java
 *
 * Created on January 2, 2002, 8:45 PM
 */

package org.apache.metro.facility.presentationservice.api;

import org.apache.metro.facility.presentationservice.impl.ChannelEvent;
import org.apache.metro.facility.presentationservice.impl.ChannelException;

/**
 *
 * @author  default
 * @version 
 */
public interface ControllerEventService extends EventService {            

    /** Notifies all registered listeners about the event.
     *
     * @param e The event to be fired
     */
    public void modelChanged(ChannelEvent event) 
        throws ChannelException;  
    
    public void addControlClickedListener(String topic, 
            ChannelListener listener)
    	throws ChannelException;
    
    public void addWindowCreatedListener(String topic, 
            ChannelListener listener)
    	throws ChannelException;

}
