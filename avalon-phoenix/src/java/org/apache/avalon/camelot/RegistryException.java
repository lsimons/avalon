/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.camelot;

import org.apache.avalon.CascadingException;

/**
 * Exception to indicate registry error.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class RegistryException
    extends CascadingException
{
    /**
     * Construct a new <code>RegistryException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public RegistryException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>RegistryException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public RegistryException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
