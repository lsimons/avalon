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

package org.apache.avalon.fortress.tools;

import java.lang.reflect.Method;

import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Bean for making it easier to run Fortress, for example as Ant task.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/26 22:42:03 $
 */
public class FortressBean implements Initializable, LogEnabled, Serviceable, Disposable {

    private final FortressConfig config = new FortressConfig();
    private Logger logger = null;
    private DefaultContainerManager cm;
    private DefaultContainer container;
    private ServiceManager sm;

    private String lookupComponentRole = null;
    private String invokeMethod = null;

    private boolean systemExitOnDispose = true;

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    protected final Logger getLogger() {
        if (this.logger == null) this.logger = new ConsoleLogger();
        return this.logger;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {
        //only initialize if we do not already have a servicemanager passed in from outside
        if (this.sm == null) {
            if (Thread.currentThread().getContextClassLoader() == null) {
                if (this.getClass().getClassLoader() != null) {
                    ClassLoader cl = this.getClass().getClassLoader();
                    config.setContextClassLoader(cl);
                    Thread.currentThread().setContextClassLoader(cl);
                } else {
                    getLogger().warn("context classloader not set and class classloader is null!");
                }
            }
            // Get the root container initialized
            this.cm = new DefaultContainerManager(config.getContext());
            ContainerUtil.initialize(cm);

            this.container = (DefaultContainer) cm.getContainer();
            this.sm = container.getServiceManager();
        }
    }

    public void run() throws Exception {
        Object component = getServiceManager().lookup(lookupComponentRole);
        Method method = component.getClass().getMethod(invokeMethod, null);
        method.invoke(component, null);
    }

    /**
     * Implementation execute() method for Ant compatability.
     */
    public void execute() {
        try {
            initialize();
            try {
                run();
            } catch (Exception e) {
                getLogger().error("error while running", e);
            }
        } catch (Exception e) {
            getLogger().error("error while initializing", e);
        }
        dispose();
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager sm) throws ServiceException {
        if (this.sm == null) this.sm = sm;
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        // Properly clean up when we are done
        org.apache.avalon.framework.container.ContainerUtil.dispose( cm );
        
        //system exit, in case we were running some GUI and some thread is still active
        if (this.systemExitOnDispose) {
            Thread.yield();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //ignore
            }
            System.exit(0);
        }
    }

    protected ServiceManager getServiceManager() {
        return this.sm;
    }

    /**
     * The container implementation has to be a subclass of 
     * <code>org.apache.avalon.fortress.impl.DefaultContainer</code>.
     * 
     * @param containerClass fully qualified class name of the container implementation class.
     */
    public void setContainerClass(String containerClass) throws Exception {
        config.setContextClassLoader(getClass().getClassLoader());
        config.setContainerClass(containerClass);
    }

    public void setContainerConfiguration(String containerConfiguration) {
        config.setContainerConfiguration(containerConfiguration);
    }

    public void setContextDirectory(String contextDirectory) {
        config.setContextDirectory(contextDirectory);
    }

    public void setInstrumentManagerConfiguration(String instrumentManagerConfiguration) {
        config.setInstrumentManagerConfiguration(instrumentManagerConfiguration);
    }

    public void setLoggerManagerConfiguration(String loggerManagerConfiguration) {
        config.setLoggerManagerConfiguration(loggerManagerConfiguration);
    }

    public void setRoleManagerConfiguration(String roleManagerConfiguration) {
        config.setRoleManagerConfiguration(roleManagerConfiguration);
    }

    public void setWorkDirectory(String workDirectory) {
        config.setWorkDirectory(workDirectory);
    }

    public void setInvokeMethod(String invokeMethod) {
        this.invokeMethod = invokeMethod;
    }

    public void setLookupComponentRole(String lookupComponentRole) {
        this.lookupComponentRole = lookupComponentRole;
    }

    /**
     * Should we call System.exit(0) after we are finished with processing.
     * Useful if the components have a GUI and there are some threads running that
     * do not allow the JVM to exit.
     */
    public void setSystemExitOnDispose(boolean systemExitOnDispose) {
        this.systemExitOnDispose = systemExitOnDispose;
    }

}
