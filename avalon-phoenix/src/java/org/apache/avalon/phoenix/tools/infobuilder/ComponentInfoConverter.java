/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.infobuilder;

import java.util.ArrayList;
import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.info.ComponentDescriptor;
import org.apache.avalon.framework.info.ComponentInfo;
import org.apache.avalon.framework.info.FeatureDescriptor;
import org.apache.avalon.framework.info.Tag;
import org.apache.avalon.phoenix.metainfo.BlockDescriptor;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * Convert a {@link ComponentInfo} into a {@link BlockInfo}.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/15 02:00:18 $
 */
public class ComponentInfoConverter
{
    private ComponentInfoConverter()
    {
    }

    public static BlockInfo toBlockInfo( final ComponentInfo component )
    {
        final BlockDescriptor descriptor =
            toBlockDescriptor( component.getComponentDescriptor() );
        final ServiceDescriptor[] services =
            toPhoenixServices( component.getServices() );
        final ServiceDescriptor[] mxServices =
            getMXServices( component.getServices() );
        final DependencyDescriptor[] dependencys =
            toPhoenixDependencys( component.getDependencies() );

        return new BlockInfo( descriptor,
                              services,
                              mxServices,
                              dependencys );

    }

    private static ServiceDescriptor[] getMXServices(
        final org.apache.avalon.framework.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            final Tag tag = services[ i ].getTag( "mx" );
            if( null != tag )
            {
                serviceSet.add( toPhoenixService( services[ i ] ) );
            }
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    private static ServiceDescriptor[] toPhoenixServices(
        final org.apache.avalon.framework.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            serviceSet.add( toPhoenixService( services[ i ] ) );
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    private static ServiceDescriptor toPhoenixService(
        final org.apache.avalon.framework.info.ServiceDescriptor service )
    {
        final Version version = toVersion( service );
        final String classname = service.getImplementationKey();
        return new ServiceDescriptor( classname, version );
    }

    private static DependencyDescriptor[] toPhoenixDependencys(
        final org.apache.avalon.framework.info.DependencyDescriptor[] dependencies )
    {
        final ArrayList depends = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ )
        {
            depends.add( toPhoenixDependency( dependencies[ i ] ) );
        }
        return (DependencyDescriptor[])depends.toArray( new DependencyDescriptor[ depends.size() ] );
    }

    private static DependencyDescriptor toPhoenixDependency(
        final org.apache.avalon.framework.info.DependencyDescriptor dependency )
    {
        final Version version = toVersion( dependency );
        final ServiceDescriptor service =
            new ServiceDescriptor( dependency.getService(), version );
        return new DependencyDescriptor( dependency.getKey(), service );
    }

    private static BlockDescriptor toBlockDescriptor( final ComponentDescriptor component )
    {
        final Version version = toVersion( component );
        String schemaType = null;
        final Tag tag = component.getTag( "phoenix" );
        if( null != tag )
        {
            schemaType = tag.getParameter( "schema-type" );
        }

        return new BlockDescriptor( component.getName(),
                                    component.getImplementationKey(),
                                    schemaType,
                                    version );
    }

    private static Version toVersion( final FeatureDescriptor component )
    {
        final Tag tag = component.getTag( "avalon" );
        Version version = new Version( 1, 0, 0 );
        if( null != tag )
        {
            final String versionString = tag.getParameter( "version" );
            if( null != versionString )
            {
                version = Version.getVersion( versionString );
            }
        }
        return version;
    }
}
