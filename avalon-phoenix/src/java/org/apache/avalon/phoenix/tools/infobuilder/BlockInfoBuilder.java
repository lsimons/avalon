/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.infobuilder;

import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.metainfo.BlockDescriptor;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * A BlockInfoBuilder is responsible for building <code>BlockInfo</code>
 * objects from Configuration objects. The format for Configuration object
 * is specified in the BlockInfo specification.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class BlockInfoBuilder
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( BlockInfoBuilder.class );

    /**
     * Create a <code>BlockInfo</code> object for specified classname from
     * specified configuration data.
     *
     * @param classname The classname of Block
     * @param info the BlockInfo configuration
     * @return the created BlockInfo
     * @exception ConfigurationException if an error occurs
     */
    public BlockInfo build( final String classname, final Configuration info )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message = REZ.getString( "creating-blockinfo", classname );
            getLogger().debug( message );
        }

        Configuration configuration = null;

        configuration = info.getChild( "services" );
        final ServiceDescriptor[] services = buildServices( configuration );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor[] dependencies = buildDependencies( classname, configuration );

        configuration = info.getChild( "block" );
        final BlockDescriptor descriptor = buildBlockDescriptor( classname, configuration );

        if( getLogger().isDebugEnabled() )
        {
            final String message = REZ.getString( "blockinfo-created",
                                                  classname,
                                                  new Integer( services.length ),
                                                  new Integer( dependencies.length ) );
            getLogger().debug( message );
        }

        return new BlockInfo( descriptor, services, dependencies );
    }

    /**
     * A utility method to build an array of <code>DependencyDescriptor</code>
     * objects from specified configuraiton and classname.
     *
     * @param classname The classname of Block (used for logging purposes)
     * @param configuration the dependencies configuration
     * @return the created DependencyDescriptor
     * @exception ConfigurationException if an error occurs
     */
    private DependencyDescriptor[] buildDependencies( final String classname,
                                                      final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] elements = configuration.getChildren( "dependency" );
        final ArrayList dependencies = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final DependencyDescriptor dependency =
                buildDependency( classname, elements[ i ] );
            dependencies.add( dependency );
        }

        return (DependencyDescriptor[])dependencies.toArray( new DependencyDescriptor[ 0 ] );
    }

    /**
     * A utility method to build a <code>DependencyDescriptor</code>
     * object from specified configuraiton.
     *
     * @param classname The classname of Block (used for logging purposes)
     * @param dependency the dependency configuration
     * @return the created DependencyDescriptor
     * @exception ConfigurationException if an error occurs
     */
    private DependencyDescriptor buildDependency( final String classname,
                                                  final Configuration dependency )
        throws ConfigurationException
    {
        final ServiceDescriptor service = buildService( dependency.getChild( "service" ) );
        String role = dependency.getChild( "role" ).getValue( null );

        //default to name of service if role unspecified
        if( null == role )
            role = service.getName();
        else
        {
            //If role is specified and it is the same as
            //service name then warn that it is redundent.
            if( role.equals( service.getName() ) )
            {
                final String message = REZ.getString( "redundent-role", classname, role );
                getLogger().warn( message );
            }
        }

        return new DependencyDescriptor( role, service );
    }

    /**
     * A utility method to build an array of <code>ServiceDescriptor</code>
     * objects from specified configuraiton.
     *
     * @param servicesSet the services configuration
     * @return the created ServiceDescriptor
     * @exception ConfigurationException if an error occurs
     */
    private ServiceDescriptor[] buildServices( final Configuration servicesSet )
        throws ConfigurationException
    {
        final Configuration[] elements = servicesSet.getChildren( "service" );
        final ArrayList services = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ] );
            services.add( service );
        }

        return (ServiceDescriptor[])services.toArray( new ServiceDescriptor[ 0 ] );
    }

    /**
     * A utility method to build a <code>ServiceDescriptor</code>
     * object from specified configuraiton data.
     *
     * @param service the service Configuration
     * @return the created ServiceDescriptor
     * @exception ConfigurationException if an error occurs
     */
    private ServiceDescriptor buildService( final Configuration service )
        throws ConfigurationException
    {
        final String name = service.getAttribute( "name" );
        final Version version = buildVersion( service.getAttribute( "version" ) );
        return new ServiceDescriptor( name, version );
    }

    /**
     * A utility method to build a <code>BlockDescriptor</code>
     * object from specified configuraiton data and classname.
     *
     * <p>Note that if a &lt;block/&gt; section is not specified then a warning
     * is generated as previous versions of Phoenix did not require such sections.
     * In the future this section will be required.</p>
     *
     * @param classname The classname of Block (used to create descriptor)
     * @param block the Block Configuration
     * @return the created BlockDescriptor
     * @exception ConfigurationException if an error occurs
     */
    private BlockDescriptor buildBlockDescriptor( final String classname,
                                                  final Configuration block )
        throws ConfigurationException
    {
        if( 0 == block.getChildren().length )
        {
            final String message = REZ.getString( "missing-block", classname );
            getLogger().warn( message );
            System.err.println( message );
            return null;
        }

        final Version version = buildVersion( block.getChild( "version" ).getValue() );

        return new BlockDescriptor( classname, version );
    }

    /**
     * A utility method to parse a Version object from specified string.
     *
     * @param version the version string
     * @return the created Version object
     * @exception ConfigurationException if an error occurs
     */
    private Version buildVersion( final String version )
        throws ConfigurationException
    {
        return Version.getVersion( version );
    }
}
