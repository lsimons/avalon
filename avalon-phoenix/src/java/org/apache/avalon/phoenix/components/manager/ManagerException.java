/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.manager;

import org.apache.avalon.framework.CascadingException;

/**
 * The ManagerException used to indicate problems with managers.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ManagerException
    extends CascadingException
{
    public ManagerException( final String message )
    {
        this( message, null );
    }

    public ManagerException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
