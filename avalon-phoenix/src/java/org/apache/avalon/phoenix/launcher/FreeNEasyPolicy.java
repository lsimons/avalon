/*
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Sep 21, 2002
 * Time: 11:19:40 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.apache.avalon.phoenix.launcher;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * Default polic class to give every code base all permssions.
 * Will be replaced once the kernel loads.
 */
class FreeNEasyPolicy
    extends Policy
{
    public PermissionCollection getPermissions( final CodeSource codeSource )
    {
        final Permissions permissions = new Permissions();
        permissions.add( new java.security.AllPermission() );
        return permissions;
    }

    public void refresh()
    {
    }
}
