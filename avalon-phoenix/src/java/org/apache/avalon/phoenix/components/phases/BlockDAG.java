/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * This is the dependency graph for blocks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockDAG
    extends AbstractLoggable
    implements Component, Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( BlockDAG.class );

    private Container       m_container;

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_container = (Container)componentManager.lookup( Container.ROLE );
    }

    public void walkGraph( final BlockVisitor visitor, final Traversal traversal )
        throws Exception
    {
        //temporary storage to record those
        //that are already traversed
        final ArrayList completed = new ArrayList();

        final String[] entries = m_container.list();
        for( int i = 0; i < entries.length; i++ )
        {
            final String name = entries[ i ];
            final BlockEntry entry = getBlockEntry( name );
            visitBlock( name, entry, visitor, traversal, completed );
        }
    }

    private BlockEntry getBlockEntry( final String name )
        throws Exception
    {
        return (BlockEntry)m_container.getEntry( name );
    }

    /**
     * Traverse dependencies of specified entry.
     *
     * @param name name of BlockEntry
     * @param entry the BlockEntry
     */
    private void visitDependencies( final String name,
                                    final BlockEntry entry,
                                    final BlockVisitor visitor,
                                    final ArrayList completed )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message = REZ.getString( "dag.notice.traverse.name", name );
            getLogger().debug( message );
        }

        final DependencyDescriptor[] descriptors = entry.getMetaData().getBlockInfo().getDependencies();
        for( int i = 0; i < descriptors.length; i++ )
        {
            final ServiceDescriptor serviceDescriptor = descriptors[ i ].getService();
            final String role = descriptors[ i ].getRole();

            if( getLogger().isDebugEnabled() )
            {
                final String message = 
                    REZ.getString( "dag.notice.traverse.depend", name, role, serviceDescriptor.getName() );
                getLogger().debug( message );
            }

            //roleEntry should NEVER be null as it is checked when
            //entry is added to container
            final DependencyMetaData dependencyMetaData = entry.getMetaData().getDependency( role );
            final String dependencyName = dependencyMetaData.getName();
            final BlockEntry dependency = getBlockEntry( dependencyName );
            visitBlock( dependencyName, dependency, visitor, Traversal.FORWARD, completed );
        }
    }

    /**
     * Traverse all reverse dependencies of specified entry.
     * A reverse dependency are those that dependend on entry.
     *
     * @param name name of BlockEntry
     * @param entry the BlockEntry
     */
    private void visitReverseDependencies( final String name,
                                           final BlockVisitor visitor,
                                           final ArrayList completed )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message = REZ.getString( "dag.notice.reverse.name", name );
            getLogger().debug( message );
        }

        final String[] names = m_container.list();
        for( int i = 0; i < names.length; i++ )
        {
            final String blockName = names[ i ];
            final BlockEntry entry = getBlockEntry( blockName );
            final DependencyMetaData[] roles = entry.getMetaData().getDependencies();

            for( int j = 0; j < roles.length; j++ )
            {
                final String depends = roles[ j ].getName();

                if( depends.equals( name ) )
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        final String message = 
                            REZ.getString( "dag.notice.reverse.depend", 
                                           name,
                                           depends,
                                           blockName );
                        getLogger().debug( message );
                    }

                    //finally try to traverse block
                    visitBlock( blockName, entry, visitor, Traversal.REVERSE, completed );
                }
            }
        }
    }

    private void visitBlock( final String name,
                             final BlockEntry entry,
                             final BlockVisitor visitor,
                             final Traversal traversal,
                             final ArrayList completed )
        throws Exception
    {
        //If already visited this block then bug out early
        if( completed.contains( name ) ) return;
        completed.add( name );

        if( Traversal.FORWARD == traversal )
        {
            visitDependencies( name, entry, visitor, completed );
        }
        else if( Traversal.REVERSE == traversal )
        {
            visitReverseDependencies( name, visitor, completed );
        }

        visitor.visitBlock( name, entry );
    }
}
