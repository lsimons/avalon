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

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.service.ServiceManager;

public class FortressServletLauncher
    implements ServletContextListener
{


  protected ContainerManager m_manager = null;


  public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext context = event.getServletContext();

      // NOTE! the exact way to get the path my differ on your application server
      String configDir = context.getRealPath("/WEB-INF");

      // Set up all the preferences for Fortress
      final FortressConfig config = new FortressConfig();

      config.setContainerConfiguration(configDir + "/system.xconf");
      config.setRoleManagerConfiguration(configDir + "/system.roles");
      config.setLoggerManagerConfiguration(configDir + "/logkit.xconf");

      // Get the root container initialized
      m_manager = new DefaultContainerManager(config.getContext());
      ContainerUtil.initialize(m_manager);

      // get the default container and ServiceManager
      DefaultContainer container = (DefaultContainer) m_manager.getContainer();
      ServiceManager manager = container.getServiceManager();

      // store the ServiceManager in the ServletContext
      context.setAttribute(ServiceManager.class.getName(), manager);
    }
    catch (Exception ex) {
      event.getServletContext().log(ex,"Error initializing the Avalon Container");
    }
  }

  public void contextDestroyed(ServletContextEvent sce) {
    org.apache.avalon.framework.container.ContainerUtil.dispose( m_manager );
  }
}