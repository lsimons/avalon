/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.verifier;

import java.util.ArrayList;
import java.util.Stack;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;
import org.apache.excalibur.containerkit.VerifyException;
import org.apache.excalibur.containerkit.Verifier;

/**
 * This Class verifies that Sars are valid. It performs a number
 * of checks to make sure that the Sar represents a valid
 * application and excluding runtime errors will start up validly.
 * Some of the checks it performs include;
 *
 * <ul>
 *   <li>Verify names of Sar, Blocks and BlockListeners contain only
 *       letters, digits or the '_' character.</li>
 *   <li>Verify that the names of the Blocks and BlockListeners are
 *       unique to Sar.</li>
 *   <li>Verify that the dependendencies specified in assembly.xml
 *       correspond to dependencies specified in BlockInfo files.</li>
 *   <li>Verify that the inter-block dependendencies specified in
 *       assembly.xml are valid. This essentially means that if
 *       Block A requires Service S from Block B then Block B must
 *       provide Service S.</li>
 *   <li>Verify that there are no circular dependendencies between
 *       blocks.</li>
 *   <li>Verify that the Class objects for Blocks support the Block
 *       interface and any specified Services.</li>
 *   <li>Verify that the Class objects for BlockListeners support the
 *       BlockListener interface.</li>
 * </ul>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.20 $ $Date: 2002/06/04 06:58:11 $
 */
