/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel.beanshell;

import org.apache.avalon.phoenix.components.kernel.DefaultKernel;

public class BeanShellKernel
    extends DefaultKernel
{

    /**
     * Overides Initialize from DefaultKernel
     */
    public void initialize()
        throws Exception
    {
        super.initialize();

        final BeanShellGUI beanShell = new BeanShellGUI( new BeanShellKernelProxy( this ) );
        beanShell.init();
    }
}