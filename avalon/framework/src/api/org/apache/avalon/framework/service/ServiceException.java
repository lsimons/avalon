/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.service;

import org.apache.avalon.framework.CascadingException;

/**
 * The exception thrown to indicate a problem with service.
 * It is usually thrown by ServiceManager or ServiceSelector.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class ServiceException
    extends CascadingException
{
    final private String m_role;

    /**
     * Construct a new <code>ServiceException</code> instance.
     *
     * @deprecated use the String,String,Throwable version instead
     * @param message the exception message
     * @param throwable the throwable
     */
    public ServiceException( final String message, final Throwable throwable )
    {
        this( null, message, throwable );
    }

    /**
     * Construct a new <code>ServiceException</code> instance.
     *
     * @param message the exception message
     * @param throwable the throwable
     */
    public ServiceException( final String role, final String message, final Throwable throwable )
    {
        super( message, throwable );
        m_role = role;
    }

    /**
     * Construct a new <code>ServiceException</code> instance.
     *
     * @deprecated use the String,String version instead
     * @param message the exception message
     */
    public ServiceException( final String message )
    {
        this( null, message, null );
    }

    /**
     * Construct a new <code>ComponentException</code> instance.
     *
     * @param message the exception message
     */
    public ServiceException( final String role, final String message )
    {
        this( role, message, null );
    }

    /**
     * Return the role that caused the exception
     */
    public String getRole()
    {
        return m_role;
    }
}
