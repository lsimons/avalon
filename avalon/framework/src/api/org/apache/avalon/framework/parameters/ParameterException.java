/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.parameters;

import org.apache.avalon.framework.CascadingException;

/**
 * Thrown when a <code>Configurable</code> component cannot be configured
 * properly, or if a value cannot be retrieved properly.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class ParameterException
    extends CascadingException
{
    /**
     * Construct a new <code>ParameterException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public ParameterException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>ParameterException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public ParameterException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
