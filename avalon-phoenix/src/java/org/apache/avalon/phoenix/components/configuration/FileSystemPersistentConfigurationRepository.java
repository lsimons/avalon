/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.phoenix.components.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
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
import org.apache.avalon.phoenix.components.configuration.merger.ConfigurationMerger;
import org.apache.avalon.phoenix.components.util.PropertyUtil;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.xml.sax.SAXException;

/**
 * <p>
 * A ConfigurationRepository that will store partial configurations on disk.
 * </p><p>
 * When a Configuration is retrieved from the repository, the configuration from disk is
 * <i>merged</i> with the configuration from the SAR. This merge is accompilished via
 * {@link ConfigurationMerger#merge}.
 * </p>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @see ConfigurationMerger
 */
public class FileSystemPersistentConfigurationRepository
        extends AbstractLogEnabled
        implements ConfigurationRepository, Contextualizable, Configurable, Initializable
{
    private static final Resources REZ =
            ResourceManager.getPackageResources( FileSystemPersistentConfigurationRepository.class );

    private final Map m_persistedConfigurations = new HashMap();

    private Context m_context;

    private File m_storageDirectory;
    private String m_debugPath;
    private DefaultConfigurationSerializer m_serializer;

    public void contextualize( final Context context )
            throws ContextException
    {
        m_context = context;
    }

    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        m_storageDirectory = new File( constructStoragePath( configuration ) );

        try
        {
            FileUtil.forceMkdir( m_storageDirectory );
        }
        catch( IOException e )
        {
            final String message = REZ.getString( "config.error.dir.invalid",
                                                  m_storageDirectory );

            throw new ConfigurationException( message, e );
        }

        m_debugPath = configuration.getChild( "debug-output-path" ).getValue( null );
    }

    public Configuration processConfiguration( final String application,
                                               final String block,
                                               final Configuration configuration )
            throws ConfigurationException
    {
        final Configuration processedConfiguration =
                doProcessConfiguration( application, block, configuration );

        if( null != m_debugPath )
        {
            writeDebugConfiguration( application, block, processedConfiguration );
        }

        return processedConfiguration;
    }

    private Configuration doProcessConfiguration( final String application,
                                                  final String block,
                                                  final Configuration configuration )
            throws ConfigurationException
    {
        final Configuration persistedConfiguration =
                (Configuration)m_persistedConfigurations.get( genKey( application, block ) );

        if( null != persistedConfiguration )
        {
            return ConfigurationMerger.merge( persistedConfiguration, configuration );
        }
        else
        {
            return configuration;
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
                return FileUtil.normalize( (String)opath );
            }
            else
            {
                final String message = REZ.getString( "config.error.nonstring",
                                                      opath.getClass().getName() );

                throw new ConfigurationException( message );
            }
        }
        catch( Exception e )
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

        if( null != m_debugPath )
        {
            FileUtil.forceMkdir( new File( m_debugPath ) );

            m_serializer = new DefaultConfigurationSerializer();
            m_serializer.setIndent( true );
        }
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

    private void loadConfigurations( final File appPath )
            throws IOException, SAXException, ConfigurationException
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final String app = appPath.getName();
        final File[] blocks = appPath.listFiles( new ConfigurationFileFilter() );

        for( int i = 0; i < blocks.length; i++ )
        {
            final String block =
                    blocks[i].getName().substring( 0, blocks[i].getName().indexOf( ".xml" ) );

            m_persistedConfigurations.put( genKey( app, block ),
                                           builder.buildFromFile( blocks[i] ) );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Loaded persistent configuration [app: " + app
                                   + ", block: " + block + "]" );
            }
        }
    }

    private String genKey( final String app, final String block )
    {
        return app + '-' + block;
    }

    private void writeDebugConfiguration( final String application,
                                          final String block,
                                          final Configuration configuration )
    {
        try
        {
            final File temp = File.createTempFile( application + "-" + block + "-",
                                                   ".xml",
                                                   new File( m_debugPath ) );

            m_serializer.serializeToFile( temp, configuration );

            if( getLogger().isDebugEnabled() )
            {
                final String message = "Configuration written at: " + temp;
                getLogger().debug( message );
            }
        }
        catch( final Exception e )
        {
            final String message = "Unable to write debug output";
            getLogger().error( message, e );
        }
    }
}
