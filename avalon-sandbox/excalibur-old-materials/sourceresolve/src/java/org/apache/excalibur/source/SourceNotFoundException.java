/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.source;

/**
 * This Exception should be thrown if the source could not be found.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/05/10 07:54:09 $
 */
public class SourceNotFoundException
    extends SourceException {

    /**
     * Construct a new <code>SourceNotFoundException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public SourceNotFoundException( final String message )
    {
        super( message, null );
    }

    /**
     * Construct a new <code>SourceNotFoundException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public SourceNotFoundException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
