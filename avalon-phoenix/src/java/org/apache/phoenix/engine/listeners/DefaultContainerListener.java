/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.listeners;

import org.apache.avalon.logger.AbstractLoggable;
import org.apache.phoenix.engine.listeners.ContainerListener;

/**
 * This interface abstracts handling of container level status.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultContainerListener 
    extends AbstractLoggable
    implements ContainerListener
{
    protected String       m_containerName;
    protected String       m_containerType;
    protected String       m_componentType;

    public DefaultContainerListener( final String containerName,
                                     final String containerType,
                                     final String componentType )
    {
        m_containerName = containerName;
        m_containerType = containerType;
        m_componentType = componentType;
    }

    public void componentAdded( final String name )
    {
        emit( name, "added to" );
    }

    public void componentLoaded( final String name )
    {
        emit( name, "loaded in" );
    }

    public void componentStarted( final String name )
    {
        emit( name, "started in" );
    }

    public void componentStopped( final String name )
    {
        emit( name, "stopped in" );
    }

    public void componentUnloaded( final String name )
    {
        emit( name, "unloaded from" );
    }

    public void componentRemoved( String name )
    {
        emit( name, "removed from" );
    }
    
    protected void emit( final String name, final String action )
    {
        getLogger().info( m_componentType + " '" + name + 
                          "' " + action + " " + m_containerType );
    }
}
