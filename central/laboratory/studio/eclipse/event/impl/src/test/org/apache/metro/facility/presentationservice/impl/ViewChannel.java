/*

   Copyright 2004. The Apache Software Foundation.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 

*/
package org.apache.metro.facility.presentationservice.impl;

import org.apache.metro.facility.presentationservice.api.ChannelListener;
import org.apache.metro.facility.presentationservice.api.ControllerEventService;
import org.apache.metro.facility.presentationservice.api.IViewChannel;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team</a>
 * 21.08.2004
 * last change:
 * 
 */
public class ViewChannel implements IViewChannel
{
    private ControllerEventService service;
    private String name;
    /**
     * 
     */
    public ViewChannel(String name)
    {
        super();
        try
        {
            this.name = name;
            service = PresentationServiceFactory.getControllerEventService();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addControlClickedListener(String topic, ChannelListener listener) throws ChannelException
    {
        service.addControlClickedListener(topic, listener);
        
    }

    public void addWindowCreatedListener(String topic, ChannelListener listener) throws ChannelException
    {
        service.addWindowCreatedListener(topic, listener);
        
    }

}
