/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.component;

/**
 * A composer is a class that need to connect to software components using
 * a "role" abstraction, thus not depending on particular implementations
 * but on behavioral interfaces.
 * <br />
 *
 * The contract surrounding a <code>Composable</code> is that it is a user.
 * The <code>Composable</code> is able to use <code>Components</code> managed
 * by the <code>ComponentManager</code> it was initialized with.  As part
 * of the contract with the system, the instantiating entity must call
 * the <code>compose</code> method before the <code>Composable</code> 
 * can be considered valid. 
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Composable
{
    /**
     * Pass the <code>ComponentManager</code> to the <code>composer</code>.
     * The <code>Composable</code> implementation should use the specified
     * <code>ComponentManager</code> to acquire the components it needs for
     * execution.
     *
     * @param manager The <code>ComponentManager</code> which this
     *                <code>Composable</code> uses.
     */
    void compose( ComponentManager componentManager )
        throws ComponentException;
}
