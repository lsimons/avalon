/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager;

import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.components.manager.rmiadaptor.RMIAdaptorImpl;

/**
 * This component is responsible for managing phoenix instance.
 * This includes managing embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultManager
    extends AbstractJMXManager
    implements Parameterizable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultManager.class );

    private static final int DEFAULT_REGISTRY_PORT =
        Integer.getInteger( "phoenix.port", 1111 ).intValue();
    private static final int DEFAULT_HTTPADAPTER_PORT =
        Integer.getInteger( "phoenix.adapter.http", 8082 ).intValue();
    private static final String DEFAULT_ADMIN_USER =
        System.getProperty( "phoenix.admin.user", "admin" );
    private static final String DEFAULT_ADMIN_PASSWD =
        System.getProperty( "phoenix.admin.passwd" );

    private Parameters m_parameters;
    private RMIAdaptorImpl m_rmiAdaptor;
    private Registry m_rmiRegistry;

    ///Name Adaptor registered with
    private String m_name;

    private Configuration m_configuration;

    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_parameters = parameters;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        super.initialize();
        final MBeanServer mBeanServer = getMBeanServer();
        m_rmiAdaptor = new RMIAdaptorImpl( mBeanServer );

        try
        {
            final String htmlParserClass =
                m_configuration.getChild( "manager-html-parser" ).
                getValue( null );
            ObjectName parserName = null;
            if( null != htmlParserClass )
            {
                parserName = new ObjectName( "Adaptor:name=htmlParser" );
                System.out.println( "Created HTML Parser " + parserName );
                mBeanServer.createMBean( htmlParserClass, parserName );
            }

            final int port =
                m_configuration.getChild( "manager-adaptor-port" ).
                getValueAsInteger( DEFAULT_HTTPADAPTER_PORT );
            final HtmlAdaptorServer html =
                new HtmlAdaptorServer( port );

            final String adminname =
                m_configuration.getChild( "manager-admin-name" ).
                getValue( DEFAULT_ADMIN_USER );
            final String adminpasswd =
                m_configuration.getChild( "manager-admin-password" ).
                getValue( DEFAULT_ADMIN_PASSWD );
            if( null != adminpasswd )
            {
                final AuthInfo auth = new AuthInfo( adminname, adminpasswd );
                html.addUserAuthenticationInfo( auth );
            }

            final String stringName =
                "Adaptor:name=html,port=" + port;
            final ObjectName name = new ObjectName( stringName );
            System.out.println( "Created HTML Adaptor " + name );
            mBeanServer.registerMBean( html, name );
            if( null != htmlParserClass )
            {
                html.setParser( parserName );
            }
            html.start();
        }
        catch( final Exception e )
        {
            System.out.println( "Could not create the HTML adaptor!!!" );
            e.printStackTrace();
            throw e;
        }
    }

    public void start()
        throws Exception
    {
        final int portp =
            m_parameters.getParameterAsInteger( "manager-registry-port",
                                                DEFAULT_REGISTRY_PORT );
        final int port =
            m_configuration.getChild( "manager-registry-port" ).getValueAsInteger( portp );
        m_name = m_parameters.getParameter( "manager-name", "Phoenix.JMXAdaptor" );

        m_rmiRegistry = LocateRegistry.createRegistry( port );

        //This next line is soooooo insecure - should use some form
        //of secure exporting mechanism
        final Remote exported = UnicastRemoteObject.exportObject( m_rmiAdaptor );
        final Remote stub = RemoteObject.toStub( exported );

        //TODO: should this do a lookup and refuse to lauch
        //if existing server registered???
        m_rmiRegistry.bind( m_name, stub );
    }

    public void stop()
        throws Exception
    {
        m_rmiRegistry.unbind( m_name );
        UnicastRemoteObject.unexportObject( m_rmiAdaptor, true );
        //TODO: How do you shutdown registry???
    }

    public void dispose()
    {
        super.dispose();
        //TODO: Unregister everything here or in embeddor???
        m_rmiAdaptor = null;
    }

    /**
     * Creates a new Manager. The mBeanServer it uses is determined from
     * the Parameters's manager-mBeanServer-class variable.
     */
    protected MBeanServer createMBeanServer()
        throws Exception
    {
        try
        {
            return MBeanServerFactory.createMBeanServer();
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.mbeanserver.create",
                               "MBeanServerFactory" );
            throw new ParameterException( message, e );
        }
    }
}
