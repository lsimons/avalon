/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * Classloader that applies correct policy information.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PolicyClassLoader
    extends URLClassLoader
{
    protected Policy      m_policy;

    public PolicyClassLoader( final URL[] urls,
                              final ClassLoader classLoader,
                              final Policy policy )
    {
        super( urls, classLoader );
        m_policy = policy;
    }

    /**
     * Overide so we can have a per-application security policy with
     * no side-effects to other applications.
     *
     * @param codeSource the codeSource to get permissions for
     * @return the PermissionCollection
     */
    protected PermissionCollection getPermissions( final CodeSource codeSource )
    {
        if( null == m_policy )
        {
            final Permissions permissions = new Permissions();
            permissions.add( new java.security.AllPermission() );
            return permissions;
        }
        else
        {
            return m_policy.getPermissions( codeSource );
        }
    }
}
