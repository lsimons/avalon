/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.component;

/**
 * Extends composer to allow recomposing.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version 1.0
 */
public interface Recomposable
    extends Composable
{
    /**
     * Repass the <code>ComponentManager</code> to the <code>composer</code>.
     * The <code>Composable</code> implementation should use the specified
     * <code>ComponentManager</code> to acquire the components it needs for
     * execution. It should also drop references to any components it
     * retrieved from old ComponentManager.
     *
     * @param componentManager The <code>ComponentManager</code> which this
     *                <code>Composable</code> uses.
     * @throws ComponentException if an error occurs
     */
    void recompose( ComponentManager componentManager )
        throws ComponentException;
}
