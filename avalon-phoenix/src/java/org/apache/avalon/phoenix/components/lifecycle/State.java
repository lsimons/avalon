/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.lifecycle;

import org.apache.avalon.framework.ValuedEnum;

/**
 * Defines possible states for contained components.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class State
    extends ValuedEnum
{
    /**
     * VOID is the initial state of all components.
     */
    public final static State VOID = new State( "VOID", 0 );

    /**
     * CREATING indicates that the Component is in process of being created.
     */
    public final static State CREATING = new State( "CREATING", 0 );

    /**
     * CREATED is the state the component exists in after it has been
     * successfully created but before it has been prepared.
     */
    public final static State CREATED = new State( "CREATED", 0 );

    /**
     * READYING indicates that the component is being prepared for service.
     * In terms of Avalons Component Lifecycle this would indicate Loggable,
     * Contextualizable, Composable, Configurable and Initializable stages.
     */
    public final static State READYING = new State( "READYING", 0 );

    /**
     * READY indicates that the component is ready to be started
     * or destroyed as appropriate.
     */
    public final static State READY = new State( "READY", 0 );

    /**
     * STARTING indicates that the component is being started.
     */
    public final static State STARTING = new State( "STARTING", 0 );

    /**
     * STARTED indicates that the component has been started.
     */
    public final static State STARTED = new State( "STARTED", 0 );

    /**
     * STOPPING indicates that the component is being stopped.
     */
    public final static State STOPPING = new State( "STOPPING", 0 );

    /**
     * STOPPED indicates that the component has been stopped.
     */
    public final static State STOPPED = new State( "STOPPED", 0 );

    /**
     * DESTROYING indicates that the component is being destroyed.
     */
    public final static State DESTROYING = new State( "DESTROYING", 0 );

    /**
     * DESTROYED indicates that the component has been destroyed.
     */
    public final static State DESTROYED = new State( "DESTROYED", 0 );

    /**
     * FAILED indicates that the component is in a FAILED state. This is
     * usually the result of an error during one of the transition states.
     */
    public final static State FAILED = new State( "FAILED", 0 );

    protected State( final String name, final int value )
    {
        super( name, value );
    }
}
