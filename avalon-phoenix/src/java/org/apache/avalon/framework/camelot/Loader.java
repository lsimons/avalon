/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.camelot;

import org.apache.avalon.framework.component.Component;

/**
 * Class used to load resources from a source.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Loader
    extends Component
{
    /**
     * Retrieve classloader associated with source.
     *
     * @return the ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * Load an object from source.
     *
     * @param component the name of object
     * @return the Object
     * @exception FactoryException if an error occurs
     */
    Object load( String component )
        throws FactoryException;
}
