/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import org.apache.avalon.excalibur.monitor.Monitor;
import org.apache.avalon.excalibur.monitor.Resource;

/**
 * The AbstractMonitor class is a useful base class which all Monitors
 * can extend. The particular monitoring policy is defined by the particular
 * implementation.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Id: AbstractMonitor.java,v 1.2 2002/09/07 07:26:35 donaldp Exp $
 */
public abstract class AbstractMonitor
    implements Monitor
{
    /**
     * The set of resources that the monitor is monitoring.
     */
    private Map m_resources = new HashMap();

    /**
     * Add an array of resources to monitor.
     *
     * @param resources the resources to monitor
     */
    public final void addResources( final Resource[] resources )
    {
        for( int i = 0; i < resources.length; i++ )
        {
            addResource(resources[ i ] );
        }
    }

    /**
     * Add a resource to monitor.  The resource key referenced in the other
     * interfaces is derived from the resource object.
     */
    public final void addResource( final Resource resource )
    {
        synchronized( m_resources )
        {
            final String resourceKey = resource.getResourceKey();
            if( m_resources.containsKey( resourceKey ) )
            {
                final Resource original =
                    (Resource)m_resources.get( resourceKey );
                original.addPropertyChangeListenersFrom( resource );
            }
            else
            {
                m_resources.put( resourceKey, resource );
            }
        }
    }

    /**
     * Find a monitored resource.  If no resource is available, return null
     */
    public final Resource getResource( final String key )
    {
        synchronized( m_resources )
        {
            return (Resource)m_resources.get( key );
        }
    }

    /**
     * Remove a monitored resource by key.
     */
    public final void removeResource( final String key )
    {
        synchronized( m_resources )
        {
            final Resource resource =
                (Resource)m_resources.remove( key );
            resource.removeAllPropertyChangeListeners();
        }
    }

    /**
     * Remove a monitored resource by reference.
     */
    public final void removeResource( final Resource resource )
    {
        removeResource( resource.getResourceKey() );
    }

    /**
     * Return an array containing all the resources currently monitored.
     *
     * @return an array containing all the resources currently monitored.
     */
    protected Resource[] getResources()
    {
        final Collection collection = m_resources.values();
        return (Resource[])collection.toArray( new Resource[ collection.size() ] );
    }
}
