/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis;

import org.apache.avalon.logger.AbstractLoggable;

/**
 * This is abstract implementation of SystemManager. 
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractSystemManager
    extends AbstractLoggable
    implements SystemManager
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
    public void register( final String name, final Object object, final Class[] interfaces )
        throws Exception
    {
    }

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX).
     *
     * @param name the name to register object under
     * @param object the object
     * @exception Exception if an error occurs such as name being already registered.
     */
    public void register( final String name, final Object object )
        throws Exception
    {
    }

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @exception Exception if an error occurs such as when no such object registered.
     */
    public void unregister( final String name )
        throws Exception
    {
    }
}
