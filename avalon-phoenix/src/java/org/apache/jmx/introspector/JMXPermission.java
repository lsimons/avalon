/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import java.security.BasicPermission;

/**
 * Permission used to check creation of <code>ConstructiveMBean</code> objects.
 * 
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class JMXPermission
    extends BasicPermission
{
    /**
     * Create a permission object with specified name and action.
     *
     * @param name the name of permission
     * @param action the action for permission (often name of class)
     */
    public JMXPermission( final String name, final String action )
    {
        super( name, action );
    }
}
