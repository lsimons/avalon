/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.blocks;

import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.camelot.ContainerException;
import org.apache.phoenix.Block;
import org.apache.phoenix.engine.ServerApplication;
import org.apache.phoenix.metainfo.DependencyInfo;
import org.apache.phoenix.metainfo.ServiceInfo;

/**
 * This is the dependency graph for blocks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockDAG
    extends AbstractLoggable
    implements Composer
{
    protected ServerApplication       m_serverApplication;

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_serverApplication = (ServerApplication)componentManager.
            lookup( "org.apache.phoenix.engine.ServerApplication" );
    }

    public void walkGraph( final String root, final BlockVisitor visitor )
        throws Exception
    {
        visitBlock( root, getBlockEntry( root ), visitor, true );
    }

    public void reverseWalkGraph( final String root, final BlockVisitor visitor )
        throws Exception
    {
        visitBlock( root, getBlockEntry( root ), visitor, false );
    }

    protected BlockEntry getBlockEntry( final String name )
        throws Exception
    {
        return (BlockEntry)m_serverApplication.getEntry( name );
        //catch( final ContainerException ce )
    }

    /**
     * Traverse dependencies of specified entry.
     *
     * @param name name of BlockEntry
     * @param entry the BlockEntry
     */
    protected void visitDependencies( final String name, 
                                      final BlockEntry entry, 
                                      final BlockVisitor visitor )
        throws Exception
    {
        getLogger().debug( "Traversing dependencies for " + name );

        final DependencyInfo[] infos = entry.getBlockInfo().getDependencies();
        for( int i = 0; i < infos.length; i++ )
        {
            final ServiceInfo serviceInfo = infos[ i ].getService();
            final String role = infos[ i ].getRole();

            getLogger().debug( "Traversing dependency of " + name + " with role " + role + 
                               " to provide service " + serviceInfo.getName() );

            //roleEntry should NEVER be null as it is checked when 
            //entry is added to container
            final RoleEntry roleEntry = entry.getRoleEntry( role );
            final String dependencyName = roleEntry.getName();
            final BlockEntry dependency = getBlockEntry( dependencyName );
            visitBlock( dependencyName, dependency, visitor, true );
        }
    }

    /**
     * Traverse all reverse dependencies of specified entry.
     * A reverse dependency are those that dependend on entry.
     *
     * @param name name of BlockEntry
     * @param entry the BlockEntry
     */
    protected void visitReverseDependencies( final String name, final BlockVisitor visitor )
        throws Exception
    {
        getLogger().debug( "Traversing reverse dependencies for " + name );
        
        final Iterator entries = m_serverApplication.list();
        while( entries.hasNext() ) 
        {
            final String blockName = (String)entries.next();
            final BlockEntry entry = getBlockEntry( blockName );           
            final RoleEntry[] roles = entry.getRoleEntrys();

            for( int i = 0; i < roles.length; i++ )
            {
                final String depends = roles[ i ].getName();
                
                if( depends.equals( name ) )
                {
                    getLogger().debug( "Attempting to unload block " + blockName +
                                       " as it depends on " + depends );

                    //finally try to traverse block
                    visitBlock( blockName, entry, visitor, false );
                }
            }
        }
    }

    protected void visitBlock( final String name, 
                               final BlockEntry entry,
                               final BlockVisitor visitor,
                               final boolean forward )
        throws Exception
    {
        if( forward ) 
        {
            visitDependencies( name, entry, visitor );
        }
        else
        {
            visitReverseDependencies( name, visitor );
        }

        visitor.visitBlock( name, entry );
    }
}
