/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager;

import java.io.File;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.excalibur.converter.Converter;
import org.apache.excalibur.converter.lib.SimpleMasterConverter;

/**
 * This component is responsible for managing phoenix instance.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class ExtendedMX4JSystemManager
    extends AbstractJMXManager
    implements Contextualizable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ExtendedMX4JSystemManager.class );

    private static final String DEFAULT_NAMING_FACTORY =
        "com.sun.jndi.rmi.registry.RegistryContextFactory";

    private File m_homeDir;
    private Configuration m_configuration;
    private Map m_jmxMBeans;
    private Map m_jmxMBeanConfs;

    public void contextualize( Context context )
        throws ContextException
    {
        m_homeDir = (File)context.get( "phoenix.home" );
    }

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

        m_jmxMBeans = new HashMap();
        m_jmxMBeanConfs = new HashMap();
        final Configuration[] mBeanConfs = m_configuration.getChildren( "mbean" );
        for ( int i = 0; i < mBeanConfs.length; i++ )
        {
            createMBean( mBeanConfs[ i ] );
        }
    }

    public void dispose()
    {
        final MBeanServer mBeanServer = getMBeanServer();

        final Iterator mBeanNames = m_jmxMBeans.keySet().iterator();
        while ( mBeanNames.hasNext() )
        {
            destroyMBean( (String)mBeanNames.next() );
        }

        super.dispose();
    }

    private void createMBean( final Configuration mBeanConf )
        throws Exception
    {
        final MBeanServer mBeanServer = getMBeanServer();
        final Converter valueConverter = new SimpleMasterConverter();

        final ObjectName objectName = new ObjectName( mBeanConf.getAttribute( "name" ) );

        try
        {
            mBeanServer.createMBean( mBeanConf.getAttribute( "class" ), objectName, null );

            //set attributes
            final Configuration[] attributes = mBeanConf.getChildren( "attribute" );
            for ( int i = 0; i < attributes.length; i++ )
            {
                final Configuration attribute = attributes[ i ];
                final String name = attribute.getAttribute( "name" );
                final String type = attribute.getAttribute( "type" );
                Object value = attribute.getValue( null );
                if ( null != value )
                {
                    final Class valueClass = Class.forName( type );
                    value = valueConverter.convert( valueClass, value, null );
                }
                mBeanServer.setAttribute( objectName, new Attribute( name, value ) );
            }

            //set dependent attributes
            final Configuration[] uses = mBeanConf.getChildren( "use" );
            for ( int i = 0; i < uses.length; i++ )
            {
                final Configuration use = uses[ i ];
                final String name = use.getAttribute( "name" );
                final String value = use.getValue();
                mBeanServer.setAttribute( objectName, new Attribute( name, new ObjectName( value ) ) );
            }

            //invoke startup operations
            final Configuration[] invokes = mBeanConf.getChild( "startup", true ).getChildren( "invoke" );
            invokeOperations( mBeanServer, objectName, invokes );

            m_jmxMBeans.put( objectName.getCanonicalName(), objectName );
            m_jmxMBeanConfs.put( objectName.getCanonicalName(), mBeanConf );
        }
        catch ( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.initialize", objectName );
            getLogger().error( message , e );
        }
    }

    private void destroyMBean( final String name )
    {
        final MBeanServer mBeanServer = getMBeanServer();

        final ObjectName objectName = (ObjectName)m_jmxMBeans.get( name );
        final Configuration mBeanConf = (Configuration)m_jmxMBeanConfs.get( name );
        try
        {
            final Configuration[] invokes = mBeanConf.getChild( "shutdown", true ).getChildren( "invoke" );
            invokeOperations( mBeanServer, objectName, invokes );
        }
        catch ( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.dispose", objectName );
            getLogger().error( message , e );
        }
    }

    private void invokeOperations( final MBeanServer mBeanServer,
                                   final ObjectName objectName,
                                   final Configuration[] invokes )
        throws Exception
    {
        for ( int i = 0; i < invokes.length; i++ )
        {
            final Configuration invoke = invokes[ i ];
            invokeOperation( mBeanServer, objectName, invoke );
        }
    }

    private void invokeOperation( final MBeanServer mBeanServer,
                                  final ObjectName objectName,
                                  final Configuration invoke )
        throws Exception
    {
        final Converter valueConverter = new SimpleMasterConverter();

        final String operationName = invoke.getAttribute( "name" );
        final Configuration[] paramConfs = invoke.getChildren( "parameter" );
        final String[] types = new String[ paramConfs.length ];
        final Object[] values = new Object[ paramConfs.length ];
        for ( int i = 0; i < paramConfs.length; i++ )
        {
            final String type = paramConfs[ i ].getAttribute( "type" );
            Object value = paramConfs[ i ].getValue( null );
            if ( null != value )
            {
                final Class valueClass = Class.forName( type );
                value = valueConverter.convert( valueClass, value, null );
            }
            types[ i ] = type;
            values[ i ] = value;
        }
        mBeanServer.invoke( objectName, operationName, values, types );
    }

    protected MBeanServer createMBeanServer()
        throws Exception
    {
        MX4JLoggerAdapter.setLogger(getLogger());
        mx4j.log.Log.redirectTo(new MX4JLoggerAdapter());
        return MBeanServerFactory.createMBeanServer( "Phoenix" );
    }
}
