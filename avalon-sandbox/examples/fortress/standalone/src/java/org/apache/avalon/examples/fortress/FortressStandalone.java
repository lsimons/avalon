/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.avalon.examples.fortress;

import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.service.ServiceManager;

import org.apache.avalon.examples.simple.Simple;

public class FortressStandalone {

  public FortressStandalone() {
  }

  public static void main( String[] args )
      throws Exception
  {

      // preferably we would override this with command line parameters
      String configDir = "src/conf";

      // Set up all the preferences for Fortress
      final FortressConfig config = new FortressConfig();

      config.setContainerConfiguration( configDir+"/system.xconf" );
      config.setRoleManagerConfiguration( configDir+"/system.roles" );
      config.setLoggerManagerConfiguration( configDir+"/logkit.xconf");


      // Get the root container initialized
      ContainerManager cm = new DefaultContainerManager( config.getContext() );
      ContainerUtil.initialize( cm );

      // get the default container and ServiceManager
      DefaultContainer container = (DefaultContainer) cm.getContainer();
      ServiceManager manager = container.getServiceManager();

      // we can now lookup our services
      Simple simple = (Simple) manager.lookup(Simple.class.getName());

      System.out.println("----------------");
      System.out.println(simple.getName());
      System.out.println("----------------");

      // don't forget to release
      manager.release(simple);

      // Properly clean up when we are done
      org.apache.avalon.framework.container.ContainerUtil.dispose( cm );
    }

}