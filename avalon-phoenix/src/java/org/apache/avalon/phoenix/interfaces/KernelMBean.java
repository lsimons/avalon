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
 * @phoenix:mx-topic name="Kernel"
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface KernelMBean
{
    String ROLE = KernelMBean.class.getName();

    /**
     * Gets the list of applications running in the container
     *
     * @phoenix:mx-attribute
     *
     * @return applicationNames The array of application names
     */
    String[] getApplicationNames();

    /**
     * Removes the application from the container
     *
     * @phoenix:mx-operation
     *
     * @param name the name of application to remove
     */
    void removeApplication( String name )
        throws Exception;
}
