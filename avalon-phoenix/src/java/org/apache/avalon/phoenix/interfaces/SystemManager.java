/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;

/**
 * This component is responsible for managing the system.
 * This includes managing the embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface SystemManager
    extends Component, Initializable, Startable, Disposable
{
    String ROLE = "org.apache.avalon.phoenix.interfaces.SystemManager";

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register object under
     * @param object the object
     * @param interfaces the interfaces to register the component under
     * @exception ManagerException if an error occurs. An error could occur if the object doesn't
     *            implement the interfaces, the interfaces parameter contain non-instance
     *            classes, the name is already registered etc.
     * @exception IllegalArgumentException if object or interfaces is null
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
     * @exception ManagerException if an error occurs such as name already registered.
     * @exception IllegalArgumentException if object is null
     */
    void register( String name, Object object )
        throws ManagerException, IllegalArgumentException;

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @exception ManagerException if an error occurs such as when no such object registered.
     */
    void unregister( String name )
        throws ManagerException;
}
