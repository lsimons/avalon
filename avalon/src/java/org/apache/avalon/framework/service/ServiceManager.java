/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.service;

/**
 * A <code>ServiceManager</code> selects <code>Object</code>s based on a
 * role.  The contract is that all the <code>Object</code>s implement the
 * differing roles and there is one <code>Object</code> per role.  If you
 * need to select on of many <code>Object</code>s that implement the same
 * role, then you need to use a <code>ServiceSelector</code>.  Roles are
 * usually the full interface name.
 *
 * A role is better understood by the analogy of a play.  There are many
 * different roles in a script.  Any actor or actress can play any given part
 * and you get the same results (phrases said, movements made, etc.).  The exact
 * nuances of the performance is different.
 *
 * Below is a list of things that might be considered the different roles:
 *
 * <ul>
 *   <li> InputAdaptor and OutputAdaptor</li>
 *   <li> Store and Spool</li>
 * </ul>
 *
 * The <code>ServiceManager</code> does not specify the methodology of
 * getting the <code>Object</code>, merely the interface used to get it.
 * Therefore the <code>ServiceManager</code> can be implemented with a
 * factory pattern, an object pool, or a simple Hashtable.
 *
 * @author <a href="mailto:scoobie@betaversion.org">Federico Barbieri</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version 1.0
 * @see org.apache.avalon.framework.service.Composable
 * @see org.apache.avalon.framework.service.ServiceSelector
 *
 */
public interface ServiceManager
{
    /**
     * Get the <code>Object</code> associated with the given role.  For
     * instance, If the <code>ServiceManager</code> had a
     * <code>LoggerComponent</code> stored and referenced by role, I would use
     * the following call:
     * <pre>
     * try
     * {
     *     MyComponent log;
     *     myComponent = (MyComponent) manager.lookup(MyComponent.ROLE);
     * }
     * catch (...)
     * {
     *     ...
     * }
     * </pre>
     *
     * @param role The role name of the <code>Object</code> to retrieve.
     * @return an <code>Object</code> value
     * @throws ComponentException if an error occurs
     */
    Object lookup( String role )
        throws ServiceException;

    /**
     * Check to see if a <code>Object</code> exists for a role.
     *
     * @param role  a string identifying the role to check.
     * @return True if the object exists, False if it does not.
     */
    boolean hasService( String role );

    /**
     * Return the <code>Object</code> when you are finished with it.  This
     * allows the <code>ServiceManager</code> to handle the End-Of-Life Lifecycle
     * events associated with the <code>Object</code>.  Please note, that no
     * Exception should be thrown at this point.  This is to allow easy use of the
     * ComponentLocator system without having to trap Exceptions on a release.
     *
     * @param object The <code>Object</code> we are releasing.
     */
    void release( Object object );

}
