/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.assembler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.InterceptorInfo;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;
import org.apache.avalon.phoenix.tools.infobuilder.BlockInfoBuilder;
import org.apache.avalon.phoenix.tools.infobuilder.InterceptorInfoBuilder;

/**
 * Assemble a {@link SarMetaData} object from a Configuration
 * object. The Configuration object represents the assembly descriptor
 * and is in the format specified for <tt>assembly.xml</tt> files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.22.4.1 $ $Date: 2002/10/15 22:14:07 $
 */
public class Assembler
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( Assembler.class );

    private final BlockInfoBuilder m_builder = new BlockInfoBuilder();
    private final InterceptorInfoBuilder m_interceptorBuilder = new InterceptorInfoBuilder();

    /**
     * Overidden setLogger() method to setup BlockInfoBuilder
     * logger simultaneously.
     *
     * @param logger the logger to use
     */
    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_builder );
    }

    /**
     * Create a {@link SarMetaData} object based on specified
     * name and assembly configuration.
     *
     * @param name the name of Sar
     * @param assembly the assembly configuration object
     * @param directory the directory Sar installed to
     * @param classLoader the ClassLoader from which resources
     *        are loaded (such as meta-data).
     * @return the new SarMetaData
     * @throws AssemblyException if an error occurs
     */
    public SarMetaData assembleSar( final String name,
                                    final Configuration assembly,
                                    final File directory,
                                    final ClassLoader classLoader )
        throws AssemblyException
    {
        final Configuration[] blockConfig = assembly.getChildren( "block" );
        final BlockMetaData[] blocks = buildBlocks( blockConfig, classLoader );

        final Configuration[] listenerConfig = assembly.getChildren( "listener" );
        final BlockListenerMetaData[] listeners = buildBlockListeners( listenerConfig );

        return new SarMetaData( name, directory, blocks, listeners );
    }

    /**
     * Create an array of {@link BlockMetaData} objects to represent
     * the &lt;block .../&gt; sections in <tt>assembly.xml</tt>.
     *
     * @param blocks the list of Configuration objects for blocks
     * @return the BlockMetaData array
     * @throws AssemblyException if an error occurs
     */
    private BlockMetaData[] buildBlocks( final Configuration[] blocks,
                                         final ClassLoader classLoader )
        throws AssemblyException
    {
        final ArrayList blockSet = new ArrayList();
        for( int i = 0; i < blocks.length; i++ )
        {
            final BlockMetaData blockMetaData =
                buildBlock( blocks[ i ], classLoader );
            blockSet.add( blockMetaData );
        }

        return (BlockMetaData[])blockSet.toArray( new BlockMetaData[ 0 ] );
    }

    /**
     * Create a single {@link BlockMetaData} object to represent
     * specified &lt;block .../&gt; section.
     *
     * @param block the Configuration object for block
     * @return the BlockMetaData object
     * @throws AssemblyException if an error occurs
     */
    private BlockMetaData buildBlock( final Configuration block,
                                      final ClassLoader classLoader )
        throws AssemblyException
    {
        try
        {
            final String name = block.getAttribute( "name" );
            final String classname = block.getAttribute( "class" );
            final Configuration[] provides = block.getChildren( "provide" );
            final Configuration proxy = block.getChild( "proxy" );
            final boolean disableProxy =
                proxy.getAttributeAsBoolean( "disable", false );
            final InterceptorInfo[] interceptors = m_interceptorBuilder.build( proxy ); 

            final DependencyMetaData[] roles = buildDependencyMetaDatas( provides );
            final BlockInfo info = getBlockInfo( name, classname, classLoader );


            return new BlockMetaData( name, roles, disableProxy, info, interceptors );
        }
        catch( final ConfigurationException ce )
        {
            final String message =
                REZ.getString( "block-entry-malformed", block.getLocation(), ce.getMessage() );
            throw new AssemblyException( message );
        }
    }

    /**
     * Get a BlockInfo for Block with specified name and classname.
     * The BlockInfo may be loaded from the specified cache otherwise it must be
     * loaded from specified ClassLoader.
     *
     * @param name the name of Block
     * @param classname the name of Blocks class
     * @return the BlockInfo for specified block
     * @throws AssemblyException if an error occurs
     */
    private BlockInfo getBlockInfo( final String name,
                                    final String classname,
                                    final ClassLoader classLoader )
        throws AssemblyException
    {
        final String resourceName = classname.replace( '.', '/' ) + ".xinfo";

        final String notice = REZ.getString( "loading-blockinfo", resourceName );
        getLogger().debug( notice );

        final URL resource = classLoader.getResource( resourceName );
        if( null == resource )
        {
            final String message = REZ.getString( "blockinfo-missing", name, resourceName );
            throw new AssemblyException( message );
        }

        try
        {
            final Configuration info = ConfigurationBuilder.build( resource.toExternalForm() );

            return m_builder.build( classname, info );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "blockinfo-nocreate", name, resourceName, e.getMessage() );
            throw new AssemblyException( message, e );
        }
    }

    /**
     * Create an array of {@link BlockListenerMetaData} objects to represent
     * the &lt;listener .../&gt; sections in <tt>assembly.xml</tt>.
     *
     * @param listeners the list of Configuration objects for listeners
     * @return the BlockListenerMetaData array
     * @throws AssemblyException if an error occurs
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
     * Create a {@link BlockListenerMetaData} object to represent
     * the specified &lt;listener .../&gt; section.
     *
     * @param listener the Configuration object for listener
     * @return the BlockListenerMetaData object
     * @throws AssemblyException if an error occurs
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
                REZ.getString( "listener-entry-malformed",
                               listener.getLocation(),
                               ce.getMessage() );
            throw new AssemblyException( message );
        }
    }

    /**
     * Helper method to build an array of DependencyMetaDatas from input config data.
     *
     * @param provides the set of provides elements for block
     * @return the created DependencyMetaData array
     * @throws ConfigurationException if config data is malformed
     */
    private DependencyMetaData[] buildDependencyMetaDatas( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList dependencies = new ArrayList();
        for( int j = 0; j < provides.length; j++ )
        {
            final Configuration provide = provides[ j ];
            final String requiredName = provide.getAttribute( "name" );
            final String alias = provide.getAttribute( "alias", requiredName );
            final String role = provide.getAttribute( "role" );

            dependencies.add( new DependencyMetaData( requiredName, role, alias ) );
        }

        return (DependencyMetaData[])dependencies.toArray( new DependencyMetaData[ 0 ] );
    }
}
