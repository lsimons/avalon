/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.SAXException;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.property.PropertyException;
import org.apache.avalon.excalibur.property.PropertyUtil;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.SystemManager;
import org.apache.excalibur.configuration.merged.ConfigurationMerger;

/**
 * Repository which persistently stores configuration information on disk
 *
 * THIS IS A WORK IN PROGRESS AND WILL CHANGE BEWARE OF USE BEWAAAARE!
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class FileSystemPersistentConfigurationRepository extends AbstractLogEnabled
    implements ConfigurationRepository, Parameterizable, Configurable, Startable, Serviceable,
    Initializable, PersistentConfigurationRepositoryMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( FileSystemPersistentConfigurationRepository.class );

    private final HashMap m_persistedConfigurations = new HashMap();
    private final HashMap m_configurations = new HashMap();

    private ServiceManager m_serviceManager;
    private String m_phoenixHome;

    private File m_storageDirectory;

    public void parameterize( final Parameters parameters ) throws ParameterException
    {
        this.m_phoenixHome = parameters.getParameter( "phoenix.home", ".." );
    }

    public void service( ServiceManager manager )
        throws ServiceException
    {
        this.m_serviceManager = manager;
    }

    private Context createConfigurationContext()
    {
        final DefaultContext ctx = new DefaultContext();

        ctx.put( "phoenix.home", this.m_phoenixHome );

        return ctx;
    }

    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        this.m_storageDirectory = new File( constructStoragePath( configuration ) );

        ensureDirectoryExists( this.m_storageDirectory );
    }

    private void ensureDirectoryExists( File dir ) throws ConfigurationException
    {
        if( !dir.isDirectory() )
        {
            if( dir.exists() )
            {
                final String message = REZ.getString( "config.error.dir.isfile", dir );

                throw new ConfigurationException( message );
            }
            else if( !dir.mkdirs() )
            {
                final String message = REZ.getString( "config.error.dir.nomake", dir );

                throw new ConfigurationException( message );
            }
        }
    }

    private String constructStoragePath( final Configuration configuration )
        throws ConfigurationException
    {
        final String path =
            configuration.getChild( "storage-directory" ).getValue( "${phoenix.home}/conf/apps" );

        try
        {
            final Object opath = PropertyUtil.resolveProperty( path,
                                                               createConfigurationContext(),
                                                               false );

            if( opath instanceof String )
            {
                return FileUtil.normalize( ( String ) opath );
            }
            else
            {
                final String message = REZ.getString( "config.error.nonstring",
                                                      opath.getClass().getName() );

                throw new ConfigurationException( message );
            }
        }
        catch( PropertyException e )
        {
            final String message = REZ.getString( "config.error.missingproperty",
                                                  configuration.getLocation() );

            throw new ConfigurationException( message, e );
        }
    }

    public void initialize() throws Exception
    {
        final SystemManager systemManager =
            ( SystemManager ) this.m_serviceManager.lookup( SystemManager.ROLE );
        final SystemManager context =
            systemManager.getSubContext( null, "component" ).getSubContext( "ConfigurationManager",
                                                                            "persistent" );

        context.register(
            "PersistentConfigurationRepository",
            this,
            new Class[]{PersistentConfigurationRepositoryMBean.class} );
    }

    public void start()
        throws Exception
    {
        loadConfigurations();
    }

    public void stop()
        throws Exception
    {
        persistConfigurations();
    }

    private void loadConfigurations()
        throws IOException, SAXException, ConfigurationException
    {
        final File[] apps = m_storageDirectory.listFiles( new ConfigurationDirectoryFilter() );

        for( int i = 0; i < apps.length; i++ )
        {
            loadConfigurations( apps[i] );
        }
    }

    private void loadConfigurations( File appPath )
        throws IOException, SAXException, ConfigurationException
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final String app = appPath.getName();
        final File[] blocks = appPath.listFiles( new ConfigurationFileFilter() );

        for( int i = 0; i < blocks.length; i++ )
        {
            final String block =
                blocks[i].getName().substring( 0, blocks[i].getName().indexOf( ".xml" ) );

            this.m_persistedConfigurations.put( new ConfigurationKey( app, block ),
                                                builder.buildFromFile( blocks[i] ) );

            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Loaded persistent configuration [app: " + app
                                   + ", block: " + block + "]" );
        }
    }

    private void persistConfigurations()
        throws SAXException, IOException, ConfigurationException
    {
        for( Iterator i = this.m_persistedConfigurations.entrySet().iterator(); i.hasNext(); )
        {
            final Map.Entry entry = ( Map.Entry ) i.next();
            final ConfigurationKey key = ( ConfigurationKey ) entry.getKey();

            persistConfiguration( key.getApplication(),
                                  key.getBlock(),
                                  ( Configuration ) entry.getValue() );
        }
    }

    private void persistConfiguration( final String application,
                                       final String block,
                                       final Configuration configuration )
        throws SAXException, IOException, ConfigurationException
    {
        final DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        final File directory = new File( this.m_storageDirectory, application );

        FileUtil.forceMkdir( directory );

        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Serializing configuration to disk [app: " + application
                               + ", block: " + block + "]" );

        serializer.setIndent( true );
        serializer.serializeToFile( new File( directory, block + ".xml" ), configuration );
    }

    public void removeConfiguration( String application, String block )
    {
        final String name = application + "." + block;

        m_configurations.remove( name );
    }

    public synchronized void storeConfiguration( final String application,
                                                 final String block,
                                                 final Configuration configuration )
        throws ConfigurationException
    {
        final String name = application + "." + block;

        if( null == configuration )
        {
            m_configurations.remove( name );
        }
        else
        {
            m_configurations.put( name, configuration );
        }
    }

    public synchronized Configuration getConfiguration( final String application,
                                                        final String block )
        throws ConfigurationException
    {
        final String name = application + "." + block;
        final Configuration c = ( Configuration ) m_configurations.get( name );
        final Configuration p = ( Configuration ) m_persistedConfigurations.get(
            new ConfigurationKey( application, block ) );

        if( null == c && p == null )
        {
            final String message = REZ.getString( "config.error.noconfig", block, application );
            throw new ConfigurationException( message );
        }
        else if( null == c )
        {
            return p;
        }
        else if( null == p )
        {
            return c;
        }
        else
        {
            return ConfigurationMerger.merge( p, c );
        }
    }

    public Configuration getPersistentConfiguration( String application, String block )
        throws ConfigurationException
    {
        final Configuration configuration = ( Configuration ) m_persistedConfigurations.get(
            new ConfigurationKey( application, block ) );

        if( null == configuration )
        {
            final String message = REZ.getString( "config.error.noconfig", block, application );
            throw new ConfigurationException( message );
        }

        return configuration;
    }

    public void storePersistentConfiguration( String application,
                                              String block,
                                              Configuration configuration )
        throws ConfigurationException
    {
        final ConfigurationKey key = new ConfigurationKey( application, block );

        if( null == configuration )
        {
            m_persistedConfigurations.remove( key );
        }
        else
        {
            m_persistedConfigurations.put( key, configuration );
        }
    }
}
