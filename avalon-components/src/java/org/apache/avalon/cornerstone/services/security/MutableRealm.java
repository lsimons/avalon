/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.security;

import java.security.Principal;

/**
 * Extends Realm to allow addition and subtraction of Principals.
 *
 * Warning: This is experimental and will most likely change in the future.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface MutableRealm
    extends Realm
{
    String ROLE = MutableRealm.class.getName();

    void addPrincipal( Principal principal );

    void removePrincipal( Principal principal );
}
