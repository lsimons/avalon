/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.logger.test;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.logger.Log4JConfLoggerManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/05/04 11:14:28 $
 */
public class Log4JConfTestCase
    extends TestCase
{
    public Log4JConfTestCase( final String name )
    {
        super( name );
    }

    public void testWrite()
        throws Exception
    {
        final Log4JConfLoggerManager manager = getManager( "log4j.xml" );
        final Logger logger = manager.getDefaultLogger();
        logger.warn( "Some random message" );
    }

    private Log4JConfLoggerManager getManager( final String resourceName )
        throws Exception
    {
        final Configuration configuration = loadConfiguration( resourceName );
        final Log4JConfLoggerManager manager = new Log4JConfLoggerManager();
        ContainerUtil.enableLogging(manager, new ConsoleLogger());
        ContainerUtil.configure( manager, configuration );
        return manager;
    }

    private Configuration loadConfiguration( final String resourceName ) throws SAXException, IOException, ConfigurationException
    {
        final InputStream resource = getResource( resourceName );
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final Configuration configuration = builder.build( resource );
        return configuration;
    }

    private InputStream getResource( final String resourceName )
    {
        final InputStream resource = getClass().getResourceAsStream( resourceName );
        if( null == resource )
        {
            throw new NullPointerException( "resource" );
        }
        return resource;
    }
}
