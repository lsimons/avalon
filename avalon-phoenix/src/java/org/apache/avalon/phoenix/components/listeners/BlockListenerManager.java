/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.listeners;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.phoenix.BlockListener;

/**
 * Interface to manage a set of <code>BlockListener</code> objects
 * and propogate <code>BlockEvent</code> notifications to these listeners.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface BlockListenerManager
    extends Component, BlockListener
{
    String ROLE = "org.apache.avalon.phoenix.components.listeners.BlockListenerManager";

    /**
     * Add a BlockListener to those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    void addBlockListener( BlockListener listener );

    /**
     * Remove a BlockListener from those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    void removeBlockListener( BlockListener listener );
}
