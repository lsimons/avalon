/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.installer;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate error deploying.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class InstallationException
    extends CascadingException
{
    /**
     * Construct a new <code>InstallationException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public InstallationException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>InstallationException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public InstallationException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
