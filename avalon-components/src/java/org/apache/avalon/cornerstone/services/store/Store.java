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
 * Allows selection from a number of configured Repositories.
 * Selection criterion is passed in as a <tt>Configuration</tt>
 * object.
 *
 * @see Repository
 * @see ObjectRepository
 * @see StreamRepository
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public interface Store
    extends ServiceSelector
{
    String ROLE = Store.class.getName();

    /**
     * Selects a Repository configured for the given <tt>policy</tt>.
     * The <tt>policy</tt> must be an instance of
     * {@link org.apache.avalon.framework.configuration.Configuration}.
     * The following attributes are used by the Store and thus are mandatory
     * in the <tt>policy</tt> parameter:
     * <pre>
     * &lt;repository destinationURL="[URL of this repository]"
     *             type="[repository type e.g. OBJECT, STREAM or MAIL]"
     *             model="[repository model e.g. PERSISTENT, CACHE]"&gt;
     *   [additional configuration]
     * &lt;/repository&gt;
     * </pre>
     * <p>
     * The <tt>policy</tt> is used both to select the appropriate
     * Repository and to configure it.
     * </p>
     *
     * @param policy a {@link org.apache.avalon.framework.configuration.Configuration} object identifying the sought Repository
     * @return requested {@link Repository}
     * @throws ServiceException if no repository matches <tt>policy</tt>
     */
    Object select( Object policy )
        throws ServiceException;
}
