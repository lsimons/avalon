/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.demo;

import java.util.ArrayList;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.info.Attribute;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.MetaDataBuilder;
import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;
import org.xml.sax.InputSource;

/**
 * Load metadata from some source. The source is usually
 * one or more xml config files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:42 $
 */
public class SimpleMetaDataBuilder
    implements MetaDataBuilder
{
    public static final String CONFIG_LOCATION = "simple:location";

    public PartitionMetaData buildAssembly( final Map parameters )
        throws Exception
    {
        final String location = (String)parameters.get( CONFIG_LOCATION );
        final ComponentMetaData[] components = loadMetaData( location );
        return new PartitionMetaData( "main", new String[ 0 ],
                                      new PartitionMetaData[ 0 ],
                                      components,
                                      Attribute.EMPTY_SET );
    }

    private ComponentMetaData[] loadMetaData( final String location )
        throws Exception
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final InputSource input = new InputSource( location );

        final Configuration configuration = builder.build( input );
        final Configuration[] children =
            configuration.getChildren( "component" );
        return loadComponentDatas( children );
    }

    private ComponentMetaData[] loadComponentDatas( final Configuration[] components )
        throws Exception
    {
        final ArrayList profiles = new ArrayList();

        for( int i = 0; i < components.length; i++ )
        {
            final ComponentMetaData component =
                loadComponentData( components[ i ] );
            profiles.add( component );
        }

        return (ComponentMetaData[])profiles.toArray( new ComponentMetaData[ profiles.size() ] );
    }

    private ComponentMetaData loadComponentData( final Configuration component )
        throws Exception
    {
        final String name = component.getAttribute( "name" );
        final String impl = component.getAttribute( "impl" );
        final Configuration config = component.getChild( "config" );
        final DependencyMetaData[] dependencies =
            parseAssociations( component.getChildren( "provide" ) );

        return new ComponentMetaData( name, impl, dependencies, null, config, null );
    }

    private DependencyMetaData[] parseAssociations( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList associations = new ArrayList();
        for( int i = 0; i < provides.length; i++ )
        {
            final Configuration provide = provides[ i ];
            final String key = provide.getAttribute( "key" );
            final String provider = provide.getAttribute( "provider" );
            final DependencyMetaData association =
                new DependencyMetaData( key, provider, key, Attribute.EMPTY_SET );
            associations.add( association );
        }
        return (DependencyMetaData[])associations.toArray( new DependencyMetaData[ associations.size() ] );
    }
}
