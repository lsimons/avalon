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
        final Configuration[] mBeanConfs = m_configuration.getChildren( "mbean" );
        for ( int i = 0; i < mBeanConfs.length; i++ )
        {
            initializeMBean( mBeanConfs[ i ] );
        }
    }

    public void dispose()
    {
        final MBeanServer mBeanServer = getMBeanServer();

        final Iterator mBeanNames = m_jmxMBeans.values().iterator();
        while ( mBeanNames.hasNext() )
        {
            final ObjectName mBeanName = (ObjectName)mBeanNames.next();
            try
            {
                //stop mbean.
                mBeanServer.invoke( mBeanName, "stop", null, null );
            }
            catch ( final Exception e )
            {
                final String message = REZ.getString( "jmxmanager.error.jmxmbean.dispose" );
                getLogger().error( message , e );
            }
        }

        super.dispose();
    }

    private void initializeMBean( final Configuration mBeanConf )
        throws Exception
    {
        final MBeanServer mBeanServer = getMBeanServer();
        final Converter valueConverter = new SimpleMasterConverter();

        final ObjectName mBeanName = new ObjectName( mBeanConf.getAttribute( "name" ) );

        try
        {
            mBeanServer.createMBean( mBeanConf.getAttribute( "class" ), mBeanName, null );

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
                mBeanServer.setAttribute( mBeanName, new Attribute( name, value ) );
            }

            //set dependent attributes
            final Configuration[] uses = mBeanConf.getChildren( "use" );
            for ( int i = 0; i < uses.length; i++ )
            {
                final Configuration use = uses[ i ];
                final String name = use.getAttribute( "name" );
                final String value = use.getValue();
                mBeanServer.setAttribute( mBeanName, new Attribute( name, new ObjectName( value ) ) );
            }

            //invoke operations
            final Configuration[] invokes = mBeanConf.getChildren( "invoke" );
            for ( int i = 0; i < invokes.length; i++ )
            {
                final Configuration invoke = invokes[ i ];
                final Configuration[] paramConfs = invoke.getChildren( "parameter" );

                final String operationName = invoke.getAttribute( "name" );
                final String[] types = new String[ paramConfs.length ];
                final Object[] values = new Object[ paramConfs.length ];
                for ( int j = 0; j < paramConfs.length; j++ )
                {
                    types[ j ] = paramConfs[ j ].getAttribute( "type" );
                    values[ j ] = paramConfs[ j ].getValue( null );
                    if ( null != values[ j ] )
                    {
                        final Class valueClass = Class.forName( types[ j ] );
                        values[ j ] = valueConverter.convert( valueClass, values[ j ], null );
                    }
                }
                mBeanServer.invoke( mBeanName, operationName, values, types );
            }

            //start mbean
            mBeanServer.invoke( mBeanName, "start", null, null );

            m_jmxMBeans.put( mBeanName.getCanonicalName(), mBeanName );
        }
        catch ( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.initialize" );
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
