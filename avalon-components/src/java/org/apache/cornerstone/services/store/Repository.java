/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.store;

import org.apache.avalon.component.Component;

/**
 * Generic Repository interface
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public interface Repository
    extends Component
{
    Repository getChildRepository( String childName );
}
