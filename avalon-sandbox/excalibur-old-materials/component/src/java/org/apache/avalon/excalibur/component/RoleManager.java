/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

/**
 * RoleManager Interface, use this to specify the Roles and how they
 * correspond easy shorthand names.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ricardo@apache.org">Ricardo Rocha</a>
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:02 $
 * @since 4.0
 */
public interface RoleManager
{
    /**
     * Find Role name based on shorthand name.  Please note that if
     * this returns <code>null</code> or an empty string, then the
     * shorthand name is assumed to be a "reserved word".  In other
     * words, you should not try to instantiate a class from an empty
     * role.
     */
    String getRoleForName( String shorthandName );

    /**
     * Get the default classname for a given role.
     */
    String getDefaultClassNameForRole( String role );

    /**
     * Get the default classname for a given hint type.  This is only
     * used by ComponentSelectors.
     */
    String getDefaultClassNameForHint( String hint, String shorthand );
}
