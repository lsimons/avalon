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
public interface ViewEventService extends EventService {            

    /** Notifies all registered listeners about the event.
     *
     * @param e The event to be fired
     */
    public ChannelEvent controlClicked(ChannelEvent event) 
        throws ChannelException;
    
    public ChannelEvent windowCreated(ChannelEvent event) 
    	throws ChannelException;    

}
