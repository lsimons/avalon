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
package my.avalon.jmx.tools;

import my.avalon.jmx.MBeanable;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import java.util.Properties;


/**
 * This Component provide System and Runtime information as MBean.
 * Useness for monitoring.
 *
 * @jmx.mbean
 * @avalon.meta.version 1.0
 * @avalon.meta.name system-info
 * @avalon.meta.stage type="my.avalon.jmx.MBeanable"
 */
public class SystemInfo extends AbstractLogEnabled implements Initializable, SystemInfoMBean, MBeanable {
    private ThreadGroup rootThreadGroup_;

    public void initialize() throws Exception {
        ThreadGroup t = Thread.currentThread().getThreadGroup();

        while (t != null) {
            rootThreadGroup_     = t;
            t                    = t.getParent();
        }
    }

    public String getName() {
        return "SystemInfo";
    }

    /**
     * @jmx.managed-attribute description="Returns the amount of free memory in the system. see Runtime.freeMemory()"
     */
    public long getFreeMemory() throws Exception {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * @jmx.managed-attribute description="Returns the total amount of memory in the Java Virtual Machine. see Runtime.totalMemory()"
     */
    public long getTotalMemory() throws Exception {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * @jmx.managed-attribute description="Returns the ratio (%) free/total amount of memory in the Java Virtual Machine."
     */
    public int getRatioMemory() throws Exception {
        return (int) ((Runtime.getRuntime().freeMemory() * 100) / Runtime.getRuntime().totalMemory());
    }

    /**
     * @jmx.managed-operation description="Runs the garbage collector. see Runtime.gc()"
     */
    public void gc(int status) throws Exception {
        Runtime.getRuntime().gc();
    }

    /**
     * @jmx.managed-operation description="Terminates the currently running Java virtual machine by initiating its shutdown sequence. see Runtime.exit(int)"
     */
    public void exit(int status) throws Exception {
        Runtime.getRuntime().exit(status);
    }

    /**
     * @jmx.managed-operation description="Enables/Disables tracing of instructions. see Runtime.traceInstructions(boolean)"
     */
    public void traceInstructions(boolean on) throws Exception {
        Runtime.getRuntime().traceInstructions(on);
    }

    /**
     * @jmx.managed-operation description="Enables/Disables tracing of method calls. see Runtime.traceMethodCalls(boolean)"
     */
    public void traceMethodCalls(boolean on) throws Exception {
        Runtime.getRuntime().traceMethodCalls(on);
    }

    /**
     * @jmx.managed-operation description="Determines the current system properties. see System.getProperties()"
     */
    public Properties showProperties() throws Exception {
        return System.getProperties();
    }

    /**
     * @jmx.managed-attribute description="Gets the system property indicated by the specified key. see System.getProperty(String)"
     */
    public String getProperty(String key) throws Exception {
        return getProperty(key);
    }

    /**
     * @jmx.managed-attribute description="Returns an estimate of the number of active threads."
     */
    public int getNumberOfActiveThreads() throws Exception {
        return rootThreadGroup_.activeCount();
    }
}
