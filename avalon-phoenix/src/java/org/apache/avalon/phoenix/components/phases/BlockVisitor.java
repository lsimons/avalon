/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.phoenix.engine.blocks.BlockEntry;

/**
 * Visitor interface that objects implement to walk the DAG.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface BlockVisitor
    extends Component
{
    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param name the name of block
     * @param entry the BlockEntry
     * @exception ApplicationException if walking is to be stopped
     */
    void visitBlock( String name, BlockEntry entry )
        throws Exception;
}
