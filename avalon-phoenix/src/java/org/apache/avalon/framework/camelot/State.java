/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.camelot;

import org.apache.avalon.framework.ValuedEnum;

/**
 * Defines possible states for contained components.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class State
    extends ValuedEnum
{

    //A list of constants representing phases in Blocks lifecycle.
    //Each phase is made up of a number of stages.
    public final static State  BASE       = new State( "BASE", 0 );
    public final static State  STARTEDUP  = new State( "STARTEDUP", 10 );
    public final static State  SHUTDOWN   = new State( "SHUTDOWN", 20 );

    private State( final String name, final int value )
    {
        super( name, value );
    }
}
