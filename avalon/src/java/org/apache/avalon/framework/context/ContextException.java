/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.context;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception signalling a badly formed Context.
 *
 * This can be thrown by Context object when a entry is not
 * found. It can also be thrown manually in contextualize()
 * when Component detects a malformed context value.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class ContextException
    extends CascadingException
{
    /**
     * Construct a new <code>ContextException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public ContextException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>ContextException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public ContextException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
