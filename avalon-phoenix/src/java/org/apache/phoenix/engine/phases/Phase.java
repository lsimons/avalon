/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import org.apache.avalon.Component;
import org.apache.avalon.util.Enum;
import org.apache.phoenix.engine.blocks.BlockVisitor;

/**
 * This represents a phase in applications lifecycle. 
 * Each phase is made up of a number of stages.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Phase
    extends BlockVisitor, Component
{  
    public class Traversal
        extends Enum
    {
        Traversal( final String name )
        {
            super( name );
        }
    }

    Traversal   FORWARD      = new Traversal( "FORWARD" );
    Traversal   REVERSE      = new Traversal( "REVERSE" );
    Traversal   LINEAR       = new Traversal( "LINEAR" );

    /**
     * Retrieve traversal that should be taken.
     *
     * @return the Traversal
     */
    Traversal getTraversal();
}