public class SarVerifier
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SarVerifier.class );

    private static final Class[] FRAMEWORK_CLASSES = new Class[]
    {
        LogEnabled.class,
        Contextualizable.class,
        Composable.class,
        Serviceable.class,
        Configurable.class,
        Parameterizable.class,
        Initializable.class,
        Startable.class,
        Disposable.class
    };

    /**
     * Verify the specified <code>SarMetaData</code> object.
     * The rules used to verify <code>SarMetaData</code> are specified
     * in the Class javadocs.
     *
     * @param sar the SarMetaDat object
     * @param classLoader the ClassLoader used to load types. This is used
     *                    to verify that specified Class objects exist and
     *                    implement the correct interfaces.
     * @throws VerifyException if an error occurs
     */
    public void verifySar( final SarMetaData sar, final ClassLoader classLoader )
        throws VerifyException
    {
        final BlockMetaData[] blocks = sar.getBlocks();
        final BlockListenerMetaData[] listeners = sar.getListeners();

        String message = null;

        message = REZ.getString( "verify-valid-names" );
        getLogger().info( message );
        verifySarName( sar.getName() );
        verifyValidNames( blocks );
        verifyValidNames( listeners );

        message = REZ.getString( "verify-unique-names" );
        getLogger().info( message );
        checkNamesUnique( blocks, listeners );

        message = REZ.getString( "verify-dependencies-mapping" );
        getLogger().info( message );
        verifyValidDependencies( blocks );

        message = REZ.getString( "verify-dependency-references" );
        getLogger().info( message );
        verifyDependencyReferences( blocks );

        message = REZ.getString( "verify-nocircular-dependencies" );
        getLogger().info( message );
        verifyNoCircularDependencies( blocks );

        message = REZ.getString( "verify-block-type" );
        getLogger().info( message );
        verifyBlocksType( blocks, classLoader );

        message = REZ.getString( "verify-listener-type" );
        getLogger().info( message );
        verifyListenersType( listeners, classLoader );
    }

    /**
     * Verfiy that all Blocks have the needed dependencies specified correctly.
     *
     * @param blocks the BlockMetaData objects for the blocks
     * @throws VerifyException if an error occurs
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
     * Verfiy that there are no circular references between Blocks.
     *
     * @param blocks the BlockMetaData objects for the blocks
     * @throws VerifyException if an error occurs
     */
    private void verifyNoCircularDependencies( final BlockMetaData[] blocks )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            final BlockMetaData block = blocks[ i ];

            final Stack stack = new Stack();
            stack.push( block );
            verifyNoCircularDependencies( block, blocks, stack );
            stack.pop();
        }
    }

    /**
     * Verfiy that there are no circular references between Blocks.
     *
     * @param blocks the BlockMetaData objects for the blocks
     * @throws VerifyException if an error occurs
     */
    private void verifyNoCircularDependencies( final BlockMetaData block,
                                               final BlockMetaData[] blocks,
                                               final Stack stack )
        throws VerifyException
    {
        final BlockMetaData[] dependencies = getDependencies( block, blocks );

        for( int i = 0; i < dependencies.length; i++ )
        {
            final BlockMetaData dependency = dependencies[ i ];
            if( stack.contains( dependency ) )
            {
                final String trace = getDependencyTrace( dependency, stack );
                final String message =
                    REZ.getString( "dependency-circular", block.getName(), trace );
                throw new VerifyException( message );
            }

            stack.push( dependency );
            verifyNoCircularDependencies( dependency, blocks, stack );
            stack.pop();
        }
    }

    /**
     * Get a string defining path from top of stack till it reaches specified block.
     *
     * @param block the block
     * @param stack the Stack
     * @return the path of dependency
     */
    private String getDependencyTrace( final BlockMetaData block,
                                       final Stack stack )
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "[ " );

        final String name = block.getName();
        final int size = stack.size();
        final int top = size - 1;
        for( int i = top; i >= 0; i-- )
        {
            final BlockMetaData other = (BlockMetaData)stack.get( i );
            if( top != i )
            {
                sb.append( ", " );
            }
            sb.append( other.getName() );

            if( other.getName().equals( name ) )
            {
                break;
            }
        }

        sb.append( ", " );
        sb.append( name );

        sb.append( " ]" );
        return sb.toString();
    }

    /**
     * Get array of dependencies for specified Block from specified Block array.
     *
     * @param block the block to get dependencies of
     * @param blocks the total set of blocks in application
     * @return the dependencies of block
     */
    private BlockMetaData[] getDependencies( final BlockMetaData block,
                                             final BlockMetaData[] blocks )
    {
        final ArrayList dependencies = new ArrayList();
        final DependencyMetaData[] deps = block.getDependencies();

        for( int i = 0; i < deps.length; i++ )
        {
            final String name = deps[ i ].getName();
            final BlockMetaData other = getBlock( name, blocks );
            dependencies.add( other );
        }

        return (BlockMetaData[])dependencies.toArray( new BlockMetaData[ 0 ] );
    }

    /**
     * Verfiy that the inter-Block dependencies are valid.
     *
     * @param blocks the BlockMetaData objects for the blocks
     * @throws VerifyException if an error occurs
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
     * @throws VerifyException if an error occurs
     */
    private void verifyDependencyReferences( final BlockMetaData block,
                                             final BlockMetaData[] others )
        throws VerifyException
    {
        final BlockInfo info = block.getBlockInfo();
        final DependencyMetaData[] roles = block.getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final String blockName = roles[ i ].getName();
            final String roleName = roles[ i ].getRole();
            final ServiceDescriptor service =
                info.getDependency( roleName ).getService();

            //Get the other block that is providing service
            final BlockMetaData other = getBlock( blockName, others );
            if( null == other )
            {
                final String message =
                    REZ.getString( "dependency-noblock", blockName, block.getName() );
                throw new VerifyException( message );
            }

            //make sure that the block offers service
            //that user expects it to be providing
            final ServiceDescriptor[] services = other.getBlockInfo().getServices();
            if( !hasMatchingService( service, services ) )
            {
                final String message =
                    REZ.getString( "dependency-noservice", blockName, service, block.getName() );
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
     * @throws VerifyException if an error occurs
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
     * @throws VerifyException if an error occurs
     */
    private void verifyBlockType( final BlockMetaData block, final ClassLoader classLoader )
        throws VerifyException
    {
        final String name = block.getName();
        final String classname = block.getClassname();
        Class clazz = null;
        try
        {
            clazz = classLoader.loadClass( classname );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "bad-block-class",
                                                  name,
                                                  classname,
                                                  e.getMessage() );
            throw new VerifyException( message );
        }

        final Class[] interfaces =
            getServiceClasses( name,
                               block.getBlockInfo().getServices(),
                               classLoader );

        verifyAvalonComponent( name, clazz, interfaces );

        for( int i = 0; i < interfaces.length; i++ )
        {
            if( !interfaces[ i ].isAssignableFrom( clazz ) )
            {
                final String message = REZ.getString( "block-noimpl-service",
                                                      name,
                                                      classname,
                                                      interfaces[ i ].getName() );
                throw new VerifyException( message );
            }
        }

        if( Block.class.isAssignableFrom( clazz ) )
        {
            final String message =
                REZ.getString( "verifier.implements-block.error",
                               name,
                               classname );
            getLogger().error( message );
            System.err.println( message );
        }

    }

    /**
     * Verify specified object satisifies the rules of being abn AValon component.
     *
     * @param name the components name
     * @param clazz the implementation class
     * @param interfaces the service classes
     */
    private void verifyAvalonComponent( final String name, Class clazz, final Class[] interfaces )
    {
        try
        {
            final Verifier verifier = new Verifier();
            setupLogger( verifier );
            verifier.
                verifyComponent( name, clazz, interfaces );
        }
        catch( VerifyException ve )
        {
            //ignore as the above will print out
            //error. However the verifier is too
            //strict and we need to be more lax for backwards
            //compatability
        }
    }

    /**
     * Verfiy that all listeners implement BlockListener.
     *
     * @param listeners the BlockListenerMetaData objects for the listeners
     * @throws VerifyException if an error occurs
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
     * @throws VerifyException if an error occurs
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
            final String message =
                REZ.getString( "bad-listener-class",
                               listener.getName(),
                               listener.getClassname(),
                               e.getMessage() );
            throw new VerifyException( message, e );
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
     * Verify that the Sat name specified is valid.
     *
     * @param name the sar name
     * @throws VerifyException if an error occurs
     */
    private void verifySarName( final String name )
        throws VerifyException
    {
        if( !isValidName( name ) )
        {
            final String message = REZ.getString( "invalid-sar-name", name );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the names of the specified blocks are valid.
     *
     * @param blocks the Blocks
     * @throws VerifyException if an error occurs
     */
    private void verifyValidNames( final BlockMetaData[] blocks )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            final String name = blocks[ i ].getName();
            if( !isValidName( name ) )
            {
                final String message = REZ.getString( "invalid-block-name", name );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Verify that the names of the specified listeners are valid.
     *
     * @param listeners the listeners
     * @throws VerifyException if an error occurs
     */
    private void verifyValidNames( final BlockListenerMetaData[] listeners )
        throws VerifyException
    {
        for( int i = 0; i < listeners.length; i++ )
        {
            final String name = listeners[ i ].getName();
            if( !isValidName( name ) )
            {
                final String message = REZ.getString( "invalid-listener-name", name );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Return true if specified name is valid.
     * Valid names consist of letters, digits or the '_' character.
     *
     * @param name the name to check
     * @return true if valid, false otherwise
     */
    private boolean isValidName( final String name )
    {
        final int size = name.length();
        for( int i = 0; i < size; i++ )
        {
            final char ch = name.charAt( i );

            if( !Character.isLetterOrDigit( ch ) && '-' != ch )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Verify that the names of the specified blocks and listeners are unique.
     * It is not valid for the same name to be used in multiple Blocks and or
     * BlockListeners.
     *
     * @param blocks the Blocks
     * @param listeners the listeners
     * @throws VerifyException if an error occurs
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
     * @throws VerifyException if an error occurs
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
     * Retrieve a list of DependencyMetaData objects for BlockMetaData
     * and verify that there is a 1 to 1 map with dependencies specified
     * in BlockInfo.
     *
     * @param block the BlockMetaData describing the block
     * @throws VerifyException if an error occurs
     */
    private void verifyDependenciesMap( final BlockMetaData block )
        throws VerifyException
    {
        //Make sure all role entries specified in config file are valid
        final DependencyMetaData[] roles = block.getDependencies();
        for( int i = 0; i < roles.length; i++ )
        {
            final String roleName = roles[ i ].getRole();
            final DependencyDescriptor descriptor = block.getBlockInfo().getDependency( roleName );

            //If there is no dependency descriptor in BlockInfo then
            //user has specified an uneeded dependency.
            if( null == descriptor )
            {
                final String message = REZ.getString( "unknown-dependency",
                                                      roles[ i ].getName(),
                                                      roleName,
                                                      block.getName() );
                throw new VerifyException( message );
            }
        }

        //Make sure all dependencies in BlockInfo file are satisfied
        final DependencyDescriptor[] dependencies = block.getBlockInfo().getDependencies();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyMetaData role = block.getDependency( dependencies[ i ].getRole() );

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
     * @throws VerifyException if an error occurs
     */
    private Class[] getServiceClasses( final String name,
                                       final ServiceDescriptor[] services,
                                       final ClassLoader classLoader )
        throws VerifyException
    {
        final Class[] classes = new Class[ services.length ];

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

            checkNotFrameworkInterface( name, classname, classes[ i ] );
        }

        return classes;
    }

    /**
     * Warn the user if any of the service interfaces extend
     * a Lifecycle interface (a generally unrecomended approach).
     *
     * @param name the name of block
     * @param classname the classname of block
     * @param clazz the service implemented by block
     */
    private void checkNotFrameworkInterface( final String name,
                                             final String classname,
                                             final Class clazz )
    {
        for( int i = 0; i < FRAMEWORK_CLASSES.length; i++ )
        {
            final Class lifecycle = FRAMEWORK_CLASSES[ i ];
            if( lifecycle.isAssignableFrom( clazz ) )
            {
                final String message =
                    REZ.getString( "verifier.service-isa-lifecycle.error",
                                   name,
                                   classname,
                                   clazz.getName(),
                                   lifecycle.getName() );
                getLogger().warn( message );
                System.err.println( message );
            }
        }
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
