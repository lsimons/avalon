/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.property;

import org.apache.avalon.CascadingException;

/**
 * Thrown when a property can not be resolved properly.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PropertyException
    extends CascadingException
{
    /**
     * Construct a new <code>PropertyException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public PropertyException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>PropertyException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public PropertyException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
