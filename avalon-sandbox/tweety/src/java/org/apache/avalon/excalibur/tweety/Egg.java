/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
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
 * This is a stripped down - collapsed version of Tweety+Main.
 * It reads the configuration file, and creates the components.
 * Then it disposes them.
 *
 * Useful to understand what Lifecycle is, without getting yet into 
 * what a Container is; this is application+Container all in one.
 *
 * After you understand this, look at Main and Tweety.
 *
 *@author     <a href="mailto:nicolaken@apache.org">Nicola Ken Barozzi</a>
 *@created    June 20, 2002
 *@version    1.0
 */
public class Egg
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
