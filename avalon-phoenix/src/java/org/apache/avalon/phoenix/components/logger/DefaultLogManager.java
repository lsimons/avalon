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

package org.apache.avalon.phoenix.components.logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.components.util.ResourceUtil;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.realityforge.loggerstore.DOMLog4JLoggerStoreFactory;
import org.realityforge.loggerstore.InitialLoggerStoreFactory;
import org.realityforge.loggerstore.Jdk14LoggerStoreFactory;
import org.realityforge.loggerstore.LogKitLoggerStoreFactory;
import org.realityforge.loggerstore.LoggerStore;
import org.realityforge.loggerstore.LoggerStoreFactory;
import org.realityforge.loggerstore.PropertyLog4JLoggerStoreFactory;
import org.realityforge.configkit.PropertyExpander;
import org.realityforge.configkit.ResolverFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author Peter Donald
 */
public class DefaultLogManager
    extends AbstractLogEnabled
    implements LogManager, Contextualizable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultLogManager.class );

    private final PropertyExpander m_expander = new PropertyExpander();

    private final InitialLoggerStoreFactory m_factory = new InitialLoggerStoreFactory();

    /**
     * Hold the value of phoenix.home
     */
    private File m_phoenixHome;

    public void contextualize( final Context context ) throws ContextException
    {
        m_phoenixHome = (File)context.get( "phoenix.home" );
    }

    private Map createLoggerManagerContext( final Map appContext )
    {
        final HashMap data = new HashMap();
        data.putAll( appContext );
        data.put( "phoenix.home", m_phoenixHome );
        return data;
    }

    /**
     * Create a Logger hierarchy for specified application.
     *
     * @param logs the configuration data for logging
     * @param context the context in which to create loggers
     * @return the Log hierarchy
     * @throws Exception if unable to create Loggers
     */
    public LoggerStore createHierarchy( final Configuration logs,
                                        final File homeDirectory,
                                        final File workDirectory,
                                        final Map context )
        throws Exception
    {
        final Map map = createLoggerManagerContext( context );
        if( null == logs )
        {
            LoggerStore store = null;
            store = scanForLoggerConfig( "SAR-INF/log4j.properties",
                                         PropertyLog4JLoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         map );
            if( null != store )
            {
                return store;
            }
            store = scanForLoggerConfig( "SAR-INF/log4j.xml",
                                         DOMLog4JLoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         map );
            if( null != store )
            {
                return store;
            }
            store = scanForLoggerConfig( "SAR-INF/logging.properties",
                                         Jdk14LoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         map );
            if( null != store )
            {
                return store;
            }
            store = scanForLoggerConfig( "SAR-INF/excalibur-logger.xml",
                                         LogKitLoggerStoreFactory.class.getName(),
                                         homeDirectory,
                                         workDirectory,
                                         map );
            if( null != store )
            {
                return store;
            }

            //TODO: Set up a default LoggerStore at this point
            final String message = "Unable to locate any logging configuration";
            throw new IllegalStateException( message );
        }
        else
        {
            final String version = logs.getAttribute( "version", "1.0" );
            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    REZ.getString( "logger-create",
                                   context.get( BlockContext.APP_NAME ),
                                   version );
                getLogger().debug( message );
            }

            if( version.equals( "1.0" ) )
            {
                final LoggerStoreFactory loggerManager = new SimpleLoggerStoreFactory();
                ContainerUtil.enableLogging( loggerManager, getLogger() );
                final HashMap config = new HashMap();
                config.put( Logger.class.getName(), getLogger() );
                config.put( Context.class.getName(), new DefaultContext( map ) );
                config.put( Configuration.class.getName(), logs );
                return loggerManager.createLoggerStore( config );
            }
            else if( version.equals( "1.1" ) )
            {
                final LoggerStoreFactory loggerManager = new LogKitLoggerStoreFactory();
                ContainerUtil.enableLogging( loggerManager, getLogger() );
                final HashMap config = new HashMap();
                config.put( Logger.class.getName(), getLogger() );
                config.put( Context.class.getName(), new DefaultContext( map ) );
                config.put( Configuration.class.getName(), logs );
                return loggerManager.createLoggerStore( config );
            }
            else if( version.equals( "log4j" ) )
            {
                final LoggerStoreFactory loggerManager = new DOMLog4JLoggerStoreFactory();
                ContainerUtil.enableLogging( loggerManager, getLogger() );
                final HashMap config = new HashMap();
                final Element element = buildLog4JConfiguration( logs );
                m_expander.expandValues( element, map );
                config.put( Element.class.getName(), element );
                return loggerManager.createLoggerStore( config );
            }
            else
            {
                final String message =
                    "Unknown logger version '" + version + "' in environment.xml";
                throw new IllegalStateException( message );
            }
        }
    }

    private Element buildLog4JConfiguration( final Configuration logs )
    {
        final Element element = ConfigurationUtil.toElement( logs );
        final Document document = element.getOwnerDocument();
        final Element newElement = document.createElement( "log4j:configuration" );
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Node node = childNodes.item( i );
            final Node newNode = node.cloneNode( true );
            newElement.appendChild( newNode );
        }

        document.appendChild( newElement );
        return newElement;
    }

    private LoggerStore scanForLoggerConfig( final String location,
                                             final String classname,
                                             final File homeDirectory,
                                             final File workDirectory,
                                             final Map context )
        throws Exception
    {
        final boolean isPropertiesFile = location.endsWith( "properties" );
        final File file =
            ResourceUtil.getFileForResource( location,
                                             homeDirectory,
                                             workDirectory );
        LoggerStore store = null;
        if( null != file )
        {
            final HashMap config = new HashMap();
            if( isPropertiesFile )
            {
                final Properties properties = new Properties();
                properties.load( file.toURL().openStream() );
                final Properties newProperties =
                    m_expander.expandValues( properties, context );
                config.put( Properties.class.getName(), newProperties );
            }
            //TODO: Remove next line as it is an ugly hack!
            else if( !location.equals( "SAR-INF/excalibur-logger.xml" ) )
            {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();

                //TODO: Need to set up config files for entity resolver
                final EntityResolver resolver =
                    ResolverFactory.createResolver( getClass().getClassLoader() );
                builder.setEntityResolver( resolver );
                final Document document = builder.parse( file );
                final Element element = document.getDocumentElement();
                m_expander.expandValues( element, context );
                config.put( Element.class.getName(), element );
            }
            else
            {
                config.put( LoggerStoreFactory.URL_LOCATION, file.toURL() );
            }
            config.put( InitialLoggerStoreFactory.INITIAL_FACTORY, classname );
            store = m_factory.createLoggerStore( config );
        }
        return store;
    }
}
