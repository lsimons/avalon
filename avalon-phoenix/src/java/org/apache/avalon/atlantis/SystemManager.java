/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis;

import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.Startable;
import org.apache.avalon.Stoppable;
import org.apache.avalon.component.Component;

/**
 * This component is responsible for managing the system.
 * This includes managing the embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface SystemManager
    extends Component, Initializable, Startable, Stoppable, Disposable
{
    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register object under
     * @param object the object
     * @param interfaces the interfaces to register the component under
     * @exception Exception if an error occurs. An error could occur if the object doesn't 
     *            implement the interfaces, the interfaces parameter contain non-instance 
     *            classes, the name is already registered etc.
     */
    void register( String name, Object object, Class[] interfaces )
        throws Exception;

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX).
     *
     * @param name the name to register object under
     * @param object the object
     * @exception Exception if an error occurs such as name being already registered.
     */
    void register( String name, Object object )
        throws Exception;

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @exception Exception if an error occurs such as when no such object registered.
     */
    void unregister( String name )
        throws Exception;
}
