/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.security.CodeSource;
import java.security.Permissions;

/**
 * Internal Policy Entry holder class.
 */
final class PolicyEntry
{
    private final CodeSource m_codeSource;

    private final Permissions m_permissions;

    public PolicyEntry( final CodeSource codeSource,
                        final Permissions permissions )
    {
        m_codeSource = codeSource;
        m_permissions = permissions;
    }

    public CodeSource getCodeSource()
    {
        return m_codeSource;
    }

    public Permissions getPermissions()
    {
        return m_permissions;
    }
}
