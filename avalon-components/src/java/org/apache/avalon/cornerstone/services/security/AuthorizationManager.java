/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.security;

import java.security.AccessControlException;
import java.security.Permission;
import java.security.Principal;
import org.apache.avalon.phoenix.Service;

/**
 * Service to manage authorization.
 * May be succeeded by JAAS in the the future.
 *
 * Warning: This is experimental and will most likely change in the future.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface AuthorizationManager
{
    //Permissions getPermissions( Principal principal );
    void checkPermission( Principal principal, Permission permission )
        throws AccessControlException;
}
