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
    /**
     * Construct a new <code>ComponentException</code> instance.
     *
     * @param message the exception message
     * @param throwable the throwable
     */
    public ServiceException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }

    /**
     * Construct a new <code>ComponentException</code> instance.
     *
     * @param message the exception message
     */
    public ServiceException( final String message )
    {
        super( message, null );
    }
}
