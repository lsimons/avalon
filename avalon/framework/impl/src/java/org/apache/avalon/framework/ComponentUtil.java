/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework;


import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.parameters.Parameterizable;

/**
 * This class provides basic facilities for enforcing Avalon's contracts
 * within your own code.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.3 $ $Date: 2001/11/30 17:17:27 $
 */
public final class ComponentUtil
{
    public static final long LOG_ENABLED    = 0x00000001;
    public static final long CONTEXTUALIZED = 0x00000002;
    public static final long PARAMETERIZED  = 0x00000004;
    public static final long CONFIGURED     = 0x00000008;
    public static final long COMPOSED       = 0x00000010;
    public static final long ACTIVE         = 0x10000000;
    public static final long INITIALIZED    = 0x00000012;
    public static final long STARTED        = 0x00000014;
    public static final long SUSPENDED      = 0x01000000;
    public static final long STOPPED        = 0x00000018;
    public static final long DISPOSED       = 0x00000020;
    public static final long INIT_MASK      = LOG_ENABLED | CONTEXTUALIZED | 
        PARAMETERIZED | CONFIGURED | COMPOSED | INITIALIZED | STARTED;

    /**
     * Hide the constructor
     */
    private ComponentUtil()
    {
    }

    /**
     * Create state mask from object (this can be used for more than just
     * components).
     */
    public static final long createStateMask( final Object object )
    {
        long mask = 0;

        if( object instanceof LogEnabled ||
            object instanceof Loggable )
        {
            mask |= LOG_ENABLED;
        }

        if( object instanceof Contextualizable )
        {
            mask |= CONTEXTUALIZED;
        }

        if( object instanceof Parameterizable )
        {
            mask |= PARAMETERIZED;
        }

        if( object instanceof Configurable )
        {
            mask |= CONFIGURED;
        }

        if( object instanceof Composable )
        {
            mask |= COMPOSED;
        }

        if( object instanceof Initializable )
        {
            mask |= INITIALIZED;
        }

        if( object instanceof Disposable )
        {
            mask |= DISPOSED;
        }

        if( object instanceof Startable )
        {
            mask |= STARTED | STOPPED;
        }

        if( object instanceof Suspendable )
        {
            mask |= SUSPENDED;
        }

        return mask & (~ACTIVE);
    }

