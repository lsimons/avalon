/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.lifecycle;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate error processing a component through its lifecycle.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public final class LifecycleException
    extends CascadingException
{
    /**
     * Construct a new <code>VerifyException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public LifecycleException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>VerifyException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public LifecycleException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
