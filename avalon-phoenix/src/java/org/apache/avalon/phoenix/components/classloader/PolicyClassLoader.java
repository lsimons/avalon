/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * Classloader that uses a specified <code>Policy</code> object
 * rather than system <code>Policy</code> object.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class PolicyClassLoader
    extends URLClassLoader
{
    ///Policy to use to define permissions for classes loaded in classloader
    private final Policy  m_policy;

    /**
     * Construct a ClassLoader using specified URLs, parent
     * ClassLoader and Policy object.
     *
     * @param urls the URLs to load resources from
     * @param classLoader the parent ClassLoader
     * @param policy the Policy object
     */
    PolicyClassLoader( final URL[] urls,
                       final ClassLoader classLoader,
                       final Policy policy )
    {
        super( urls, classLoader );

        if( null == policy )
        {
            throw new NullPointerException( "policy" );
        }

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
        return m_policy.getPermissions( codeSource );
    }
}