    /**
     * Throw an exception if a value is already set.
     *
     * @param source  the source object to test against
     * @param value   the value to attempt to assign
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the source is already set.
     */
    public static final void setWriteOnceObject( Object source, 
                                                 final Object value, 
                                                 final String message )
    {
        if ( null == source )
        {
            source = value;
        }
        else
        {
            throw new IllegalStateException( message );
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the LOG_ENABLED state has already been set, if the component implements
     * LogEnabled or Logger, and if the state has progressed beyond the Logger
     * stage.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkLogEnabled( long state, final long mask, final String message )
    {
        if( ( (state & mask & LOG_ENABLED) > 0 ) || 
            ( (mask & LOG_ENABLED) == 0 ) || 
            ( state > LOG_ENABLED ) )
        {
            throw new IllegalStateException( message );
        }

        state |= LOG_ENABLED;
        if ( (state & INIT_MASK) == (mask & INIT_MASK) )
        {
            state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the CONTEXTUALIZED state has already been set, if the component implements
     * Contextualizable, and if the state has progressed beyond the Context stage.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkContextualized( long state, final long mask, final String message )
    {
        if ( ( (state & mask & CONTEXTUALIZED) > 0 ) || 
             ( (mask & CONTEXTUALIZED) == 0 ) || ( state > CONTEXTUALIZED ) )
        {
            throw new IllegalStateException( message );
        }

        state |= CONTEXTUALIZED;
        if ( (state & INIT_MASK) == (mask & INIT_MASK) )
        {
            state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the PARAMETERIZED state has already been set, if the component implements
     * Parameterizable, and if the state has progressed beyond the Parameters stage.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkParameterized( long state, final long mask, final String message )
    {
        if ( ( (state & mask & PARAMETERIZED) > 0 ) || ( (mask & PARAMETERIZED) == 0 ) || ( state > PARAMETERIZED ) )
        {
            throw new IllegalStateException( message );
        }

        state |= PARAMETERIZED;
        if ( (state & INIT_MASK) == (mask & INIT_MASK) )
        {
            state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the CONFIGURED state has already been set, if the component implements
     * Configurable, and if the state has progressed beyond the Configuration stage.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkConfigured( long state, final long mask, final String message )
    {
        if ( ( (state & mask & CONFIGURED) > 0 ) || ( (mask & CONFIGURED) == 0 ) || ( state > CONFIGURED ) )
        {
            throw new IllegalStateException( message );
        }

        state |= CONFIGURED;
        if ( (state & INIT_MASK) == (mask & INIT_MASK) )
        {
            state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the INITIALIZED state has already been set, if the component implements
     * Initializable, and if the state has progressed beyond the <code>initialize</code> stage.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkInitialized( long state, final long mask, final String message )
    {
        if ( ( (state & mask & INITIALIZED) > 0 ) || ( (mask & INITIALIZED) == 0 ) || ( state > INITIALIZED ) )
        {
            throw new IllegalStateException( message );
        }

        state |= INITIALIZED;
        if ( (state & INIT_MASK) == (mask & INIT_MASK) )
        {
            state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the STARTED state has already been set, if the component implements
     * Startable, and if the state has progressed beyond the <code>start</code> stage.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkStarted( long state, final long mask, final String message )
    {
        if ( ( (state & mask & STARTED) > 0 ) || ( (mask & STARTED) == 0 ) || ( state > STARTED ) )
        {
            throw new IllegalStateException( message );
        }

        state |= STARTED;
        if ( (state & INIT_MASK) == (mask & INIT_MASK) )
        {
            state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the SUSPENDED state has already been set, if the component implements
     * Suspendable, and if the Component is active.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkSuspended( long state, final long mask, final String message )
    {
        ComponentUtil.checkActive( state, mask, message );
        if ( ( (state & mask & SUSPENDED) > 0 ) || ( (mask & SUSPENDED) == 0 ) )
        {
            throw new IllegalStateException( message );
        }

        state |= SUSPENDED;
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the SUSPENDED state has not been set, if the component implements
     * Suspendable, and if the Component is active.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkResumed( long state, final long mask, final String message )
    {
        ComponentUtil.checkActive( state, mask, message );
        if ( ( (state & mask & SUSPENDED) == 0 ) || ( (mask & SUSPENDED) == 0 ) )
        {
            throw new IllegalStateException( message );
        }

        state &= ~SUSPENDED;
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the STOPPED state has not been set, if the component implements
     * Startable, and if the Component is active.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkStopped( long state, final long mask, final String message )
    {
        if ( ( (state & mask & STOPPED) > 0 ) || ( (mask & STOPPED) == 0 ) || ( (state & mask) > STOPPED ) )
        {
            throw new IllegalStateException( message );
        }

        state &= ~ACTIVE;
        state |= STOPPED;
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the DISPOSED state has not been set, if the component implements
     * Disposable.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public static final void checkDisposed( long state, final long mask, final String message )
    {
        if ( ( (state & mask & DISPOSED) > 0 ) || ( (mask & DISPOSED) == 0 ) )
        {
            throw new IllegalStateException( message );
        }

        state &= ~ACTIVE;
        state |= DISPOSED;
    }

    /**
     * Checks to see if the state is active.
     *
     * @param state   the current state of the Component
     * @param mask    the list of valid states for the component
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the component is not active
     */
    public static final void checkActive( final long state, final long mask, final String message )
    {
        if( (ACTIVE & state) > 0 ) 
        {
            return;
        }

        throw new IllegalStateException( message );
    }
}
