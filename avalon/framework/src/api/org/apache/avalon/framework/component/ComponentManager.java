/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.component;

/**
 * A <code>ComponentManager</code> selects <code>Component</code>s based on a
 * role.  The contract is that all the <code>Component</code>s implement the
 * differing roles and there is one <code>Component</code> per role.  If you
 * need to select on of many <code>Component</code>s that implement the same
 * role, then you need to use a <code>ComponentSelector</code>.  Roles are
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
 * The <code>ComponentManager</code> does not specify the methodology of
 * getting the <code>Component</code>, merely the interface used to get it.
 * Therefore the <code>ComponentManager</code> can be implemented with a
 * factory pattern, an object pool, or a simple Hashtable.
 *
 * @see org.apache.avalon.framework.component.Component
 * @see org.apache.avalon.framework.component.Composable
 * @see org.apache.avalon.framework.component.ComponentSelector
 *
 * @author <a href="mailto:scoobie@betaversion.org">Federico Barbieri</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface ComponentManager
{
    /**
     * Get the <code>Component</code> associated with the given role.  For
     * instance, If the <code>ComponentManager</code> had a
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
     * @param name The role name of the <code>Component</code> to retrieve.
     * @exception ComponentException if an error occurs
     */
    Component lookup( String role )
        throws ComponentException;

    /**
     * Check to see if a <code>Component</code> exists for a role.
     *
     * @param role  a string identifying the role to check.
     * @return True if the component exists, False if it does not.
     */
    boolean hasComponent( String role );

    /**
     * Return the <code>Component</code> when you are finished with it.  This
     * allows the <code>ComponentManager</code> to handle the End-Of-Life Lifecycle
     * events associated with the Component.  Please note, that no Exceptions
     * should be thrown at this point.  This is to allow easy use of the
     * ComponentManager system without having to trap Exceptions on a release.
     *
     * @param component The Component we are releasing.
     */
    void release( Component component );
}
