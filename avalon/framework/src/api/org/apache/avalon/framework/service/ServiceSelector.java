/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.service;

/**
 * A <code>ServiceSelector</code> selects {@link Object}s based on a
 * supplied policy.  The contract is that all the {@link Object}s implement the
 * same role.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version 1.0
 * @see org.apache.avalon.framework.service.Serviceable
 * @see org.apache.avalon.framework.service.ServiceSelector
 */
public interface ServiceSelector
{
    /**
     * Select the {@link Object} associated with the given policy.
     * For instance, If the {@link ServiceSelector} has a
     * <code>Generator</code> stored and referenced by a URL, the
     * following call could be used:
     *
     * <pre>
     * try
     * {
     *     Generator input;
     *     input = (Generator)selector.select( new URL("foo://demo/url") );
     * }
     * catch (...)
     * {
     *     ...
     * }
     * </pre>
     *
     * @param policy A criteria against which a {@link Object} is selected.
     *
     * @return an {@link Object} value
     * @throws ServiceException If the requested {@link Object} cannot be supplied
     */
    Object select( Object policy )
        throws ServiceException;

    /**
     * Check to see if a {@link Object} exists relative to the supplied policy.
     *
     * @param policy a {@link Object} containing the selection criteria
     * @return True if the component is available, False if it not.
     */
    boolean isSelectable( Object policy );

    /**
     * Return the {@link Object} when you are finished with it.  This
     * allows the {@link ServiceSelector} to handle the End-Of-Life Lifecycle
     * events associated with the {@link Object}.  Please note, that no
     * Exception should be thrown at this point.  This is to allow easy use of the
     * ServiceSelector system without having to trap Exceptions on a release.
     *
     * @param object The {@link Object} we are releasing.
     */
    void release( Object object );

}
