/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.interfaces;

/**
 * This is the interface via which you can manager
 * the root container of Applications.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface KernelMBean
{
    String ROLE = KernelMBean.class.getName();

    String[] getApplicationNames();

    void removeApplication( String name )
        throws Exception;
}
