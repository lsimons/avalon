/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource.ids;

import org.apache.avalon.framework.CascadingException;

/**
 * Thrown when it was not possible to allocate an Id.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 03:04:27 $
 * @since 4.1
 */
public class IdException
    extends CascadingException
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Construct a new IdException instance.
     *
     * @param message The detail message for this exception.
     */
    public IdException( String message )
    {
        super( message );
    }

    /**
     * Construct a new IdException instance.
     *
     * @param message The detail message for this exception.
     * @param throwable The root cause of the exception.
     */
    public IdException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
