/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
 package org.apache.avalon.framework;

/**
 * This class provides basic facilities for enforcing Avalon's contracts
 * within your own code.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/11/19 17:10:55 $
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
    public static final long STARTED        = ACTIVE & 0x00000014;
    public static final long SUSPENDED      = ACTIVE & 0x01000000;
    public static final long STOPPED        = 0x00000018;
    public static final long DISPOSED       = 0x00000020;

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
    public static final long createStateMask( Object obj )
    {
        long mask = 0;

        if ( obj instanceof org.apache.avalon.framework.logger.LogEnabled ||
             obj instanceof org.apache.avalon.framework.logger.Loggable )
        {
            mask |= LOG_ENABLED;
        }

        if ( obj instanceof org.apache.avalon.framework.context.Contextualizable )
        {
            mask |= CONTEXTUALIZED;
        }

        if ( obj instanceof org.apache.avalon.framework.parameters.Parameterizable )
        {
            mask |= PARAMETERIZED;
        }

        if ( obj instanceof org.apache.avalon.framework.configuration.Configurable )
        {
            mask |= CONFIGURED;
        }

        if ( obj instanceof org.apache.avalon.framework.component.Composable )
        {
            mask |= COMPOSED;
        }

        if ( obj instanceof org.apache.avalon.framework.activity.Initializable )
        {
            mask |= INITIALIZED;
        }

        if ( obj instanceof org.apache.avalon.framework.activity.Disposable )
        {
            mask |= DISPOSED;
        }

        if ( obj instanceof org.apache.avalon.framework.activity.Startable )
        {
            mask |= STARTED | STOPPED;
        }

        if ( obj instanceof org.apache.avalon.framework.activity.Suspendable )
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
    public static final void checkObject( Object source, final Object value, final String message)
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
     * Checks to see if the state is active
     */
    public static final void checkActive( final long state, final long mask, final String message )
    {
        if ( (ACTIVE & state) > 0 ||
             ( ( mask & INITIALIZED ) > 0 &&
               ( mask & STARTED ) == 0 ) ) {
            return;
        }

        throw new IllegalStateException( message );
    }
}