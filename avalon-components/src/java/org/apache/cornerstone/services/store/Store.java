/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.store;

import org.apache.avalon.Component;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.ComponentSelector;
import org.apache.phoenix.Service;

/**
 * @author Federico Barbieri <fede@apache.org>
 */
public interface Store
    extends Service, ComponentSelector
{
    /**
     * This method accept a Configuration object as hint and return the
     * corresponding Repository.
     * The Configuration must be in the form of:
     * <repository destinationURL="[URL of this repository]"
     *             type="[repository type ex. OBJECT or STREAM or MAIL etc.]"
     *             model="[repository model ex. PERSISTENT or CACHE etc.]">
     *   [addition configuration]
     * </repository>
     */
    Component select( Object hint )
        throws ComponentManagerException;
}
