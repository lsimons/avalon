/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.security;

import java.security.Principal;
import java.util.Hashtable;
import org.apache.cornerstone.services.security.RoleManager;
import org.apache.phoenix.Block;

/**
 * Service to manager the role mappings for principals.
 *
 * This currently assumes principal is the prime dimension
 * which could be wrong. It could be that roles are a primary
 * aspect and
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultRoleManager
    implements Block, RoleManager
{
    protected Hashtable          m_principals   = new Hashtable();

    public void removeRoleMapping( final Principal principal )
    {
        checkPrincipal();
        final String name = principal.getName();
        m_principals.remove( name );
    }

    public void addRoleMapping( final Principal principal, final String[] roles )
    {
        checkPrincipal();
        final String name = principal.getName();
        m_principals.put( name, roles );
    }

    public boolean isPrincipalInRole( final Principal principal, final String role )
    {
        checkPrincipal();
        final String name = principal.getName();
        final String[] roles = (String[])m_principals.get( name );

        if( null != roles )
        {
            for( int i = 0; i < roles.length; i++ )
            {
                if( roles[ i ].equals( role ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected void checkPrincipal()
    {
        //TOTO: verify that Principal is valid ....
    }
}
