/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.avalon.phoenix.components.kernel.beanshell;

import org.apache.avalon.phoenix.components.kernel.DefaultKernel;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.excalibur.proxy.DynamicProxy;

public class BeanShellKernel extends DefaultKernel
{

    private Kernel m_kernel;

    /**
     * Overides Initialize from DefaultKernel
     *
     * @throws Exception
     */
    public void initialize() throws Exception
    {
        super.initialize();
        m_kernel = (Kernel) DynamicProxy.newInstance(
                new BeanShellKernelProxy(this), new Class[] {Kernel.class});

        BeanShellGUI beanShell = new BeanShellGUI(m_kernel);
        beanShell.init();
    }


}