/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.component;

/**
 * A <code>ComponentSelector</code> selects <code>Component</code>s based on a
 * hint.  The contract is that all the <code>Component</code>s implement the
 * same role.
 *
 * A role is better understood by the analogy of a play.  There are many
 * different roles in a script.  Any actor or actress can play any given part
 * and you get the same results (phrases said, movements made, etc.).  The exact
 * nuances of the performance is different.
 *
 * Below is a list of things that might be considered the same role:
 *
 * <ul>
 *   <li> XMLInputAdaptor and PropertyInputAdaptor</li>
 *   <li> FileGenerator   and SQLGenerator</li>
 * </ul>
 *
 * The <code>ComponentSelector</code> does not specify the methodology of
 * getting the <code>Component</code>, merely the interface used to get it.
 * Therefore the <code>ComponentSelector</code> can be implemented with a
 * factory pattern, an object pool, or a simple Hashtable.
 *
 * @see org.apache.avalon.framework.component.Component
 * @see org.apache.avalon.framework.component.Composable
 * @see org.apache.avalon.framework.component.ComponentManager
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface ComponentSelector
    extends Component
{
    /**
     * Select the <code>Component</code> associated with the given hint.
     * For instance, If the <code>ComponentSelector</code> has a
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
     * @param hint A hint to retrieve the correct <code>Component</code>.
     * @return the desired component
     * @throws ComponentException If the given role is not associated
     *                               with a <code>Component</code>, or a
     *                               <code>Component</code> instance cannot
     *                               be created.
     */
    Component select( Object hint )
        throws ComponentException;

    /**
     * Check to see if a <code>Component</code> exists for a hint.
     *
     * @param hint  a string identifying the role to check.
     * @return True if the component exists, False if it does not.
     */
    boolean hasComponent( Object hint );

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
