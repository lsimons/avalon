/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.framework.tools.infobuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.ComponentDescriptor;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.ContextDescriptor;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.framework.info.EntryDescriptor;
import org.apache.avalon.phoenix.framework.info.LoggerDescriptor;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.xml.sax.InputSource;

/**
 * A XMLInfoReader is responsible for building {@link ComponentInfo}
 * objects from Configuration objects. The format for Configuration object
 * is specified in the <a href="package-summary.html#external">package summary</a>.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/03/01 03:39:47 $
 */
public final class XMLInfoReader
    extends AbstractLogEnabled
    implements InfoReader
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( XMLInfoReader.class );

    /**
     * Create a {@link ComponentInfo} object for specified
     * classname, loaded from specified {@link InputStream}.
     *
     * @param implementationKey The classname of Component
     * @param inputStream the InputStream to load ComponentInfo from
     * @return the created ComponentInfo
     * @throws ConfigurationException if an error occurs
     */
    public ComponentInfo createComponentInfo( final String implementationKey,
                                              final InputStream inputStream )
        throws Exception
    {
        final InputSource input = new InputSource( inputStream );
        final Configuration configuration = ConfigurationBuilder.build( input );
        return buildComponentInfo( implementationKey, configuration );
    }

    /**
     * Create a {@link ComponentInfo} object for specified classname from
     * specified configuration data.
     *
     * @param classname The classname of Component
     * @param info the ComponentInfo configuration
     * @return the created ComponentInfo
     * @throws ConfigurationException if an error occurs
     */
    private ComponentInfo buildComponentInfo( final String classname,
                                              final Configuration info )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "builder.creating-info.notice",
                               classname );
            getLogger().debug( message );
        }

        final String topLevelName = info.getName();
        if( !topLevelName.equals( "component-info" ) )
        {
            final String message =
                REZ.getString( "builder.bad-toplevel-element.error",
                               classname,
                               topLevelName );
            throw new ConfigurationException( message );
        }

        Configuration configuration = null;
        configuration = info.getChild( "component" );
        final ComponentDescriptor descriptor = buildComponentDescriptor( configuration );
        final String implementationKey = descriptor.getImplementationKey();

        configuration = info.getChild( "loggers" );
        final LoggerDescriptor[] loggers = buildLoggers( configuration );

        configuration = info.getChild( "context" );
        final ContextDescriptor context = buildContext( configuration );

        configuration = info.getChild( "services" );
        final ServiceDescriptor[] services = buildServices( configuration );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor[] dependencies =
            buildDependencies( implementationKey, configuration );

        configuration = info.getChild( "configuration-schema", false );
        final SchemaDescriptor configurationSchema = buildSchema( configuration );

        configuration = info.getChild( "parameters-schema", false );
        final SchemaDescriptor parametersSchema = buildSchema( configuration );

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "builder.created-info.notice",
                               implementationKey,
                               new Integer( services.length ),
                               new Integer( dependencies.length ),
                               new Integer( context.getEntrys().length ),
                               new Integer( loggers.length ) );
            getLogger().debug( message );
        }

        return new ComponentInfo( descriptor, services,
                                  loggers, context, dependencies,
                                  configurationSchema, parametersSchema );
    }

    /**
     * A utility method to build a {@link SchemaDescriptor} object
     * from specified configuraiton.
     *
     * @param configuration the loggers configuration
     * @return the created Schema
     * @throws ConfigurationException if an error occurs
     */
    private SchemaDescriptor buildSchema( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == configuration )
        {
            return null;
        }
        final String location = configuration.getAttribute( "location", "" );
        final String type = configuration.getAttribute( "type", "" );
        final Attribute[] attributes = buildAttributes( configuration );
        return new SchemaDescriptor( location, type, attributes );
    }

    /**
     * A utility method to build an array of {@link LoggerDescriptor} objects
     * from specified configuraiton.
     *
     * @param configuration the loggers configuration
     * @return the created LoggerDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private LoggerDescriptor[] buildLoggers( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] elements = configuration.getChildren( "logger" );
        final ArrayList loggers = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final LoggerDescriptor logger = buildLogger( elements[ i ] );
            loggers.add( logger );
        }

        return (LoggerDescriptor[])loggers.toArray( new LoggerDescriptor[ loggers.size() ] );
    }

    /**
     * A utility method to build a {@link LoggerDescriptor}
     * object from specified configuraiton.
     *
     * @param logger the Logger configuration
     * @return the created LoggerDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private LoggerDescriptor buildLogger( Configuration logger )
        throws ConfigurationException
    {
        final String name = logger.getAttribute( "name", "" );
        final Attribute[] attributes = buildAttributes( logger );
        return new LoggerDescriptor( name, attributes );
    }

    /**
     * A utility method to build an array of {@link DependencyDescriptor}
     * objects from specified configuration and classname.
     *
     * @param classname The classname of Component (used for logging purposes)
     * @param configuration the dependencies configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
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

        return (DependencyDescriptor[])dependencies.toArray( DependencyDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link DependencyDescriptor}
     * object from specified configuraiton.
     *
     * @param classname The classname of Component (used for logging purposes)
     * @param dependency the dependency configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private DependencyDescriptor buildDependency( final String classname,
                                                  final Configuration dependency )
        throws ConfigurationException
    {
        final String implementationKey =
            dependency.getAttribute( "type" );
        final boolean optional =
            dependency.getAttributeAsBoolean( "optional", false );

        final Attribute[] attributes = buildAttributes( dependency );

        String key = dependency.getAttribute( "key", null );

        //default to name of service if key unspecified
        if( null == key )
        {
            key = implementationKey;
        }
        else
        {
            //If key is specified and it is the same as
            //service name then warn that it is redundent.
            if( key.equals( implementationKey ) )
            {
                final String message =
                    REZ.getString( "builder.redundent-key.notice",
                                   classname,
                                   key );
                getLogger().warn( message );
            }
        }

        return new DependencyDescriptor( key, implementationKey, optional, attributes );
    }

    /**
     * A utility method to build a {@link ContextDescriptor}
     * object from specified configuraiton.
     *
     * @param context the dependency configuration
     * @return the created ContextDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ContextDescriptor buildContext( final Configuration context )
        throws ConfigurationException
    {
        final EntryDescriptor[] entrys =
            buildEntrys( context.getChildren( "entry" ) );

        final Attribute[] attributes = buildAttributes( context );

        final String type =
            context.getAttribute( "type",
                                  ContextDescriptor.DEFAULT_TYPE );

        return new ContextDescriptor( type, entrys, attributes );
    }

    /**
     * A utility method to build an array of {@link EntryDescriptor}
     * objects from specified configuraiton.
     *
     * @param entrySet the set of entrys to build
     * @return the created {@link EntryDescriptor}s
     * @throws ConfigurationException if an error occurs
     */
    private EntryDescriptor[] buildEntrys( final Configuration[] entrySet )
        throws ConfigurationException
    {
        final ArrayList entrys = new ArrayList();

        for( int i = 0; i < entrySet.length; i++ )
        {
            final EntryDescriptor service = buildEntry( entrySet[ i ] );
            entrys.add( service );
        }

        return (EntryDescriptor[])entrys.toArray( new EntryDescriptor[ entrys.size() ] );
    }

    /**
     * Create a {@link EntryDescriptor} from configuration.
     *
     * @param config the configuration
     * @return the created {@link EntryDescriptor}
     * @throws ConfigurationException if an error occurs
     */
    private EntryDescriptor buildEntry( final Configuration config )
        throws ConfigurationException
    {
        final String key = config.getAttribute( "key" );
        final String type = config.getAttribute( "type" );
        final boolean optional =
            config.getAttributeAsBoolean( "optional", false );
        final Attribute[] attributes = buildAttributes( config );

        return new EntryDescriptor( key, type, optional, attributes );
    }

    /**
     * A utility method to build an array of {@link ServiceDescriptor}
     * objects from specified configuraiton.
     *
     * @param servicesSet the services configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
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

        return (ServiceDescriptor[])services.toArray( ServiceDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link ServiceDescriptor}
     * object from specified configuraiton data.
     *
     * @param service the service Configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ServiceDescriptor buildService( final Configuration service )
        throws ConfigurationException
    {
        final String type = service.getAttribute( "type" );
        final Attribute[] attributes = buildAttributes( service );
        return new ServiceDescriptor( type, attributes );
    }

    /**
     * Build up a list of attributes from specific config tree.
     *
     * @param config the attributes config
     * @return the set of attributes
     */
    private Attribute[] buildAttributes( final Configuration config )
        throws ConfigurationException
    {
        final ArrayList attributes = new ArrayList();

        final Configuration[] attributeConfigs = config.getChildren( "attribute" );
        for( int i = 0; i < attributeConfigs.length; i++ )
        {
            final Configuration attributeConfig = attributeConfigs[ i ];
            final Attribute attribute = buildAttribute( attributeConfig );
            attributes.add( attribute );
        }

        return (Attribute[])attributes.toArray( new Attribute[ attributes.size() ] );
    }

    /**
     * Build a attribute from a specific configuration.
     *
     * @param config the configuration to build attribute from
     * @return the new Attribute
     * @throws ConfigurationException if unable to build attribute due to malformed xml
     */
    private Attribute buildAttribute( Configuration config )
        throws ConfigurationException
    {
        final String name = config.getAttribute( "name" );
        final Properties parameters = buildParameters( config );
        if( 0 == parameters.size() )
        {
            return new Attribute( name, null );
        }
        else
        {
            return new Attribute( name, parameters );
        }
    }

    /**
     * Build up a list of parameters from specific config tree.
     *
     * @param config the parameters config
     * @return the Properties object representing parameters
     */
    private Properties buildParameters( final Configuration config )
        throws ConfigurationException
    {
        final Properties parameters = new Properties();
        final Configuration[] children = config.getChildren( "param" );
        for( int i = 0; i < children.length; i++ )
        {
            final Configuration child = children[ i ];
            final String key = child.getAttribute( "name" );
            final String value = child.getAttribute( "value" );
            parameters.setProperty( key, value );
        }
        return parameters;
    }

    /**
     * A utility method to build a {@link ComponentDescriptor}
     * object from specified configuraiton data and classname.
     *
     * @param config the Component Configuration
     * @return the created ComponentDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ComponentDescriptor buildComponentDescriptor( final Configuration config )
        throws ConfigurationException
    {
        final String type = config.getAttribute( "type" );
        final Attribute[] attributes = buildAttributes( config );

        return new ComponentDescriptor( type, attributes );
    }
}
