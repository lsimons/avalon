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

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.property.PropertyException;
import org.apache.avalon.excalibur.property.PropertyUtil;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepositoryMBean;
import org.apache.excalibur.configuration.ConfigurationUtil;
import org.apache.excalibur.configuration.merged.ConfigurationMerger;
import org.apache.excalibur.configuration.merged.ConfigurationSplitter;

import org.xml.sax.SAXException;

/**
 * <p>
 * A ConfigurationRepository that will store partial configurations on disk.
 * </p><p>
 * When a Configuration is retrieved from the repository, the configuration from disk is
 * <i>merged</i> with the configuration from the SAR. This merge is accompilished via
 * {@link ConfigurationMerger#merge}.
 * </p><p>
 * When a Configuration is stored in the repository, if there is no <i>transient</i>, that is,
 * configuration from the SAR, Configuration information, the first store is that. Subsequent
 * calls to storeConfiguration will persist the difference between the <i>transient</i>
 * Configuration and the passed configuration to disk. The differences are computed via
 * {@link ConfigurationSplitter#split}
 * </p>
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @see org.apache.excalibur.configuration.merged.ConfigurationMerger
 * @see org.apache.excalibur.configuration.merged.ConfigurationSplitter
 */
public class FileSystemPersistentConfigurationRepository extends AbstractLogEnabled
    implements ConfigurationRepository, Contextualizable, Configurable, Initializable,
    ConfigurationRepositoryMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( FileSystemPersistentConfigurationRepository.class );

    private final DefaultConfigurationRepository m_persistedConfigurations =
        new DefaultConfigurationRepository();
    private final DefaultConfigurationRepository
        m_transientConfigurations = new DefaultConfigurationRepository();
    private final DefaultConfigurationRepository
        m_mergedConfigurations = new DefaultConfigurationRepository();

    private Context m_context;

    private File m_storageDirectory;

    public void contextualize( Context context )
        throws ContextException
    {
        m_context = context;
    }

    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        this.m_storageDirectory = new File( constructStoragePath( configuration ) );

        try
        {
            FileUtil.forceMkdir( this.m_storageDirectory );
        }
        catch( IOException e )
        {
            final String message = REZ.getString( "config.error.dir.invalid",
                                                  this.m_storageDirectory );

            throw new ConfigurationException( message, e );

        }
    }

    private String constructStoragePath( final Configuration configuration )
        throws ConfigurationException
    {
        final String path =
            configuration.getChild( "storage-directory" ).getValue( "${phoenix.home}/conf/apps" );

        try
        {
            final Object opath = PropertyUtil.resolveProperty( path, m_context, false );

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

    public void initialize()
        throws Exception
    {
        loadConfigurations();
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

            m_persistedConfigurations.storeConfiguration( app,
                                                          block,
                                                          builder.buildFromFile( blocks[i] ) );

            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Loaded persistent configuration [app: " + app
                                   + ", block: " + block + "]" );
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
        throws ConfigurationException
    {
        m_transientConfigurations.removeConfiguration( application, block );
        m_mergedConfigurations.removeConfiguration( application, block );
    }

    public synchronized void storeConfiguration( final String application,
                                                 final String block,
                                                 final Configuration configuration )
        throws ConfigurationException
    {

        if( m_transientConfigurations.hasConfiguration( application, block ) )
        {
            if( !ConfigurationUtil.equals( configuration, getConfiguration( application, block ) ) )
            {
                final Configuration layer =
                    ConfigurationSplitter.split( configuration,
                                                 getConfiguration( application,
                                                                   block,
                                                                   m_transientConfigurations ) );

                m_persistedConfigurations.storeConfiguration( application, block, layer );
                m_mergedConfigurations.removeConfiguration( application, block );

                try
                {
                    persistConfiguration( application, block, layer );
                }
                catch( SAXException e )
                {
                    final String message =
                        REZ.getString( "config.error.persist", application, block );

                    throw new ConfigurationException( message, e );
                }
                catch( IOException e )
                {
                    final String message =
                        REZ.getString( "config.error.persist", application, block );

                    throw new ConfigurationException( message, e );
                }
            }
        }
        else
        {
            m_transientConfigurations.storeConfiguration( application, block, configuration );
        }
    }

    public synchronized Configuration getConfiguration( final String application,
                                                        final String block )
        throws ConfigurationException
    {
        if( m_mergedConfigurations.hasConfiguration( application, block ) )
        {
            return m_mergedConfigurations.getConfiguration( application, block );
        }
        else
        {
            final Configuration configuration = createMergedConfiguration( application, block );

            m_mergedConfigurations.storeConfiguration( application, block, configuration );

            return configuration;
        }
    }

    private Configuration createMergedConfiguration( final String application,
                                                     final String block )
        throws ConfigurationException
    {
        final Configuration t = getConfiguration( application, block, m_transientConfigurations );
        final Configuration p = getConfiguration( application, block, m_persistedConfigurations );

        if( null == t && p == null )
        {
            final String message = REZ.getString( "config.error.noconfig", block, application );

            throw new ConfigurationException( message );
        }
        else if( null == t )
        {
            return p;
        }
        else if( null == p )
        {
            return t;
        }
        else
        {
            return ConfigurationMerger.merge( p, t );
        }
    }

    private Configuration getConfiguration( final String application,
                                            final String block,
                                            final DefaultConfigurationRepository repository )
    {
        if( repository.hasConfiguration( application, block ) )
        {
            try
            {
                return repository.getConfiguration( application, block );
            }
            catch( ConfigurationException e )
            {
                final String message = REZ.getString( "config.error.noconfig", block, application );

                throw new CascadingRuntimeException( message, e );
            }
        }
        else
        {
            return null;
        }
    }

    public boolean hasConfiguration( String application, String block )
    {
        return m_mergedConfigurations.hasConfiguration( application, block )
            || m_transientConfigurations.hasConfiguration( application, block )
            || m_persistedConfigurations.hasConfiguration( application, block );
    }
}
