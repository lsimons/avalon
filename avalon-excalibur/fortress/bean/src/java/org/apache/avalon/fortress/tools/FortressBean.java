/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import org.apache.commons.logging.impl.AvalonLogger;

/**
 * Bean for making it easier to run Fortress, for example as Ant task.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/12/01 18:04:15 $
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
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            // Get the root container initialized
            this.cm = new DefaultContainerManager(config.getContext());
            ContainerUtil.initialize(cm);
            // set the static logger for commons logging 
            AvalonLogger.setDefaultLogger(cm.getLogger());
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.AvalonLogger");

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
        System.getProperties().remove("org.apache.commons.logging.Log");
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
