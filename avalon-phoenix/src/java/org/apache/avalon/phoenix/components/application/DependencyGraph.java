/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import java.util.ArrayList;
import org.apache.avalon.phoenix.containerkit.registry.ComponentProfile;
import org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData;
import org.apache.avalon.framework.info.DependencyDescriptor;

/**
 *
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
class DependencyGraph
{
    ///Private constructor to block instantiation
    private DependencyGraph()
    {
    }

    /**
     * Method to generate an ordering of nodes to traverse.
     * It is expected that the specified Blocks have passed
     * verification tests and are well formed.
     *
     * @param forward true if forward dependencys traced, false if dependencies reversed
     * @param blocks the blocks to traverse
     * @return the ordered node names
     */
    public static String[] walkGraph( final boolean forward,
                                      final ComponentProfile[] blocks )
    {
        final ArrayList result = new ArrayList();

        //temporary storage to record those
        //that are already traversed
        final ArrayList done = new ArrayList();

        for( int i = 0; i < blocks.length; i++ )
        {
            visitBlock( blocks[ i ], blocks, forward, done, result );
        }

        return (String[])result.toArray( new String[ 0 ] );
    }

    private static void visitBlock( final ComponentProfile block,
                                    final ComponentProfile[] blocks,
                                    final boolean forward,
                                    final ArrayList done,
                                    final ArrayList order )
    {
        //If already visited this block then bug out early
        final String name = block.getMetaData().getName();
        if( done.contains( name ) )
        {
            return;
        }
        done.add( name );

        if( forward )
        {
            visitDependencies( block, blocks, done, order );
        }
        else
        {
            visitReverseDependencies( block, blocks, done, order );
        }

        order.add( name );
    }

    /**
     * Traverse dependencies of specified block.
     *
     * @param block the BlockMetaData
     */
    private static void visitDependencies( final ComponentProfile block,
                                           final ComponentProfile[] blocks,
                                           final ArrayList done,
                                           final ArrayList order )
    {
        final DependencyDescriptor[] descriptors = block.getInfo().getDependencies();
        for( int i = 0; i < descriptors.length; i++ )
        {
            final String key = descriptors[ i ].getKey();
            final DependencyMetaData dependency = block.getMetaData().getDependency( key );
            final ComponentProfile other = getBlock( dependency.getProviderName(), blocks );
            visitBlock( other, blocks, true, done, order );
        }
    }

    /**
     * Traverse all reverse dependencies of specified block.
     * A reverse dependency are those that dependend on block.
     *
     * @param block the ComponentProfile
     */
    private static void visitReverseDependencies( final ComponentProfile block,
                                                  final ComponentProfile[] blocks,
                                                  final ArrayList done,
                                                  final ArrayList order )
    {
        final String name = block.getMetaData().getName();

        for( int i = 0; i < blocks.length; i++ )
        {
            final ComponentProfile other = blocks[ i ];
            final DependencyMetaData[] roles = other.getMetaData().getDependencies();

            for( int j = 0; j < roles.length; j++ )
            {
                final String depends = roles[ j ].getProviderName();
                if( depends.equals( name ) )
                {
                    visitBlock( other, blocks, false, done, order );
                }
            }
        }
    }

    /**
     * Utility method to get block with specified name from specified array.
     *
     * @param name the name of block
     * @param blocks the Block array
     * @return the Block
     */
    private static ComponentProfile getBlock( final String name,
                                              final ComponentProfile[] blocks )
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            if( blocks[ i ].getMetaData().getName().equals( name ) )
            {
                return blocks[ i ];
            }
        }

        //Should never happen if Verifier passed checks
        throw new IllegalStateException();
    }
}
