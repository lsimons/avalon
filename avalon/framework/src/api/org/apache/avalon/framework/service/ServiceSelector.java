/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.service;

/**
 * A <code>ServiceSelector</code> selects <code>Object</code>s based on a
 * supplied policy.  The contract is that all the <code>Object</code>s implement the
 * same role.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version 1.0
 * @see org.apache.avalon.framework.service.Serviceable
 * @see org.apache.avalon.framework.service.ServiceSelector
 *
 */
public interface ServiceSelector
{
    /**
     * Select the <code>Object</code> associated with the given policy.
     * For instance, If the <code>ServiceSelector</code> has a
     * <code>Generator</code> stored and referenced by a URL, I would use the
     * following call:
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
     * @param policy A criteria against which a <code>Object</code> is selected.
     *
     * @return an <code>Object</code> value
     * @throws ComponentException If the requested <code>Object</code> cannot be supplied
     */
    Object select( Object policy )
        throws ServiceException;

    /**
     * Check to see if a <code>Object</code> exists relative to the supplied policy.
     *
     * @param policy a <code>Object</code> containing the selection criteria
     * @return True if the component is available, False if it not.
     */
    boolean isSelectable( Object policy );

    /**
     * Return the <code>Object</code> when you are finished with it.  This
     * allows the <code>ServiceSelector</code> to handle the End-Of-Life Lifecycle
     * events associated with the <code>Object</code>.  Please note, that no
     * Exception should be thrown at this point.  This is to allow easy use of the
     * ServiceSelector system without having to trap Exceptions on a release.
     *
     * @param object The <code>Object</code> we are releasing.
     */
    void release( Object object );


}
