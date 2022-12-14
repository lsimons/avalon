/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.phoenix.components.application;

import java.util.ArrayList;
import org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.containerkit.profile.ComponentProfile;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;

/**
 *
 *
 * @author Peter Donald
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
            final DependencyMetaData[] dependencySet = block.getMetaData().getDependencies( key );
            for( int j = 0; j < dependencySet.length; j++ )
            {
                final DependencyMetaData dependency = dependencySet[ j ];
                final ComponentProfile other = getBlock( dependency.getProviderName(), blocks );
                visitBlock( other, blocks, true, done, order );
            }
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
