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
package org.apache.metro.facility.presentationservice.test;

import junit.framework.TestCase;

import org.apache.metro.facility.presentationservice.api.ChannelListener;
import org.apache.metro.facility.presentationservice.api.IViewChannel;
import org.apache.metro.facility.presentationservice.api.ChannelEvent;
import org.apache.metro.facility.presentationservice.api.ChannelException;
import org.apache.metro.facility.presentationservice.impl.ViewChannel;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team</a>
 * 21.08.2004
 * last change:
 * 
 */
public class ModellMock extends TestCase
{

    public static void main(String[] args)
    {
    }

    public void initialize()
    {
        try
        {

            IViewChannel channel = new ViewChannel("test");
            
            channel.addControlClickedListener("apply", new ChannelListener (){
                public ChannelEvent notify(ChannelEvent event) throws ChannelException
                {
                    System.out.println("Hallo!!! " + event.getValue("field1"));
                    event.putValue("field1", "return message");                    
                    return event;
            
                }          
            });
        } catch (ChannelException e)
        {
            fail("unable to perform clicked event");
        }

    }

}