/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.tweety;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.avalon.framework.activity.*;
import org.apache.avalon.framework.component.*;
import org.apache.avalon.framework.configuration.*;
import org.apache.avalon.framework.context.*;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.framework.parameters.*;
import org.apache.avalon.framework.service.*;
import org.apache.avalon.framework.container.*;
   
/**
 * This is the main tweety class.
 * It reads the configuration file, and creates the components.
 *
 *@author     <a href="mailto:nicolaken@krysalis.org">Nicola Ken Barozzi</a>
 *@created    June 20, 2002
 *@version    1.0
 */
public class Tweety
{
	public static void main( String[] args )
	{
            Logger  sharedLogger = new ConsoleLogger();
            Context sharedContext = new DefaultContext();
            DefaultComponentManager sharedComponentManager = new DefaultComponentManager();
            DefaultServiceManager sharedServiceManager = new DefaultServiceManager();            
                        
          try {
          
            //load properties           
            Properties properties = new Properties();
            properties.load(new FileInputStream("tweety.properties"));
            
            //this will keep references to components so we know what to shutdown at the end
            Object[] components = new Object[properties.size()];
            
            //All the roles
            Enumeration roles = properties.propertyNames();

            //create and setup all the component 
            for (int i=0; roles.hasMoreElements(); i++) {

                //Get the role of the component being setup
                String role = (String )roles.nextElement();
             
                //create the component instance 
                Object component = Class.forName((String) properties.get(role));

                //setup the component by running the appropriate lifecycle methods in order

                ContainerUtil.enableLogging(component, sharedLogger);
                ContainerUtil.contextualize(component, sharedContext);
                ContainerUtil.compose      (component, sharedComponentManager);
                ContainerUtil.service      (component, sharedServiceManager);
                //ContainerUtil.configure    (component, configuration);
                //ContainerUtil.parameterize (component, parameters);
                ContainerUtil.initialize   (component);
                ContainerUtil.start        (component);

                //put the new component in the componentmanager and servicemanager
                sharedServiceManager.put(role, component);
                if(component instanceof org.apache.avalon.framework.component.Component){
                   sharedComponentManager.put(role, (Component) component);
                }   
            
                components[i]=component;
             }
             
             //
             // Here components that create threads continue operation till they wish
             //
             
             //shutdown all the components that were set up
             for (int i=0 ; i<components.length; i++ ) {
           
               Object component = components[i];
               
               //shutdown the component by running the appropriate lifecycle methods in order
               ContainerUtil.stop( component );
               ContainerUtil.dispose( component );
             }   
            
                
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
            System.err.println("Error reading configuration file.\nProgram Terminated");
            System.exit(-4);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error.\nProgram Terminated");
            System.exit(-2);
        }
      }
}






