/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager;

import com.sun.jdmk.comm.HtmlAdaptorServer;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.components.kernel.DefaultKernel;
import org.apache.avalon.phoenix.components.kernel.DefaultKernelMBean;
import org.apache.avalon.phoenix.interfaces.EmbeddorMBean;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.PackageRepository;
import org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.jmx.adaptor.RMIAdaptorImpl;
import org.apache.jmx.introspector.JavaBeanMBean;

/**
 * This component is responsible for managing phoenix instance.
 * This includes managing embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultManager
    extends AbstractSystemManager
    implements Parameterizable, Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultManager.class );

    private static final int DEFAULT_REGISTRY_PORT =
        Integer.getInteger( "phoenix.port", 1111 ).intValue();

    private Parameters m_parameters;
    private MBeanServer m_mBeanServer;
    private RMIAdaptorImpl m_rmiAdaptor;
    private Registry m_rmiRegistry;

    ///Name Adaptor registered with
    private String m_name;

    private String m_domain = "Phoenix";

    private Embeddor m_embeddor;
    private Deployer m_deployer;
    private LogManager m_logManager;
    private Kernel m_kernel;
    private ConfigurationRepository m_repository;
    private PackageRepository m_extensionManager;

    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_parameters = parameters;
    }

    /**
     * Retrieve relevent services needed to deploy.
     *
     * @param componentManager the ComponentManager
     * @exception ComponentException if an error occurs
     */
    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_embeddor = (Embeddor)componentManager.lookup( Embeddor.ROLE );
        m_kernel = (Kernel)componentManager.lookup( Kernel.ROLE );
        m_deployer = (Deployer)componentManager.lookup( Deployer.ROLE );
        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
        m_logManager = (LogManager)componentManager.lookup( LogManager.ROLE );
        m_extensionManager = (PackageRepository)componentManager.lookup( PackageRepository.ROLE );
    }

    public void initialize()
        throws Exception
    {
        m_mBeanServer = createMBeanServer();
        m_rmiAdaptor = new RMIAdaptorImpl( m_mBeanServer );

        try
        {
            final HtmlAdaptorServer html = new HtmlAdaptorServer();
            final ObjectName name = new ObjectName( "Adaptor:name=html,port=8082" );
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

        //TODO: SystemManager itself aswell???
        //FIXME: All this stuff should be done by embeddor and read out of a config file
        register( "Kernel", m_kernel );
        register( "ExtensionManager", m_extensionManager, new Class[]{ ExtensionManagerMBean.class } );
        register( "Embeddor", m_embeddor, new Class[]{ EmbeddorMBean.class } );
        register( "Deployer", m_deployer, new Class[]{ Deployer.class } );
        register( "LogManager", m_logManager );
        register( "ConfigurationRepository", m_repository );
    }

    public void start()
        throws Exception
    {
        final int port =
            m_parameters.getParameterAsInteger( "manager-registry-port", DEFAULT_REGISTRY_PORT );
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
     * @exception ManagerException if an error occurs
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

            final ObjectName objectName =
                new ObjectName( m_domain + ":name=" + name );
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

    /**
     * Create a MBean for specified object.
     * The following policy is used top create the MBean...
     *
     * @param object the object to create MBean for
     * @return the MBean to be exported
     * @exception ManagerException if an error occurs
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
     * @exception ManagerException if an error occurs
     */
    protected void unexport( final String name,
                             final Object exportedObject )
        throws ManagerException
    {
        try
        {
            m_mBeanServer.unregisterMBean( new ObjectName( name ) );
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
     * @exception ManagerException if verification fails
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
                                       "org.apache.jmx.MBeanServerImpl" );

        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            return (MBeanServer)Class.forName( className ).newInstance();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.mbeanserver.create", className );
            throw new ParameterException( message, e );
        }
    }
}
