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
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;

/**
 * Repository which persistently stores configuration information on disk
 *
 * THIS IS A WORK IN PROGRESS AND WILL CHANGE BEWARE OF USE BEWAAAARE!
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class FileSystemPersistentConfigurationRepository extends AbstractLogEnabled
    implements ConfigurationRepository, Parameterizable, Configurable, Startable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( FileSystemPersistentConfigurationRepository.class );

    private final HashMap m_configurations = new HashMap();

    private String m_phoenixHome;

    private File m_storageDirectory;

    public void parameterize( final Parameters parameters ) throws ParameterException
    {
        this.m_phoenixHome = parameters.getParameter( "phoenix.home", ".." );
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

    public void start()
        throws Exception
    {
    }

    public void stop()
        throws Exception
    {
        writeJoinedConfigurationsToDisk( joinConfigurations() );
    }

    private Map joinConfigurations()
    {
        final Map joinedConfigurations = new HashMap();

        for( Iterator i = this.m_configurations.entrySet().iterator(); i.hasNext(); )
        {
            final Map.Entry entry = ( Map.Entry ) i.next();
            final ConfigurationKey key = ( ConfigurationKey ) entry.getKey();

            DefaultConfiguration joined =
                ( DefaultConfiguration ) joinedConfigurations.get( key.getApplication() );

            if( null == joined )
            {
                joined = new DefaultConfiguration( key.getApplication(), "-" );

                joinedConfigurations.put( key.getApplication(), joined );
            }

            joined.addChild( ( Configuration ) entry.getValue() );
        }

        return joinedConfigurations;
    }

    private void writeJoinedConfigurationsToDisk( Map joinedConfigurations )
        throws SAXException, IOException, ConfigurationException
    {
        final DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();

        serializer.setIndent( true );

        for( Iterator i = joinedConfigurations.entrySet().iterator(); i.hasNext(); )
        {
            final Map.Entry entry = ( Map.Entry ) i.next();
            final String application = ( String ) entry.getKey();

            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Serializing configuration to disk: " + application );

            serializer.serializeToFile( new File( this.m_storageDirectory, application + ".xml" ),
                                        ( Configuration ) entry.getValue() );
        }
    }

    public synchronized void storeConfiguration( final String application,
                                                 final String block,
                                                 final Configuration configuration )
        throws ConfigurationException
    {
        final ConfigurationKey key = new ConfigurationKey( application, block );

        if( null == configuration )
        {
            //do nothing right now.
        }
        else
        {
            m_configurations.put( key, configuration );
        }
    }

    public synchronized Configuration getConfiguration( final String application,
                                                        final String block )
        throws ConfigurationException
    {
        final ConfigurationKey key = new ConfigurationKey( application, block );
        final Configuration configuration = ( Configuration ) m_configurations.get( key );

        if( null == configuration )
        {
            final String message = REZ.getString( "config.error.noconfig", block, application );
            throw new ConfigurationException( message );
        }

        return configuration;
    }

    private final class ConfigurationKey
    {
        private final String m_application;
        private final String m_block;

        public ConfigurationKey( String application, String block )
        {
            this.m_application = application;
            this.m_block = block;
        }

        public int hashCode()
        {
            return this.getApplication().hashCode() + this.getBlock().hashCode();
        }

        public boolean equals( Object obj )
        {
            if( obj instanceof ConfigurationKey )
            {
                final ConfigurationKey key = ( ConfigurationKey ) obj;

                return this.getApplication().equals( key.getApplication() )
                    && this.getBlock().equals( key.getBlock() );
            }
            else
            {
                return false;
            }
        }

        public String getApplication()
        {
            return m_application;
        }

        public String getBlock()
        {
            return m_block;
        }
    }
}
