/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.verifier;

import java.net.URL;
import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.RoleMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.BlockInfoBuilder;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultVerifier
    extends AbstractLoggable
    implements Verifier
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultVerifier.class );

    private final DefaultConfigurationBuilder  m_builder  = new DefaultConfigurationBuilder();

    public void verifySar( final SarMetaData sar, final ClassLoader classLoader )
        throws VerifyException
    {
        final BlockMetaData[] blocks = sar.getBlocks();
        final BlockListenerMetaData[] listeners = sar.getListeners();

        hackVerifySar( blocks, listeners, classLoader );
    }

    public void hackVerifySar( final BlockMetaData[] blocks, 
                               final BlockListenerMetaData[] listeners, 
                               final ClassLoader classLoader )
        throws VerifyException
    {
        String message = null;

        message = REZ.getString( "verify-unique-names" );
        getLogger().info( message );
        checkNamesUnique( blocks, listeners );

        message = REZ.getString( "loading-blockinfos" );
        getLogger().info( message );
        loadBlockInfos( blocks, classLoader );

        message = REZ.getString( "verify-dependencies-mapping" );
        getLogger().info( message );
        verifyValidDependencies( blocks );

        message = REZ.getString( "verify-dependency-references" );
        getLogger().info( message );
        verifyDependencyReferences( blocks );

        //TODO: Verify that there are no circular dependencies!

        message = REZ.getString( "verify-block-type" );
        getLogger().info( message );
        verifyBlocksType( blocks, classLoader );

        message = REZ.getString( "verify-listener-type" );
        getLogger().info( message );
        verifyListenersType( listeners, classLoader );
    }

    /**
     * Loade the BlockInfo objects from specified ClassLoader for specified blocks.
     *
     * @param blocks the blocks
     * @param classLoader the ClassLoader
     * @exception VerifyException if an error occurs
     */
    private void loadBlockInfos( final BlockMetaData[] blocks, final ClassLoader classLoader )
        throws VerifyException
    {
        final HashMap infoCache = new HashMap();

        for( int i = 0; i < blocks.length; i++ )
        {
            final String name = blocks[ i ].getName();
            final String classname = blocks[ i ].getClassname();
            final BlockInfo blockInfo = 
                getBlockInfo( name, classname, infoCache, classLoader );

            blocks[ i ].setBlockInfo( blockInfo );
        }
    }

    /**
     * Verfiy that all Blocks have the needed dependencies specified correctly.
     *
     * @param blocks the BlockMetaData objects for the blocks
     * @exception VerifyException if an error occurs
     */
    private void verifyValidDependencies( final BlockMetaData[] blocks )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            verifyDependenciesMap( blocks[ i ] );
        }
    }

    /**
     * Verfiy that the inter-Block dependencies are valid.
     *
     * @param blocks the BlockMetaData objects for the blocks
     * @exception VerifyException if an error occurs
     */
    private void verifyDependencyReferences( final BlockMetaData[] blocks )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            verifyDependencyReferences( blocks[ i ], blocks );
        }
    }

    /**
     * Verfiy that the inter-Block dependencies are valid for specified Block.
     *
     * @param block the BlockMetaData object for the block
     * @param others the BlockMetaData objects for the other blocks
     * @exception VerifyException if an error occurs
     */
    private void verifyDependencyReferences( final BlockMetaData block, 
                                             final BlockMetaData[] others )
        throws VerifyException
    {
        final BlockInfo info = block.getBlockInfo();
        final RoleMetaData[] roles = block.getRoles();

        for( int i = 0; i < roles.length; i++ )
        {
            final String name = roles[ i ].getName();
            final String interfaceName = roles[ i ].getInterface();
            final ServiceDescriptor service =
                info.getDependency( interfaceName ).getService();

            //Make sure block does not depend on itself
            if( name.equals( block.getName() ) )
            {
                final String message = 
                    REZ.getString( "dependency-circular", name, service );
                throw new VerifyException( message );
            }

            //Get the other block that is providing service
            final BlockMetaData other = getBlock( name, others );
            if( null == other )
            {
                final String message = 
                    REZ.getString( "dependency-noblock", name, block.getName() );
                throw new VerifyException( message );
            }

            //make sure that the block offers service 
            //that user expects it to be providing
            final ServiceDescriptor[] services = other.getBlockInfo().getServices();
            if( !hasMatchingService( service, services ) )
            {
                final String message = 
                    REZ.getString( "dependency-noservice", name, service, block.getName() );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Get Block with specified name from specified Block array.
     *
     * @param name the name of block to get
     * @param blocks the array of Blocks to search
     * @return the Block if found, else null
     */
    private BlockMetaData getBlock( final String name, final BlockMetaData[] blocks )
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            if( blocks[ i ].getName().equals( name ) )
            {
                return blocks[ i ];
            }
        }

        return null;
    }

    /**
     * Verfiy that all Blocks specify classes that implement the
     * advertised interfaces.
     *
     * @param blocks the BlockMetaData objects for the blocks
     * @exception VerifyException if an error occurs
     */
    private void verifyBlocksType( final BlockMetaData[] blocks, final ClassLoader classLoader )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            verifyBlockType( blocks[ i ], classLoader );
        }
    }

    /**
     * Verfiy that specified Block designate classes that implement the
     * advertised interfaces.
     *
     * @param block the BlockMetaData object for the blocks
     * @exception VerifyException if an error occurs
     */
    private void verifyBlockType( final BlockMetaData block, final ClassLoader classLoader )
        throws VerifyException
    {
        Class clazz = null;
        try
        {
            clazz = classLoader.loadClass( block.getClassname() );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "bad-block-class",
                                                  block.getName(),
                                                  block.getClassname(),
                                                  e.getMessage() );
            throw new VerifyException( message );
        }

        final Class[] interfaces =
            getServiceClasses( block.getName(),
                               block.getBlockInfo().getServices(),
                               classLoader );

        for( int i = 0; i < interfaces.length; i++ )
        {
            if( !interfaces[ i ].isAssignableFrom( clazz ) )
            {
                final String message = REZ.getString( "block-noimpl-service",
                                                      block.getName(),
                                                      block.getClassname(),
                                                      interfaces[ i ].getName() );
                throw new VerifyException( message );
            }
        }

    }

    /**
     * Verfiy that all listeners implement BlockListener.
     *
     * @param listeners the BlockListenerMetaData objects for the listeners
     * @exception VerifyException if an error occurs
     */
    private void verifyListenersType( final BlockListenerMetaData[] listeners,
                                      final ClassLoader classLoader )
        throws VerifyException
    {
        for( int i = 0; i < listeners.length; i++ )
        {
            verifyListenerType( listeners[ i ], classLoader );
        }
    }

    /**
     * Verfiy that specified Listener class implements the BlockListener interface.
     *
     * @param listener the BlockListenerMetaData object for the listener
     * @exception VerifyException if an error occurs
     */
    private void verifyListenerType( final BlockListenerMetaData listener,
                                     final ClassLoader classLoader )
        throws VerifyException
    {
        Class clazz = null;
        try
        {
            clazz = classLoader.loadClass( listener.getClassname() );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "bad-listener-class",
                                                  listener.getName(),
                                                  listener.getClassname(),
                                                  e.getMessage() );
            throw new VerifyException( message );
        }

        if( !BlockListener.class.isAssignableFrom( clazz ) )
        {
            final String message = REZ.getString( "listener-noimpl-listener",
                                                  listener.getName(),
                                                  listener.getClassname() );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the names of the specified blocks and listeners are unique.
     * It is not valid for the same name to be used in multiple Blocks and or
     * BlockListeners.
     *
     * @param blocks the Blocks
     * @param listeners the listeners
     * @exception VerifyException if an error occurs
     */
    private void checkNamesUnique( final BlockMetaData[] blocks,
                                   final BlockListenerMetaData[] listeners )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            final String name = blocks[ i ].getName();
            checkNameUnique( name, blocks, listeners, i, -1 );
        }

        for( int i = 0; i < listeners.length; i++ )
        {
            final String name = listeners[ i ].getName();
            checkNameUnique( name, blocks, listeners, -1, i );
        }
    }

    /**
     * Verify that the specified name is unique among specified blocks
     * and listeners except for those indexes specified.
     *
     * @param name the name to check for
     * @param blocks the Blocks
     * @param listeners the listeners
     * @param blockIndex the index of block that is allowed to
     *                   match in name (or -1 if name designates a listener)
     * @param listenerIndex the index of listener that is allowed to
     *                      match in name (or -1 if name designates a block)
     * @exception VerifyException if an error occurs
     */
    private void checkNameUnique( final String name,
                                  final BlockMetaData[] blocks,
                                  final BlockListenerMetaData[] listeners,
                                  final int blockIndex,
                                  final int listenerIndex )
        throws VerifyException
    {
        //Verify no blocks have the same name
        for( int i = 0; i < blocks.length; i++ )
        {
            final String other = blocks[ i ].getName();
            if( blockIndex != i && name.equals( other ) )
            {
                final String message = REZ.getString( "duplicate-name", name );
                throw new VerifyException( message );
            }
        }

        //Verify no listeners have the same name
        for( int i = 0; i < listeners.length; i++ )
        {
            final String other = listeners[ i ].getName();
            if( listenerIndex != i && name.equals( other ) )
            {
                final String message = REZ.getString( "duplicate-name", name );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Get a BlockInfo for Block with specified name and classname.
     * The BlockInfo may be loaded from the specified cache otherwise it must be
     * loaded from specified ClassLoader.
     *
     * @param name the name of Block
     * @param classname the name of Blocks class
     * @param cache the place to cache BlockInfo objects
     * @return the BlockInfo for specified block
     * @exception VerifyException if an error occurs
     */
    private BlockInfo getBlockInfo( final String name,
                                    final String classname,
                                    final HashMap cache,
                                    final ClassLoader classLoader )
        throws VerifyException
    {
        final BlockInfo cachedInfo = (BlockInfo)cache.get( classname );
        if( null != cachedInfo )
        {
            return cachedInfo;
        }

        final String resourceName = classname.replace( '.', '/' ) + ".xinfo";

        final String notice = REZ.getString( "loading-blockinfo", resourceName );
        getLogger().debug( notice );

        final URL resource = classLoader.getResource( resourceName );
        if( null == resource )
        {
            final String message = REZ.getString( "blockinfo-missing", name, resourceName );
            throw new VerifyException( message );
        }

        try
        {
            final Configuration info = m_builder.build( resource.toString() );
            final BlockInfo blockInfo = BlockInfoBuilder.build( classname, info );
            cache.put( classname, blockInfo );
            return blockInfo;
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "blockinfo-nocreate", name, resourceName, e.getMessage() );
            throw new VerifyException( message, e );
        }
    }

    /**
     * Retrieve a list of RoleMetaData objects for BlockMetaData
     * and verify that there is a 1 to 1 map with dependencies specified
     * in BlockInfo.
     *
     * @param block the BlockMetaData describing the block
     * @exception VerifyException if an error occurs
     */
    private void verifyDependenciesMap( final BlockMetaData block )
        throws VerifyException
    {
        //Make sure all role entries specified in config file are valid
        final RoleMetaData[] roles = block.getRoles();
        for( int i = 0; i < roles.length; i++ )
        {
            final String interfaceName = roles[ i ].getInterface();
            final DependencyDescriptor descriptor = block.getBlockInfo().getDependency( interfaceName );

            //If there is no dependency descriptor in BlockInfo then
            //user has specified an uneeded dependency.
            if( null == descriptor )
            {
                final String message = REZ.getString( "unknown-dependency",
                                                      roles[ i ].getName(),
                                                      interfaceName,
                                                      block.getName() );
                throw new VerifyException( message );
            }
        }

        //Make sure all dependencies in BlockInfo file are satisfied
        final DependencyDescriptor[] dependencies = block.getBlockInfo().getDependencies();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final RoleMetaData role = block.getRole( dependencies[ i ].getRole() );

            //If there is no Role then the user has failed
            //to specify a needed dependency.
            if( null == role )
            {
                final String message = REZ.getString( "unspecified-dependency",
                                                      dependencies[ i ].getRole(),
                                                      block.getName() );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Retrieve an array of Classes for all the services (+ the Block interface)
     * that a Block offers. This method also makes sure all services offered are
     * interfaces.
     *
     * @param name the name of block
     * @param services the services the Block offers
     * @param classLoader the classLoader
     * @return an array of Classes for all the services
     * @exception VerifyException if an error occurs
     */
    private Class[] getServiceClasses( final String name,
                                       final ServiceDescriptor[] services,
                                       final ClassLoader classLoader )
        throws VerifyException
    {
        final Class[] classes = new Class[ services.length + 1 ];

        for( int i = 0; i < services.length; i++ )
        {
            final String classname = services[ i ].getName();
            try
            {
                classes[ i ] = classLoader.loadClass( classname );
            }
            catch( final Throwable t )
            {
                final String message =
                    REZ.getString( "bad-service-class", name, classname, t.getMessage() );
                throw new VerifyException( message, t );
            }

            if( !classes[ i ].isInterface() )
            {
                final String message =
                    REZ.getString( "service-not-interface", name, classname );
                throw new VerifyException( message );
            }
            
            //TODO: Verify that Service extends Service interface????
        }

        classes[ services.length ] = Block.class;
        return classes;
    }

    /**
     * Return true if specified service matches any of the
     * candidate services.
     *
     * @param candidates an array of candidate services
     * @param service the service
     * @return true if candidate services contains a service that matches
     *         specified service, false otherwise
     */
    private boolean hasMatchingService( final ServiceDescriptor service,
                                        final ServiceDescriptor[] candidates )
    {
        for( int i = 0; i < candidates.length; i++ )
        {
            if( service.matches( candidates[ i ] ) )
            {
                return true;
            }
        }

        return false;
    }
}
