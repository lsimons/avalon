/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.component;

import org.apache.avalon.framework.CascadingException;

/**
 * The exception thrown by ComponentManager.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 */
public class ComponentException
    extends CascadingException
{
    /**
     * Construct a new <code>ComponentException</code> instance.
     */
    public ComponentException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }

    /**
     * Construct a new <code>ComponentException</code> instance.
     */
    public ComponentException( final String message )
    {
        super( message, null );
    }
}
