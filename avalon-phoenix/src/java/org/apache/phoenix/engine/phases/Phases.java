/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import org.apache.avalon.camelot.State;

/**
 * This contains a list of constants representing phases in applications lifecycle.
 * Each phase is made up of a number of stages.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
interface Phases
{
    State      BASE        = new State( "BASE", 0 );
    State      STARTEDUP   = new State( "STARTEDUP", 10 );
    State      SHUTDOWN    = new State( "SHUTDOWN", 20 );
}
