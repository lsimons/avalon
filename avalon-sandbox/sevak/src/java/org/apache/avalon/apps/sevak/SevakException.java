/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.apps.sevak;

import org.apache.avalon.framework.CascadingThrowable;
/**
 *  General Exception indicating the state of the Embedded Web Server i.e. Catalina
 *
 * @author <a href="mailto:vinayc77@yahoo.com">Vinay Chandran</a>
 */
public class SevakException extends Exception implements CascadingThrowable
{
    /**
     * The Throwable that caused this exception to be thrown.
     */
    private final Throwable m_throwable;

    /**
     * Construct a new <code>SevakException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public SevakException(final String message)
    {
        this(message, null);
    }

    /**
     * Construct a new <code>SevakException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public SevakException(final String message, final Throwable throwable)
    {
        super(message);
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
