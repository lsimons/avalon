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
package my.avalon.jmx;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.lifecycle.Accessor;

import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 * (Un)Register object to the "jmx-server" service. The domaine is "Application"
 * and the name to used to identified object is:
 * <ul>
 *   <li>the return of the method MBeanable.getName() if object is instance of MBeanable.</li>
 *   <li>the value of "urn:avalon:name" store in context.</li>
 * </ul>
 *
 * To be auto-registered a MBean component need to include the stage "my.avalon.jmx.MBeanable".
 * The implementation of the interface my.avalon.jmx.MBeanable is optional (only if you want to choose your name).
 *
 * @avalon.type version="1.0" name="jmx-handler"
 * @avalon.extension type="my.avalon.jmx.MBeanable"
 */
public class MBeanableHandler extends AbstractLogEnabled implements Accessor, Serviceable {
    private MBeanServer jmxServer_;

    /**
    * Servicing of the component by the container during
    * which service dependencies declared under the component
    * can be resolved using the supplied service manager.
    *
    * @param manager the service manager
    * @avalon.dependency key="jmx-server" type="javax.management.MBeanServer" optional="true"
    */
    public void service(ServiceManager manager) throws ServiceException {
        if (manager.hasService("jmx-server")) {
            jmxServer_ = (MBeanServer) manager.lookup("jmx-server");
        }
    }

    /**
     * Register object to "jmx-server" service.
     */
    public void access(Object object, Context context)
                throws Exception {
        try {
            if (jmxServer_ != null) {
                ObjectName name = getObjectName(object, context);
                jmxServer_.registerMBean(object, name);
                getLogger().debug("register component : " + name);
            }
        } catch (Exception exc) {
            getLogger().warn("register", exc);
        }
    }

    /**
     * Unregister object form "jmx-server" service.
     */
    public void release(Object object, Context context) {
        try {
            if (jmxServer_ != null) {
                ObjectName name = getObjectName(object, context);
                jmxServer_.unregisterMBean(name);
                getLogger().debug("unregister component : " + name);
            }
        } catch (Exception exc) {
            getLogger().warn("unregister", exc);
        }
    }

    /**
     * build the ObjectName from source Object and it's context.
     * @param obj the object to register with returned ObjectName.
     * @param context context to use to find "urn:avalon:name" to use if obj isn't instanceof MBeanable
     * @return the builded ObjectName.
     */
    private ObjectName getObjectName(Object obj, Context context)
                              throws Exception {
        String back = null;

        if (obj instanceof MBeanable) {
            back = ((MBeanable) obj).getName();
        }

        if (back == null) {
            back = (String) context.get("urn:avalon:partition");
        }

        return new ObjectName("Application:name=" + back);
    }
}
