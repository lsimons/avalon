/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework;

/**
 * Class from which all exceptions should inherit.
 * Allows recording of nested exceptions.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class CascadingException
    extends Exception
    implements CascadingThrowable
{
    private final Throwable         m_throwable;

    /**
     * Construct a new <code>CascadingException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public CascadingException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>CascadingException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public CascadingException( final String message, final Throwable throwable )
    {
        super( message );
        m_throwable = throwable;
    }

    /**
     * Retrieve root cause of the exception.
     *
     * @return the root cause
     */
    public final Throwable getCause()
    {
        return m_throwable;
    }
}
