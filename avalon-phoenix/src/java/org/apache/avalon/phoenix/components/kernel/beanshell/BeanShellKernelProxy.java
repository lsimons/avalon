/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel.beanshell;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.metadata.SarMetaData;

public class BeanShellKernelProxy
    implements Kernel
{
    private transient Kernel m_kernel;

    /**
     * Construct a Proxy to the Kernel that does not implement all methods.
     */
    public BeanShellKernelProxy( final Kernel kernel )
    {
        m_kernel = kernel;
    }

    public void addApplication( SarMetaData metaData,
                                ClassLoader classLoader,
                                Logger hierarchy,
                                Configuration server )
        throws Exception
    {
        throw new UnsupportedOperationException( "This is not supported for non-kernel visitors" );
    }

    public void removeApplication( final String name )
        throws Exception
    {
        m_kernel.removeApplication( name );
    }

    public Application getApplication( final String name )
    {
        return m_kernel.getApplication( name );
    }

    public String[] getApplicationNames()
    {
        return m_kernel.getApplicationNames();
    }
}