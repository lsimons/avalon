/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.verifier;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate error verifying a Block or application.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.6 $ $Date: 2002/09/15 02:07:31 $
 */
public final class VerifyException
    extends CascadingException
{
    /**
     * Construct a new <code>VerifyException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public VerifyException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>VerifyException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public VerifyException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}