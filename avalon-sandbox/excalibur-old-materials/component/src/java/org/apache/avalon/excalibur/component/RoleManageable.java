/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

/**
 * RoleManageable Interface, use this to set the RoleManagers for child
 * Components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:02 $
 * @since 4.0
 */
public interface RoleManageable
{
    /**
     * Sets the RoleManager for child components.  Can be for special
     * purpose components, however it is used mostly internally.
     */
    void setRoleManager( RoleManager roles );
}
