/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.testcase;

import junit.framework.AssertionFailedError;

import org.apache.avalon.framework.CascadingThrowable;

/**
 * This is an extention to the testing framework so that we can get detailed
 * messages from JUnit (The AssertionFailedError hides the underlying cause)
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version $Id: CascadingAssertionFailedError.java,v 1.3 2003/03/22 12:31:45 leosimons Exp $
 */
public class CascadingAssertionFailedError
    extends AssertionFailedError
    implements CascadingThrowable
{
    private final Throwable m_throwable;

    /**
     * Constructor with no message
     */
    public CascadingAssertionFailedError()
    {
        this( null, null );
    }

    /**
     * Constructor with a message
     */
    public CascadingAssertionFailedError( String message )
    {
        this( message, null );
    }

    /**
     * Constructor with a message and a parent exception
     */
    public CascadingAssertionFailedError( String message,
                                          Throwable parentThrowable )
    {
        super( message );
        m_throwable = parentThrowable;
    }

    /**
     * Return the parent exception
     */
    public final Throwable getCause()
    {
        return m_throwable;
    }
}
