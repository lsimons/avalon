/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate that an Application failed to 
 * startup or shutdown cleanly.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class ApplicationException
    extends CascadingException
{
    /**
     * Construct a new <code>ApplicationException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public ApplicationException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>ApplicationException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public ApplicationException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
