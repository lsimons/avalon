/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.applications;

import org.apache.framework.CascadingException;

/**
 * The ApplicationException is used to indicate problems with applications.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ApplicationException
    extends CascadingException
{
    public ApplicationException( final String message )
    {
        this( message, null );
    }

    public ApplicationException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
