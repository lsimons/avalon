/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.framework.ValuedEnum;

/**
 * Defines possible states for contained components.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
final class State
    extends ValuedEnum
{
    /**
     * VOID is the initial state of all components.
     */
    public static final State VOID = new State( "VOID", 0 );

    /**
     * CREATING indicates that the Component is in process of being created.
     */
    public static final State CREATING = new State( "CREATING", 0 );

    /**
     * CREATED is the state the component exists in after it has been
     * successfully created but before it has been prepared.
     */
    public static final State CREATED = new State( "CREATED", 0 );

    /**
     * DESTROYING indicates that the component is being destroyed.
     */
    public static final State DESTROYING = new State( "DESTROYING", 0 );

    /**
     * DESTROYED indicates that the component has been destroyed.
     */
    public static final State DESTROYED = new State( "DESTROYED", 0 );

    /**
     * FAILED indicates that the component is in a FAILED state. This is
     * usually the result of an error during one of the transition states.
     */
    public static final State FAILED = new State( "FAILED", 0 );

    protected State( final String name, final int value )
    {
        super( name, value );
    }
}
