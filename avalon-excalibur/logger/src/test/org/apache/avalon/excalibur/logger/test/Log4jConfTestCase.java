/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger.test;

import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.avalon.excalibur.logger.Log4jConfLoggerManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/10/28 00:36:03 $
 */
public class Log4jConfTestCase
    extends TestCase
{
    public Log4jConfTestCase( final String name )
    {
        super( name );
    }

    public void testWrite()
        throws Exception
    {
        final Log4jConfLoggerManager manager = getManager( "log4j.xml" );
        final Logger logger = manager.getDefaultLogger();
        logger.warn( "Some random message" );
    }

    private Log4jConfLoggerManager getManager( final String resourceName )
        throws Exception
    {
        final Configuration configuration = loadConfiguration( resourceName );
        final Log4jConfLoggerManager manager = new Log4jConfLoggerManager();
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
