/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.camelot;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate error creating entries in factory.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class FactoryException
    extends CascadingException
{
    /**
     * Construct a new <code>FactoryException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public FactoryException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>FactoryException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public FactoryException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
