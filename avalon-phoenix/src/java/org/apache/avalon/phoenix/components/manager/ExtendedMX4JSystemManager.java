/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This component is responsible for managing phoenix instance.
 * Support Flexible jmx helper mbean configuration.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class ExtendedMX4JSystemManager
    extends AbstractJMXManager
    implements Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ExtendedMX4JSystemManager.class );

    private static final String DEFAULT_NAMING_FACTORY =
        "com.sun.jndi.rmi.registry.RegistryContextFactory";

    private Configuration m_configuration;
    private Map m_mBeanScripters;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final String namingFactory =
            configuration.getChild( "rmi-naming-factory" ).getValue( null);
        if ( null != namingFactory )
        {
            getLogger().warn( "Deprecated." );
            System.setProperty( "java.naming.factory.initial", namingFactory );
        }
        else if ( null == System.getProperty( "java.naming.factory.initial" ) )
        {
            System.setProperty( "java.naming.factory.initial", DEFAULT_NAMING_FACTORY  );
        }

        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        super.initialize();

        m_mBeanScripters = new HashMap();
        final Configuration[] scripters =
            m_configuration.getChildren( "mbean" );
        for ( int i = 0; i < scripters.length; i++ )
        {
            createMBeanScripter( scripters[ i ] );
        }
    }

    public void dispose()
    {
        final Iterator scripterNames = m_mBeanScripters.keySet().iterator();
        while ( scripterNames.hasNext() )
        {
            destroyMBeanScripter( (String)scripterNames.next() );
        }
        m_mBeanScripters.clear();
        m_mBeanScripters = null;

        super.dispose();
    }

    private void createMBeanScripter( final Configuration scripterConf )
        throws Exception
    {
        final MBeanScripter scripter =
            new MBeanScripter( getMBeanServer(), scripterConf );
        try
        {
            scripter.startup();

            m_mBeanScripters.put( scripter.getName(), scripter );
        }
        catch ( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.initialize", scripter.getName() );
            getLogger().error( message , e );
            throw e;
        }
    }

    private void destroyMBeanScripter( final String name )
    {
        final MBeanScripter scripter =
            (MBeanScripter)m_mBeanScripters.get( name );
        try
        {
            scripter.shutdown();
        }
        catch ( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.dispose", scripter.getName() );
            getLogger().error( message , e );
        }
    }

    protected MBeanServer createMBeanServer()
        throws Exception
    {
        MX4JLoggerAdapter.setLogger(getLogger());
        mx4j.log.Log.redirectTo(new MX4JLoggerAdapter());
        return MBeanServerFactory.createMBeanServer( "Phoenix" );
    }
}
