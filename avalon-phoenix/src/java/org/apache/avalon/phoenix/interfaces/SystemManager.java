/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

/**
 * This component is responsible for managing the system.
 * This includes managing the embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface SystemManager
{
    String ROLE = SystemManager.class.getName();

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register object under
     * @param object the object
     * @param interfaces the interfaces to register the component under
     * @throws ManagerException if an error occurs. An error could occur if the object doesn't
     *            implement the interfaces, the interfaces parameter contain non-instance
     *            classes, the name is already registered etc.
     * @throws IllegalArgumentException if object or interfaces is null
     */
    void register( String name, Object object, Class[] interfaces )
        throws ManagerException, IllegalArgumentException;

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX). Note that the particular management scheme
     * will most likely use reflection to extract manageable information.
     *
     * @param name the name to register object under
     * @param object the object
     * @throws ManagerException if an error occurs such as name already registered.
     * @throws IllegalArgumentException if object is null
     */
    void register( String name, Object object )
        throws ManagerException, IllegalArgumentException;

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @throws ManagerException if an error occurs such as when no such object registered.
     */
    void unregister( String name )
        throws ManagerException;
}
