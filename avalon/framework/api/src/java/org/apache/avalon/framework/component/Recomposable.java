/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.component;

/**
 * Extends composer to allow recomposing.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Recomposable
    extends Composable
{
    void recompose( ComponentManager componentManager )
        throws ComponentException;
}
