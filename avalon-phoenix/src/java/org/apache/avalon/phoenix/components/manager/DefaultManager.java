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
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.components.kernel.DefaultKernel;
import org.apache.avalon.phoenix.components.kernel.DefaultKernelMBean;
import org.apache.avalon.phoenix.components.manager.rmiadaptor.RMIAdaptorImpl;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.excalibur.baxter.JavaBeanMBean;

/**
 * This component is responsible for managing phoenix instance.
 * This includes managing embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultManager
    extends AbstractSystemManager
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
    private MBeanServer m_mBeanServer;
    private RMIAdaptorImpl m_rmiAdaptor;
    private Registry m_rmiRegistry;

    ///Name Adaptor registered with
    private String m_name;

    private String m_domain = "Phoenix";

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
        m_mBeanServer = createMBeanServer();
        m_rmiAdaptor = new RMIAdaptorImpl( m_mBeanServer );

        try
        {
            final int port = m_configuration.getChild( "manager-adaptor-port" ).getValueAsInteger( DEFAULT_HTTPADAPTER_PORT );
            final HtmlAdaptorServer html =
                new HtmlAdaptorServer( port );

            final String adminname = m_configuration.getChild( "manager-admin-name" ).getValue( DEFAULT_ADMIN_USER );
            final String adminpasswd = m_configuration.getChild( "manager-admin-password" ).getValue( DEFAULT_ADMIN_PASSWD );
            if( null != adminpasswd )
            {
                final AuthInfo auth = new AuthInfo( adminname, adminpasswd );
                html.addUserAuthenticationInfo( auth );
            }

            final String stringName =
                "Adaptor:name=html,port=" + port;
            final ObjectName name = new ObjectName( stringName );
            System.out.println( "Created HTML Adaptor " + name );
            m_mBeanServer.registerMBean( html, name );
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
            m_parameters.getParameterAsInteger( "manager-registry-port", DEFAULT_REGISTRY_PORT );
        final int port =
            m_configuration.getChild( "manager-registry-port" ).getValueAsInteger( portp );
        m_name = m_parameters.getParameter( "manager-name", "Phoenix.JMXAdaptor" );

        m_rmiRegistry = LocateRegistry.createRegistry( port );

        //This next line is soooooo insecure - should use some form
        //of secure exporting mechanism
        final Remote exported = UnicastRemoteObject.exportObject( m_rmiAdaptor );
        final Remote stub = RemoteObject.toStub( exported );

        //TODO: should this do a lookup and refuse to lauch if existing server registered???
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
        //TODO: Unregister everything here or in embeddor???
        m_rmiAdaptor = null;
        m_mBeanServer = null;
    }

    /**
     * Export the object to the particular management medium using
     * the supplied object and interfaces.
     * This needs to be implemented by subclasses.
     *
     * @param name the name of object
     * @param object the object
     * @param interfaces the interfaces
     * @return the exported object
     * @throws ManagerException if an error occurs
     */
    protected Object export( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        try
        {
            Object mBean = null;
            if( null != interfaces )
            {
                mBean = new JavaBeanMBean( object, interfaces );
            }
            else
            {
                mBean = createMBean( object );
            }

            final ObjectName objectName = createObjectName( name );
            m_mBeanServer.registerMBean( mBean, objectName );
            return mBean;
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.export.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    private ObjectName createObjectName( final String name ) throws MalformedObjectNameException
    {
        return new ObjectName( m_domain + ":name=" + name );
    }

    /**
     * Create a MBean for specified object.
     * The following policy is used top create the MBean...
     *
     * @param object the object to create MBean for
     * @return the MBean to be exported
     * @throws ManagerException if an error occurs
     */
    private Object createMBean( final Object object )
        throws ManagerException
    {
        //HACK: ugly Testing hack!!
        if( object instanceof DefaultKernel )
        {
            return new DefaultKernelMBean( (DefaultKernel)object );
        }
        else
        {
            return new JavaBeanMBean( object );
        }
    }

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws ManagerException if an error occurs
     */
    protected void unexport( final String name,
                             final Object exportedObject )
        throws ManagerException
    {
        try
        {
            m_mBeanServer.unregisterMBean( createObjectName( name ) );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.unexport.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @throws ManagerException if verification fails
     */
    protected void verifyInterface( final Class clazz )
        throws ManagerException
    {
        //TODO: check it extends all right things and that it
        //has all the right return types etc. Blocks must have
        //interfaces extending Service (or Manageable)
    }

    /**
     * Creates a new Manager. The mBeanServer it uses is determined from
     * the Parameters's manager-mBeanServer-class variable.
     */
    private MBeanServer createMBeanServer()
        throws Exception
    {
        final String className =
            m_parameters.getParameter( "manager-mBeanServer-class",
                                       "com.sun.management.jmx.MBeanServerImpl" );

        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            return (MBeanServer)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.mbeanserver.create",
                               className );
            throw new ParameterException( message, e );
        }
    }
}
