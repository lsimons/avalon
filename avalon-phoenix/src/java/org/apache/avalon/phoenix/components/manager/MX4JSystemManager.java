/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This component is responsible for managing phoenix instance.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 */
public class MX4JSystemManager
    extends AbstractJMXManager implements Configurable
{

    private static final int DEFAULT_HTTPADAPTER_PORT =
        Integer.getInteger( "phoenix.adapter.http", 8082 ).intValue();

    private int m_port;

    public void initialize()
        throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = getMBeanServer();

        final ObjectName adaptorName = new ObjectName( "Http:name=HttpAdaptor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.HttpAdaptor", adaptorName, null );
        mBeanServer.setAttribute( adaptorName, new Attribute( "Port", new Integer(m_port) ) );

        /**
         // add user names
         m_mBeanServer.invoke(adaptorName,
         "addAuthorization",
         new Object[] {"mx4j", "mx4j"},
         new String[] {"java.lang.String", "java.lang.String"});

         // use basic authentication
         m_mBeanServer.setAttribute(adaptorName, new Attribute("AuthenticationMethod", "basic"));
         */

        ObjectName processorName = new ObjectName( "Http:name=XSLTProcessor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.XSLTProcessor", processorName, null );
        /*
                if( path != null )
                {
                    m_mBeanServer.setAttribute( processorName, new Attribute( "File", path ) );
                }
        */
        final Attribute useCache =
            new Attribute( "UseCache", new Boolean( false ) );
        mBeanServer.setAttribute( processorName, useCache );
        /*
                if( pathInJar != null )
                {
                    m_mBeanServer.setAttribute( processorName,
                    new Attribute( "PathInJar", pathInJar ) );
                }
        */

        mBeanServer.setAttribute( adaptorName, new Attribute( "ProcessorName", processorName ) );

        // starts the server
        mBeanServer.invoke( adaptorName, "start", null, null );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getChild( "manager-adaptor-port" ).
                getValueAsInteger( DEFAULT_HTTPADAPTER_PORT );
    }

    protected MBeanServer createMBeanServer()
        throws Exception
    {
        return MBeanServerFactory.createMBeanServer( "Phoenix" );
    }
}
