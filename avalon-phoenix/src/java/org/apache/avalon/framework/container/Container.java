/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.container;

import java.util.Iterator;
import org.apache.avalon.framework.component.Component;

/**
 * This contains it during execution and may provide certain
 * facilities (like a thread per EJB etc).
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Container
    extends Component
{
    String ROLE = "org.apache.avalon.framework.camelot.Container";

    /**
     * Add a component instance to container.
     *
     * @param entry the component entry
     */
    void add( String name, Entry entry )
        throws ContainerException;

    /**
     * Remove a component instance from container.
     *
     * @param name the name of component
     */
    void remove( String name )
        throws ContainerException;

    /**
     * Retrieve Entry from container
     *
     * @param name the name of entry
     * @return the entry
     */
    Entry getEntry( String name )
        throws ContainerException;

    /**
     * List all names of entries in container.
     *
     * @return the list of all entries
     */
    Iterator list();
}
