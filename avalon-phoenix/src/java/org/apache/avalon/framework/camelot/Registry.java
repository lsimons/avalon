/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.camelot;

import java.util.Iterator;
import org.apache.avalon.framework.component.Component;

/**
 * Represents a database of Infos.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Registry
    extends Component
{
    /**
     * register an info under a particular name.
     *
     * @param name the name
     * @param info the info
     * @exception RegistryException if info is invalid or name already contains info under name
     */
    void register( String name, Info info )
        throws RegistryException;

    /**
     * unregister an info.
     *
     * @param name the name of info
     * @exception RegistryException if no such info exists
     */
    void unregister( String name )
        throws RegistryException;

    /**
     * Retrieve an Info by name.
     *
     * @param name the name
     * @param clazz the expected class type of info
     * @return the Info
     * @exception RegistryException if an error occurs
     */
    Info getInfo( String name, Class clazz )
        throws RegistryException;

    /**
     * Return an iterator of all names of infos registered.
     *
     * @return the info names
     */
    Iterator getInfoNames();
}
