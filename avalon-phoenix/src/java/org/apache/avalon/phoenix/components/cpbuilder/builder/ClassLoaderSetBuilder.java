/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.cpbuilder.builder;

import java.util.ArrayList;
import org.apache.avalon.excalibur.extension.Extension;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.ClassLoaderDef;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.ClassLoaderSetDef;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.EntryDef;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.FileSetDef;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.JoinDef;

/**
 * This class builds a {@link ClassLoaderSetDef} object from
 * specified configuration.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2002/09/01 04:17:46 $
 */
public class ClassLoaderSetBuilder
{
    public ClassLoaderSetDef build( final Configuration config,
                                    final String[] predefined )
        throws ConfigurationException
    {
        final String defaultClassLoader =
            config.getAttribute( "default" );

        final String version =
            config.getAttribute( "version" );
        if( !"1.0".equals( version ) )
        {
            final String message = "Bad version:" + version;
            throw new ConfigurationException( message );
        }

        final Configuration[] joinConfigs =
            config.getChildren( "join" );
        final JoinDef[] joins = buildJoins( joinConfigs );

        final Configuration[] clConfigs =
            config.getChildren( "classloader" );

        final ClassLoaderDef[] classloaders =
            buildClassLoaders( clConfigs );

        return new ClassLoaderSetDef( defaultClassLoader,
                                      predefined,
                                      classloaders,
                                      joins );
    }

    private ClassLoaderDef[] buildClassLoaders( Configuration[] configs )
        throws ConfigurationException
    {
        final ArrayList loaders = new ArrayList();

        for( int i = 0; i < configs.length; i++ )
        {
            final ClassLoaderDef loader = buildLoader( configs[ i ] );
            loaders.add( loader );
        }

        return (ClassLoaderDef[])loaders.toArray( new ClassLoaderDef[ loaders.size() ] );
    }

    private ClassLoaderDef buildLoader( final Configuration config )
        throws ConfigurationException
    {
        final String name = config.getAttribute( "name" );
        final String parent = config.getAttribute( "parent" );

        final EntryDef[] entrys =
            buildEntrys( config.getChildren( "entry" ) );
        final Extension[] extensions =
            buildExtensions( config.getChildren( "extension" ) );
        final FileSetDef[] fileSets =
            buildFileSets( config.getChildren( "fileset" ) );
        return new ClassLoaderDef( name, parent, entrys,
                                   extensions, fileSets );
    }

    private Extension[] buildExtensions( final Configuration[] configs )
        throws ConfigurationException
    {
        final ArrayList extensions = new ArrayList();

        for( int i = 0; i < configs.length; i++ )
        {
            final Extension extension =
                buildExtension( configs[ i ] );
            extensions.add( extension );
        }

        return (Extension[])extensions.toArray( new Extension[ extensions.size() ] );
    }

    private Extension buildExtension( final Configuration config )
        throws ConfigurationException
    {
        final String name =
            config.getChild( "name" ).getValue();
        final String specVersion =
            config.getChild( "specification-version" ).getValue( null );
        final String specVendor =
            config.getChild( "specification-vendor" ).getValue( null );
        final String implVersion =
            config.getChild( "implementation-version" ).getValue( null );
        final String implVendor =
            config.getChild( "implementation-vendor" ).getValue( null );
        final String implVendorID =
            config.getChild( "implementation-vendor-id" ).getValue( null );
        final String implURL =
            config.getChild( "implementation-url" ).getValue( null );

        return new Extension( name, specVersion, specVendor,
                              implVersion, implVendor, implVendorID,
                              implURL );
    }

    private FileSetDef[] buildFileSets( final Configuration[] configs )
        throws ConfigurationException
    {
        final ArrayList fileSets = new ArrayList();

        for( int i = 0; i < configs.length; i++ )
        {
            final FileSetDef fileSet =
                buildFileSet( configs[ i ] );
            fileSets.add( fileSet );
        }

        return (FileSetDef[])fileSets.toArray( new FileSetDef[ fileSets.size() ] );
    }

    private FileSetDef buildFileSet( Configuration config )
        throws ConfigurationException
    {
        final String dir = config.getAttribute( "dir" );
        final String[] includes =
            buildSelectors( config.getChildren( "include" ) );
        final String[] excludes =
            buildSelectors( config.getChildren( "exclude" ) );
        return new FileSetDef( dir, includes, excludes );
    }

    private String[] buildSelectors( Configuration[] configs )
        throws ConfigurationException
    {
        final ArrayList selectors = new ArrayList();

        for( int i = 0; i < configs.length; i++ )
        {
            final String name =
                configs[ i ].getAttribute( "name" );
            selectors.add( name );
        }

        return (String[])selectors.toArray( new String[ selectors.size() ] );
    }

    private EntryDef[] buildEntrys( final Configuration[] configs )
        throws ConfigurationException
    {
        final ArrayList entrys = new ArrayList();

        for( int i = 0; i < configs.length; i++ )
        {
            final EntryDef entry = buildEntry( configs[ i ] );
            entrys.add( entry );
        }

        return (EntryDef[])entrys.toArray( new EntryDef[ entrys.size() ] );
    }

    private EntryDef buildEntry( final Configuration config )
        throws ConfigurationException
    {
        final String location = config.getAttribute( "location" );
        return new EntryDef( location );
    }

    private JoinDef[] buildJoins( final Configuration[] configs )
        throws ConfigurationException
    {
        final ArrayList joins = new ArrayList();

        for( int i = 0; i < configs.length; i++ )
        {
            final JoinDef join = buildJoin( configs[ i ] );
            joins.add( join );
        }

        return (JoinDef[])joins.toArray( new JoinDef[ joins.size() ] );
    }

    private JoinDef buildJoin( final Configuration config )
        throws ConfigurationException
    {
        final String name = config.getAttribute( "name" );
        final Configuration[] children =
            config.getChildren( "classloader-ref" );
        final String[] classloaders =
            buildClassLoaderRefs( children );
        return new JoinDef( name, classloaders );
    }

    private String[] buildClassLoaderRefs( final Configuration[] configs )
        throws ConfigurationException
    {
        final ArrayList refs = new ArrayList();

        for( int i = 0; i < configs.length; i++ )
        {
            final String ref = configs[ i ].getAttribute( "name" );
            refs.add( ref );
        }

        return (String[])refs.toArray( new String[ refs.size() ] );
    }
}
