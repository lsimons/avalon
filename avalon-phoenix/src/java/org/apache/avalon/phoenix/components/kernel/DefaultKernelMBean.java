/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kernel;

import org.apache.jmx.introspector.ConstructiveMBean;

/**
 * The manager for the kernel. Used to test ConstructiveMBean.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultKernelMBean
    extends ConstructiveMBean
{
    public DefaultKernelMBean( final DefaultKernel kernel )
    {
        super( kernel, true );
    }

    /**
     * Utility method called to define manageable
     * objects attributes and operations.
     */
    protected void defineObject()
    {
        addAttribute( "applicationNames", false );

        final String[] params = new String[]{ String.class.getName() };
        addOperation( "getApplication", params, INFO );
    }
}
