/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import java.util.ArrayList;
import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * This is the dependency graph for blocks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockDAG
    implements Component, Composable
{
    private Container       m_container;

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_container = (Container)componentManager.lookup( Container.ROLE );
    }

    public String[] walkGraph( final Traversal traversal )
        throws Exception
    {
        final ArrayList result = new ArrayList();

        //temporary storage to record those
        //that are already traversed
        final ArrayList done = new ArrayList();

        final String[] entries = m_container.list();
        for( int i = 0; i < entries.length; i++ )
        {
            final String name = entries[ i ];
            final BlockMetaData block = getBlock( name );
            visitBlock( name, block, traversal, done, result );
        }

        return (String[])result.toArray( new String[ 0 ] );
    }

    private BlockMetaData getBlock( final String name )
        throws Exception
    {
        return ((BlockEntry)m_container.getEntry( name )).getMetaData();
    }

    /**
     * Traverse dependencies of specified block.
     *
     * @param name name of BlockMetaData
     * @param block the BlockMetaData
     */
    private void visitDependencies( final String name,
                                    final BlockMetaData block,
                                    final ArrayList done,
                                    final ArrayList order )
        throws Exception
    {
        final DependencyDescriptor[] descriptors = block.getBlockInfo().getDependencies();
        for( int i = 0; i < descriptors.length; i++ )
        {
            final ServiceDescriptor serviceDescriptor = descriptors[ i ].getService();
            final String role = descriptors[ i ].getRole();
            final DependencyMetaData dependencyMetaData = block.getDependency( role );
            final String dependencyName = dependencyMetaData.getName();
            final BlockMetaData dependency = getBlock( dependencyName );
            visitBlock( dependencyName, dependency, Traversal.FORWARD, done, order );
        }
    }

    /**
     * Traverse all reverse dependencies of specified block.
     * A reverse dependency are those that dependend on block.
     *
     * @param name name of BlockMetaData
     * @param block the BlockMetaData
     */
    private void visitReverseDependencies( final String name,
                                           final ArrayList done,
                                           final ArrayList order )
        throws Exception
    {
        final String[] names = m_container.list();
        for( int i = 0; i < names.length; i++ )
        {
            final String blockName = names[ i ];
            final BlockMetaData block = getBlock( blockName );
            final DependencyMetaData[] roles = block.getDependencies();

            for( int j = 0; j < roles.length; j++ )
            {
                final String depends = roles[ j ].getName();
                if( depends.equals( name ) )
                {
                    visitBlock( blockName, block, Traversal.REVERSE, done, order );
                }
            }
        }
    }

    private void visitBlock( final String name,
                             final BlockMetaData block,
                             final Traversal traversal,
                             final ArrayList done,
                             final ArrayList order )
        throws Exception
    {
        //If already visited this block then bug out early
        if( done.contains( name ) ) return;
        done.add( name );

        if( Traversal.FORWARD == traversal )
        {
            visitDependencies( name, block, done, order );
        }
        else if( Traversal.REVERSE == traversal )
        {
            visitReverseDependencies( name, done, order );
        }

        order.add( name );
    }
}
