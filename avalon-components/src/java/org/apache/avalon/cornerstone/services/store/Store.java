/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.store;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.phoenix.Service;

/**
 * @author Federico Barbieri <fede@apache.org>
 */
public interface Store
    extends ComponentSelector
{
    String ROLE = "org.apache.avalon.cornerstone.services.store.Store";

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
        throws ComponentException;
}
