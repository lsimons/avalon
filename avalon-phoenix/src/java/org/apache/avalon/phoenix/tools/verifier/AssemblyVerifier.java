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
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.containerkit.metadata.ComponentMetaData;
import org.apache.excalibur.containerkit.metadata.DependencyMetaData;
import org.apache.excalibur.containerkit.metainfo.ComponentInfo;
import org.apache.excalibur.containerkit.metainfo.DependencyDescriptor;
import org.apache.excalibur.containerkit.metainfo.ServiceDescriptor;
import org.apache.excalibur.containerkit.verifier.Verifier;
import org.apache.excalibur.containerkit.verifier.VerifyException;

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
 *       correspond to dependencies specified in ComponentInfo files.</li>
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
 * @version $Revision: 1.2 $ $Date: 2002/06/04 08:47:30 $
 * @todo redo documentation and all the i18n strings
 */
public class AssemblyVerifier
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AssemblyVerifier.class );
    private final Verifier m_verifier = new Verifier();

    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_verifier );
    }

    /**
     * Validate and Verify the specified assembly (ie organization
     * of components). See the Class Javadocs for the rules and
     * regulations of assembly.
     *
     * @param components the Components that make up assembly
     * @param classLoader the ClassLoader used to load types. This is used
     *                    to verify that specified Class objects exist and
     *                    implement the correct interfaces.
     * @throws VerifyException if an error occurs
     */
    public void verifySar( final ComponentMetaData[] components,
                           final ClassLoader classLoader )
        throws VerifyException
    {
        String message = null;

        message = REZ.getString( "verify-valid-names" );
        getLogger().info( message );
        verifyValidNames( components );

        message = REZ.getString( "verify-unique-names" );
        getLogger().info( message );
        checkNamesUnique( components );

        message = REZ.getString( "verify-dependencies-mapping" );
        getLogger().info( message );
        verifyValidDependencies( components );

        message = REZ.getString( "verify-dependency-references" );
        getLogger().info( message );
        verifyDependencyReferences( components );

        message = REZ.getString( "verify-nocircular-dependencies" );
        getLogger().info( message );
        verifyNoCircularDependencies( components );

        message = REZ.getString( "verify-block-type" );
        getLogger().info( message );
        verifyTypes( components, classLoader );
    }

    /**
     * Verfiy that all Blocks have the needed dependencies specified correctly.
     *
     * @param blocks the ComponentMetaData objects for the blocks
     * @throws VerifyException if an error occurs
     */
    private void verifyValidDependencies( final ComponentMetaData[] blocks )
        throws VerifyException
    {
        for( int i = 0; i < blocks.length; i++ )
        {
            verifyDependenciesMap( blocks[ i ] );
        }
    }

    /**
     * Verfiy that there are no circular references between Components.
     *
     * @param components the ComponentMetaData objects for the components
     * @throws VerifyException if an error occurs
     */
    private void verifyNoCircularDependencies( final ComponentMetaData[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentMetaData component = components[ i ];

            final Stack stack = new Stack();
            stack.push( component );
            verifyNoCircularDependencies( component, components, stack );
            stack.pop();
        }
    }

    /**
     * Verfiy that there are no circular references between Components.
     *
     * @param components the ComponentMetaData objects for the components
     * @throws VerifyException if an error occurs
     */
    private void verifyNoCircularDependencies( final ComponentMetaData component,
                                               final ComponentMetaData[] components,
                                               final Stack stack )
        throws VerifyException
    {
        final ComponentMetaData[] dependencies = getDependencies( component, components );

        for( int i = 0; i < dependencies.length; i++ )
        {
            final ComponentMetaData dependency = dependencies[ i ];
            if( stack.contains( dependency ) )
            {
                final String trace = getDependencyTrace( dependency, stack );
                final String message =
                    REZ.getString( "dependency-circular",
                                   component.getName(),
                                   trace );
                throw new VerifyException( message );
            }

            stack.push( dependency );
            verifyNoCircularDependencies( dependency, components, stack );
            stack.pop();
        }
    }

    /**
     * Get a string defining path from top of stack till
     * it reaches specified component.
     *
     * @param component the component
     * @param stack the Stack
     * @return the path of dependency
     */
    private String getDependencyTrace( final ComponentMetaData component,
                                       final Stack stack )
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "[ " );

        final String name = component.getName();
        final int size = stack.size();
        final int top = size - 1;
        for( int i = top; i >= 0; i-- )
        {
            final ComponentMetaData other = (ComponentMetaData)stack.get( i );
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
     * Get array of dependencies for specified Component from specified
     * Component array.
     *
     * @param component the component to get dependencies of
     * @param components the total set of components in application
     * @return the dependencies of component
     */
    private ComponentMetaData[] getDependencies( final ComponentMetaData component,
                                                 final ComponentMetaData[] components )
    {
        final ArrayList dependencies = new ArrayList();
        final DependencyMetaData[] deps = component.getDependencies();

        for( int i = 0; i < deps.length; i++ )
        {
            final String name = deps[ i ].getName();
            final ComponentMetaData other = getComponentMetaData( name, components );
            dependencies.add( other );
        }

        return (ComponentMetaData[])dependencies.toArray( new ComponentMetaData[ 0 ] );
    }

    /**
     * Verfiy that the inter-Component dependencies are valid.
     *
     * @param components the ComponentMetaData objects for the components
     * @throws VerifyException if an error occurs
     */
    private void verifyDependencyReferences( final ComponentMetaData[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            verifyDependencyReferences( components[ i ], components );
        }
    }

    /**
     * Verfiy that the inter-Component dependencies are valid for specified Component.
     *
     * @param component the ComponentMetaData object for the component
     * @param others the ComponentMetaData objects for the other components
     * @throws VerifyException if an error occurs
     */
    private void verifyDependencyReferences( final ComponentMetaData component,
                                             final ComponentMetaData[] others )
        throws VerifyException
    {
        final ComponentInfo info = component.getComponentInfo();
        final DependencyMetaData[] roles = component.getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final String providerName = roles[ i ].getName();
            final String roleName = roles[ i ].getRole();
            final ServiceDescriptor service =
                info.getDependency( roleName ).getService();

            //Get the other block that is providing service
            final ComponentMetaData provider = getComponentMetaData( providerName, others );
            if( null == provider )
            {
                final String message =
                    REZ.getString( "dependency-noblock",
                                   providerName,
                                   component.getName() );
                throw new VerifyException( message );
            }

            //make sure that the block offers service
            //that user expects it to be providing
            final ServiceDescriptor[] services = provider.getComponentInfo().getServices();
            if( !hasMatchingService( service, services ) )
            {
                final String message =
                    REZ.getString( "dependency-noservice",
                                   providerName,
                                   service,
                                   component.getName() );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Get Block with specified name from specified Block array.
     *
     * @param name the name of block to get
     * @param components the array of Blocks to search
     * @return the Block if found, else null
     */
    private ComponentMetaData getComponentMetaData( final String name,
                                                    final ComponentMetaData[] components )
    {
        for( int i = 0; i < components.length; i++ )
        {
            if( components[ i ].getName().equals( name ) )
            {
                return components[ i ];
            }
        }

        return null;
    }

    /**
     * Verfiy that all Components specify classes that implement the
     * advertised interfaces.
     *
     * @param components the ComponentMetaData objects for the components
     * @throws VerifyException if an error occurs
     */
    private void verifyTypes( final ComponentMetaData[] components,
                                   final ClassLoader classLoader )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            verifyType( components[ i ], classLoader );
        }
    }

    /**
     * Verfiy that specified Block designate classes that implement the
     * advertised interfaces.
     *
     * @param block the ComponentMetaData object for the blocks
     * @throws VerifyException if an error occurs
     */
    private void verifyType( final ComponentMetaData block, final ClassLoader classLoader )
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
            final String message =
                REZ.getString( "bad-block-class",
                                                  name,
                                                  classname,
                                                  e.getMessage() );
            throw new VerifyException( message );
        }

        final Class[] interfaces =
            getServiceClasses( name,
                               block.getComponentInfo().getServices(),
                               classLoader );

        m_verifier.verifyComponent( name, clazz, interfaces );
    }

    /**
     * Verify that the names of the specified Components are valid.
     *
     * @param components the Components metadata
     * @throws VerifyException if an error occurs
     */
    private void verifyValidNames( final ComponentMetaData[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            final String name = components[ i ].getName();
            if( !isValidName( name ) )
            {
                final String message =
                    REZ.getString( "invalid-block-name", name );
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
     * @param components the Components
     * @throws VerifyException if an error occurs
     */
    private void checkNamesUnique( final ComponentMetaData[] components )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            final String name = components[ i ].getName();
            verifyUniqueName( components, name, i );
        }
    }

    /**
     * Verfify that specified name is unique among the specified components.
     *
     * @param components the array of components to check
     * @param name the name of component
     * @param index the index of component in array (so we can skip it)
     * @throws VerifyException if names are not unique
     */
    private void verifyUniqueName( final ComponentMetaData[] components,
                                   final String name,
                                   final int index )
        throws VerifyException
    {
        for( int i = 0; i < components.length; i++ )
        {
            final String other = components[ i ].getName();
            if( index != i && other.equals( name ) )
            {
                final String message =
                    REZ.getString( "duplicate-name", name );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Retrieve a list of DependencyMetaData objects for ComponentMetaData
     * and verify that there is a 1 to 1 map with dependencies specified
     * in ComponentInfo.
     *
     * @param block the ComponentMetaData describing the block
     * @throws VerifyException if an error occurs
     */
    private void verifyDependenciesMap( final ComponentMetaData block )
        throws VerifyException
    {
        //Make sure all role entries specified in config file are valid
        final DependencyMetaData[] roles = block.getDependencies();
        for( int i = 0; i < roles.length; i++ )
        {
            final String roleName = roles[ i ].getRole();
            final DependencyDescriptor descriptor = block.getComponentInfo().getDependency( roleName );

            //If there is no dependency descriptor in ComponentInfo then
            //user has specified an uneeded dependency.
            if( null == descriptor )
            {
                final String message =
                    REZ.getString( "unknown-dependency",
                                   roles[ i ].getName(),
                                   roleName,
                                   block.getName() );
                throw new VerifyException( message );
            }
        }

        //Make sure all dependencies in ComponentInfo file are satisfied
        final DependencyDescriptor[] dependencies = block.getComponentInfo().getDependencies();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyMetaData role = block.getDependency( dependencies[ i ].getRole() );

            //If there is no Role then the user has failed
            //to specify a needed dependency.
            if( null == role )
            {
                final String message =
                    REZ.getString( "unspecified-dependency",
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
        }

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
