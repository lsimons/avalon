/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.store;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;

/**
 * @author Federico Barbieri <fede@apache.org>
 */
public interface Store
    extends ServiceSelector
{
    String ROLE = Store.class.getName();

    /**
     * This method accept a Configuration object as policy and returns the
     * corresponding Repository.
     * The Configuration must be in the form of:
     * <repository destinationURL="[URL of this repository]"
     *             type="[repository type ex. OBJECT or STREAM or MAIL etc.]"
     *             model="[repository model ex. PERSISTENT or CACHE etc.]">
     *   [addition configuration]
     * </repository>
     */
    Object select( Object policy )
        throws ServiceException;
}
