/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.assembler;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate that there was an error Assembling SarMetaData.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2002/02/26 11:13:04 $
 */
public final class AssemblyException
    extends CascadingException
{
    /**
     * Construct a new <code>AssemblyException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public AssemblyException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>AssemblyException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public AssemblyException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
