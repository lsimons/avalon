/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import org.apache.avalon.camelot.State;
import org.apache.avalon.component.Component;
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
    State      BASE        = new State( "BASE", 0 );
    State      STARTEDUP   = new State( "STARTEDUP", 10 );
    State      SHUTDOWN    = new State( "SHUTDOWN", 20 );
}
