/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.assembler;

import java.io.File;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * Assemble a <code>SarMetaData</code> object from a Configuration
 * object. The Configuration object represents the assembly descriptor
 * and is in the format specified for <code>assembly.xml</code> files.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultAssembler
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultAssembler.class );

    /**
     * Create a <code>SarMetaData</code> object based on specified 
     * name and assembly configuration. 
     *
     * @param name the name of Sar
     * @param assembly the assembly configuration object
     * @param directory the directory Sar installed to
     * @param classPath the URL array to represent Sar ClassPath
     * @return the new SarMetaData
     * @exception AssemblyException if an error occurs
     */
    public SarMetaData assembleSar( final String name,
                                    final Configuration assembly,
                                    final File directory )
        throws AssemblyException
    {
        final Configuration[] blockConfig = assembly.getChildren( "block" );
        final BlockMetaData[] blocks = buildBlocks( blockConfig );

        final Configuration[] listenerConfig = assembly.getChildren( "block-listener" );
        final BlockListenerMetaData[] listeners = buildBlockListeners( listenerConfig );

        return new SarMetaData( name, directory, blocks, listeners );
    }

    /**
     * Create an array of <code>BlockMetaData</code> objects to represent
     * the &lt;block .../&gt; sections in <code>assembly.xml</code>.
     *
     * @param blocks the list of Configuration objects for blocks
     * @return the BlockMetaData array
     * @exception Exception if an error occurs
     */
    private BlockMetaData[] buildBlocks( final Configuration[] blocks )
        throws AssemblyException
    {
        final ArrayList blockSet = new ArrayList();
        for( int i = 0; i < blocks.length; i++ )
        {
            final BlockMetaData blockMetaData = buildBlock( blocks[ i ] );
            blockSet.add( blockMetaData );
        }

        return (BlockMetaData[])blockSet.toArray( new BlockMetaData[ 0 ] );
    }

    /**
     * Create a single <code>BlockMetaData</code> object to represent
     * specified &lt;block .../&gt; section.
     *
     * @param block the Configuration object for block
     * @return the BlockMetaData object
     * @exception AssemblyException if an error occurs
     */
    private BlockMetaData buildBlock( final Configuration block )
        throws AssemblyException
    {
        try
        {
            final String name = block.getAttribute( "name" );
            final String classname = block.getAttribute( "class" );
            final Configuration[] provides = block.getChildren( "provide" );
            
            final DependencyMetaData[] roles = buildDependencyMetaDatas( provides );
            return new BlockMetaData( name, classname, roles );
        }
        catch( final ConfigurationException ce )
        {
            final String message =
                REZ.getString( "block-entry-malformed", block.getLocation(), ce.getMessage() );
            throw new AssemblyException( message );
        }
    }

    /**
     * Create an array of <code>BlockListenerMetaData</code> objects to represent
     * the &lt;block-listener .../&gt; sections in <code>assembly.xml</code>.
     *
     * @param listeners the list of Configuration objects for listeners
     * @return the BlockListenerMetaData array
     * @exception AssemblyException if an error occurs
     */
    private BlockListenerMetaData[] buildBlockListeners( final Configuration[] listeners )
        throws AssemblyException
    {
        final ArrayList listenersMetaData = new ArrayList();
        for( int i = 0; i < listeners.length; i++ )
        {
            final BlockListenerMetaData listener = buildBlockListener( listeners[ i ] );
            listenersMetaData.add( listener );
        }

        return (BlockListenerMetaData[])listenersMetaData.toArray( new BlockListenerMetaData[ 0 ] );
    }

    /**
     * Create a <code>BlockListenerMetaData</code> object to represent
     * the specified &lt;block-listener .../&gt; section.
     *
     * @param listener the Configuration object for listener
     * @return the BlockListenerMetaData object
     * @exception AssemblyException if an error occurs
     */
    private BlockListenerMetaData buildBlockListener( final Configuration listener )
        throws AssemblyException
    {
        try
        {
            final String name = listener.getAttribute( "name" );
            final String className = listener.getAttribute( "class" );
            
            return new BlockListenerMetaData( name, className );
        }
        catch( final ConfigurationException ce )
        {
            final String message =
                REZ.getString( "listener-entry-malformed", listener.getLocation(), ce.getMessage() );
            throw new AssemblyException( message );
        }
    }

    /**
     * Helper method to build an array of DependencyMetaDatas from input config data.
     *
     * @param provides the set of provides elements for block
     * @return the created DependencyMetaData array
     * @exception ConfigurationException if config data is malformed
     */
    private DependencyMetaData[] buildDependencyMetaDatas( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList dependencies = new ArrayList();
        for( int j = 0; j < provides.length; j++ )
        {
            final Configuration provide = provides[ j ];
            final String requiredName = provide.getAttribute( "name" );
            final String role = provide.getAttribute( "role" );

            dependencies.add( new DependencyMetaData( requiredName, role ) );
        }

        return (DependencyMetaData[])dependencies.toArray( new DependencyMetaData[ 0 ] );
    }
}
