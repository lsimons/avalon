/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import org.apache.avalon.framework.component.Component;

/**
 * The Monitor is used to actively check a set of resources to see if they have
 * changed.  It will be implemented as a Component, that can be retrieved from
 * the ComponentLocator.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: Monitor.java,v 1.8 2002/06/13 17:24:52 bloritsch Exp $
 */
public interface Monitor
    extends Component
{
    String ROLE = Monitor.class.getName();

    /**
     * Add a resource to monitor.  The resource key referenced in the other
     * interfaces is derived from the resource object.
     */
    void addResource( Resource resource );

    /**
     * Find a monitored resource.  If no resource is available, return null
     */
    Resource getResource( String key );

    /**
     * Remove a monitored resource by key.
     */
    void removeResource( String key );

    /**
     * Remove a monitored resource by reference.
     */
    void removeResource( Resource resource );
}
