/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import java.util.ArrayList;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;

/**
 * This is the dependency graph for blocks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockDAG
    implements Component
{
    public String[] walkGraph( final Traversal traversal, final BlockMetaData[] blocks )
    {
        final ArrayList result = new ArrayList();

        //temporary storage to record those
        //that are already traversed
        final ArrayList done = new ArrayList();

        for( int i = 0; i < blocks.length; i++ )
        {
            visitBlock( blocks[ i ], blocks, traversal, done, result );
        }

        return (String[])result.toArray( new String[ 0 ] );
    }

    private void visitBlock( final BlockMetaData block, 
                             final BlockMetaData[] blocks,
                             final Traversal traversal,
                             final ArrayList done,
                             final ArrayList order )
    {
        //If already visited this block then bug out early
        final String name = block.getName();
        if( done.contains( name ) ) return;
        done.add( name );

        if( Traversal.FORWARD == traversal )
        {
            visitDependencies( block, blocks, done, order );
        }
        else if( Traversal.REVERSE == traversal )
        {
            visitReverseDependencies( block, blocks, done, order );
        }

        order.add( name );
    }

    /**
     * Traverse dependencies of specified block.
     *
     * @param name name of BlockMetaData
     * @param block the BlockMetaData
     */
    private void visitDependencies( final BlockMetaData block, 
                                    final BlockMetaData[] blocks,
                                    final ArrayList done,
                                    final ArrayList order )
    {
        final DependencyDescriptor[] descriptors = block.getBlockInfo().getDependencies();
        for( int i = 0; i < descriptors.length; i++ )
        {
            final DependencyMetaData dependency = block.getDependency( descriptors[ i ].getRole() );
            final BlockMetaData other = getBlock( dependency.getName(), blocks );
            visitBlock( other, blocks, Traversal.FORWARD, done, order );
        }
    }

    /**
     * Traverse all reverse dependencies of specified block.
     * A reverse dependency are those that dependend on block.
     *
     * @param name name of BlockMetaData
     * @param block the BlockMetaData
     */
    private void visitReverseDependencies( final BlockMetaData block,
                                           final BlockMetaData[] blocks,
                                           final ArrayList done,
                                           final ArrayList order )
    {
        final String name = block.getName();

        for( int i = 0; i < blocks.length; i++ )
        {
            final BlockMetaData other = blocks[ i ];
            final DependencyMetaData[] roles = other.getDependencies();

            for( int j = 0; j < roles.length; j++ )
            {
                final String depends = roles[ j ].getName();
                if( depends.equals( name ) )
                {
                    visitBlock( other, blocks, Traversal.REVERSE, done, order );
                }
            }
        }
    }

    private BlockMetaData getBlock( final String name, final BlockMetaData[] blocks )
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            if( blocks[ i ].getName().equals( name ) )
            {
                return blocks[ i ];
            }
        }

        //Should never happen if Verifier passed checks
        throw new IllegalStateException();
    }
}
