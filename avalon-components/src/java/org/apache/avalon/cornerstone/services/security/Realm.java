/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.security;

import java.security.Principal;
import java.util.Iterator;
import org.apache.avalon.phoenix.Service;

/**
 * Interface for a Realm.
 * A Realm is a grouping of principals. The names of principals are guarenteed
 * to be unique within a realm. Sample realms may be
 * <ul>
 *   <li>unix domain</li>
 *   <li>NT domain</li>
 *   <li>set of users who have mail forwarding accounts</li>
 *   <li>set of users who have access to HTTP Realm "Foo"</li>
 * </ul>
 *
 * Warning: This is experimental and will most likely change in the future.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Realm
    extends Service
{
    Principal getPrincipal( String name );
    Iterator getPrincipalNames();
}
