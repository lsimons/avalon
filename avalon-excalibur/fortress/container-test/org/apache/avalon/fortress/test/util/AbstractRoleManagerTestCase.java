/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.fortress.test.util;

import junit.framework.TestCase;

import org.apache.avalon.fortress.RoleEntry;
import org.apache.avalon.fortress.RoleManager;

/**
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/03/22 12:31:53 $
 */
public class AbstractRoleManagerTestCase extends TestCase
{
    private boolean m_informixClassExists = false;

    public AbstractRoleManagerTestCase( final String key )
    {
        super( key );

        try
        {
            Class.forName( "org.apache.avalon.excalibur.datasource.InformixDataSource" );
            m_informixClassExists = true;
        }
        catch( Exception e )
        {
            m_informixClassExists = false;
        }
    }

    protected boolean isInformixClassExists()
    {
        return m_informixClassExists;
    }

    protected void checkRole( final RoleManager roles,
                              final String shortname,
                              final String role,
                              final String className,
                              final String handlerClassname )
        throws ClassNotFoundException
    {
        final RoleEntry roleEntry = roles.getRoleForShortName( shortname );
        assertNotNull( "RoleEntry", roleEntry );

        assertEquals( "componentClass:",
                      roleEntry.getComponentClass(), Class.forName( className ) );
        assertEquals( "Role:", roleEntry.getRole(), role );
        assertEquals( "Handler:",
                      roleEntry.getHandlerClass(), Class.forName( handlerClassname ) );
    }
}
