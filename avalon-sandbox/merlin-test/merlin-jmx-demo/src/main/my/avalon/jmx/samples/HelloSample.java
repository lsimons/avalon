/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 *
 * http://www.apache.org/
 */
package my.avalon.jmx.samples;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 * HelloSample sample of avalon component usable throught jmx.
 * This implementation don't use stage but need direct access to "jmx-server" service, and
 * control when (un)register.
 * <ul>
 *   <li>this implementation is more portable (I think it could be work on Fortress,
 *        because don't use any container special features).</li>
 *   <li>this implementation require to include all the (un)registering code (and search of the jmx-server)</li>
 * </ul>
 *
 * @jmx.mbean
 * @avalon.meta.version 1.0
 * @avalon.meta.name jmx-hello
 */
public class HelloSample extends AbstractLogEnabled implements HelloSampleMBean, Serviceable, Configurable, Initializable, Executable, Disposable {
    private MBeanServer jmxServer_;
    private ObjectName  jmxName_;

    /**
     * Servicing of the component by the container during
     * which service dependencies declared under the component
     * can be resolved using the supplied service manager.
     *
     * @param manager the service manager
     * @avalon.meta.dependency key="jmx-server" type="javax.management.MBeanServer"
     */
    public void service(ServiceManager manager) throws ServiceException {
        jmxServer_ = (MBeanServer) manager.lookup("jmx-server");
    }

    /**
     * Configuration of the component by the container.  The
     * implementation get a child element named 'source' and
     * assigns the value of the element to a local variable.
     *
     * @param config the component configuration
     * @exception ConfigurationException if a configuration error occurs
     */
    public void configure(Configuration config) throws ConfigurationException {
        try {
            jmxName_ = new ObjectName(config.getAttribute("jmxName", "Application:name=HelloSample"));
        } catch (Exception exc) {
            throw new ConfigurationException("", exc);
        }
    }

    /**
     * Initialization of the component by the container.
     * @exception Exception if an initialization error occurs
     */
    public void initialize() throws Exception {
        getLogger().info("initialize");
        jmxServer_.registerMBean(this, jmxName_);
    }

    /**
     * log a info message "execute".
     * @jmx.managed-operation
     */
    public void execute() throws Exception {
        getLogger().info(this.getClass() + " : execute");
    }

    public void dispose() {
        getLogger().info("dispose");

        try {
            jmxServer_.unregisterMBean(jmxName_);
        } catch (Exception exc) {
            getLogger().warn("", exc);
        }
    }
}
