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
import org.apache.avalon.framework.component.Component;
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
 * @version CVS $Revision: 1.1 $ $Date: 2001/11/30 21:33:13 $
 */
public abstract class AbstractComponent implements Component
{
    private static final String OUT_OF_ORDER = "Initialization perfomed out of order";

    private static final long LOG_ENABLED    = 0x00000001;
    private static final long CONTEXTUALIZED = 0x00000002;
    private static final long PARAMETERIZED  = 0x00000004;
    private static final long CONFIGURED     = 0x00000008;
    private static final long COMPOSED       = 0x00000010;
    private static final long ACTIVE         = 0x10000000;
    private static final long INITIALIZED    = 0x00000012;
    private static final long STARTED        = 0x00000014;
    private static final long SUSPENDED      = 0x01000000;
    private static final long STOPPED        = 0x00000018;
    private static final long DISPOSED       = 0x00000020;
    private static final long INIT_MASK      = LOG_ENABLED | CONTEXTUALIZED |
        PARAMETERIZED | CONFIGURED | COMPOSED | INITIALIZED | STARTED;

    private final long m_mask;
    private       long m_state;

    /**
     * Create state mask from this component instance.
     */
    public AbstractComponent()
    {
        long mask = 0;

        if( this instanceof LogEnabled ||
            this instanceof Loggable )
        {
            mask |= LOG_ENABLED;
        }

        if( this instanceof Contextualizable )
        {
            mask |= CONTEXTUALIZED;
        }

        if( this instanceof Parameterizable )
        {
            mask |= PARAMETERIZED;
        }

        if( this instanceof Configurable )
        {
            mask |= CONFIGURED;
        }

        if( this instanceof Composable )
        {
            mask |= COMPOSED;
        }

        if( this instanceof Initializable )
        {
            mask |= INITIALIZED;
        }

        if( this instanceof Disposable )
        {
            mask |= DISPOSED;
        }

        if( this instanceof Startable )
        {
            mask |= STARTED | STOPPED;
        }

        if( this instanceof Suspendable )
        {
            mask |= SUSPENDED;
        }

        m_mask = mask &(~ACTIVE);
    }

    /**
     * Throw an exception if a value is already set on a write-once object.
     *
     * @param source  the source object to test against
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the source is already set.
     */
    protected final void checkAssigned( Object source, final String message )
    {
        if ( null != source )
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
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    public final void checkLogEnabled( final String message )
    {
        if( ( (m_state & m_mask & LOG_ENABLED) > 0 ) ||
            ( (m_mask & LOG_ENABLED) == 0 ) ||
            ( m_state > LOG_ENABLED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= LOG_ENABLED;
        if ( (m_state & INIT_MASK) == (m_mask & INIT_MASK) )
        {
            m_state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the CONTEXTUALIZED state has already been set, if the component implements
     * Contextualizable, and if the state has progressed beyond the Context stage.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkContextualized( final String message )
    {
        if ( ( (m_state & m_mask & CONTEXTUALIZED) > 0 ) ||
             ( (m_mask & CONTEXTUALIZED) == 0 ) || ( m_state > CONTEXTUALIZED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= CONTEXTUALIZED;
        if ( (m_state & INIT_MASK) == (m_mask & INIT_MASK) )
        {
            m_state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the PARAMETERIZED state has already been set, if the component implements
     * Parameterizable, and if the state has progressed beyond the Parameters stage.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkParameterized( final String message )
    {
        if ( ( (m_state & m_mask & PARAMETERIZED) > 0 ) ||
             ( (m_mask & PARAMETERIZED) == 0 ) || ( m_state > PARAMETERIZED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= PARAMETERIZED;
        if ( (m_state & INIT_MASK) == (m_mask & INIT_MASK) )
        {
            m_state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the CONFIGURED state has already been set, if the component implements
     * Configurable, and if the state has progressed beyond the Configuration stage.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkConfigured( final String message )
    {
        if ( ( (m_state & m_mask & CONFIGURED) > 0 ) ||
             ( (m_mask & CONFIGURED) == 0 ) || ( m_state > CONFIGURED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= CONFIGURED;
        if ( (m_state & INIT_MASK) == (m_mask & INIT_MASK) )
        {
            m_state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the COMPOSED state has already been set, if the component implements
     * Composable, and if the state has progressed beyond the Compose stage.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkComposed( final String message )
    {
        if ( ( (m_state & m_mask & COMPOSED) > 0 ) ||
             ( (m_mask & COMPOSED) == 0 ) || ( m_state > COMPOSED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= COMPOSED;
        if ( (m_state & INIT_MASK) == (m_mask & INIT_MASK) )
        {
            m_state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the INITIALIZED state has already been set, if the component implements
     * Initializable, and if the state has progressed beyond the <code>initialize</code> stage.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkInitialized( final String message )
    {
        if ( ( (m_state & m_mask & INITIALIZED) > 0 ) ||
             ( (m_mask & INITIALIZED) == 0 ) || ( m_state > INITIALIZED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= INITIALIZED;
        if ( (m_state & INIT_MASK) == (m_mask & INIT_MASK) )
        {
            m_state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the STARTED state has already been set, if the component implements
     * Startable, and if the state has progressed beyond the <code>start</code> stage.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkStarted( final String message )
    {
        if ( ( (m_state & m_mask & STARTED) > 0 ) ||
             ( (m_mask & STARTED) == 0 ) || ( m_state > STARTED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= STARTED;
        if ( (m_state & INIT_MASK) == (m_mask & INIT_MASK) )
        {
            m_state |= ACTIVE;
        }
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the SUSPENDED state has already been set, if the component implements
     * Suspendable, and if the Component is active.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkSuspended( final String message )
    {
        ComponentUtil.checkActive( m_state, m_mask, message );
        if ( ( (m_state & m_mask & SUSPENDED) > 0 ) || ( (m_mask & SUSPENDED) == 0 ) )
        {
            throw new IllegalStateException( message );
        }

        m_state |= SUSPENDED;
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the SUSPENDED state has not been set, if the component implements
     * Suspendable, and if the Component is active.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkResumed( final String message )
    {
        ComponentUtil.checkActive( m_state, m_mask, message );
        if ( ( (m_state & m_mask & SUSPENDED) == 0 ) || ( (m_mask & SUSPENDED) == 0 ) )
        {
            throw new IllegalStateException( message );
        }

        m_state &= ~SUSPENDED;
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the STOPPED state has not been set, if the component implements
     * Startable, and if the Component is active.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkStopped( final String message )
    {
        if ( ( (m_state & m_mask & STOPPED) > 0 ) ||
             ( (m_mask & STOPPED) == 0 ) || ( (m_state & m_mask) > STOPPED ) )
        {
            throw new IllegalStateException( message );
        }

        m_state &= ~ACTIVE;
        m_state |= STOPPED;
    }

    /**
     * Throw an exception if the initialization is out of order.  It tests to see
     * if the DISPOSED state has not been set, if the component implements
     * Disposable.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the state is manage out of order
     */
    protected final void checkDisposed( final String message )
    {
        if ( ( (m_state & m_mask & DISPOSED) > 0 ) || ( (m_mask & DISPOSED) == 0 ) )
        {
            throw new IllegalStateException( message );
        }

        m_state &= ~ACTIVE;
        m_state |= DISPOSED;
    }

    /**
     * Checks to see if the state is active.
     *
     * @param message the message to include in the thrown exception
     * @throws IllegalStateException if the component is not active
     */
    protected final void checkActive( final String message )
    {
        if ( (ACTIVE & m_state) > 0 ) {
            return;
        }

        throw new IllegalStateException( message );
    }
}