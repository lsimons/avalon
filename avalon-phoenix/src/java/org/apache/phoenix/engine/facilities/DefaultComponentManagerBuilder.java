/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities;

import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.DefaultComponentManager;
import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.pipeline.ComponentManagerBuilder;
import org.apache.phoenix.engine.ServerApplication;
import org.apache.phoenix.engine.blocks.BlockEntry;
import org.apache.phoenix.engine.blocks.RoleEntry;

/**
 * Component responsible for building componentManager information for entry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultComponentManagerBuilder
    implements Facility, ComponentManagerBuilder, Composer
{
    //container to get dependencies from
    protected ServerApplication        m_serverApplication;

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_serverApplication = (ServerApplication)componentManager.
            lookup( "org.apache.phoenix.engine.ServerApplication" );
    }

    /**
     * Build a ComponentManager for a specific Block.
     *
     * @param name the name of the block
     * @param entry the BlockEntry
     * @return the created ComponentManager
     */
    public ComponentManager createComponentManager( String name, Entry entry )
        throws ComponentManagerException
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();       
        final RoleEntry[] roleEntrys = ((BlockEntry)entry).getRoleEntrys();
        
        for( int i = 0; i < roleEntrys.length; i++ )
        {
            final String dependencyName = roleEntrys[ i ].getName();

            try
            {
                //dependency should NEVER be null here as it 
                //is validated at entry time
                final BlockEntry dependency = 
                    (BlockEntry)m_serverApplication.getEntry( dependencyName );
                componentManager.put( roleEntrys[ i ].getRole(), dependency.getBlock() );
            }
            catch( final ContainerException ce ) {}
        }
        
        return componentManager;
    }
}
