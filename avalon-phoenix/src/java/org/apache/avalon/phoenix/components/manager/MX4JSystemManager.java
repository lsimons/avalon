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
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.components.kernel.DefaultKernel;
import org.apache.avalon.phoenix.components.kernel.DefaultKernelMBean;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.DeployerMBean;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.avalon.phoenix.interfaces.EmbeddorMBean;
import org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.KernelMBean;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.avalon.phoenix.interfaces.PackageRepository;
import org.apache.excalibur.baxter.JavaBeanMBean;

/**
 * This component is responsible for managing phoenix instance.
 * This includes managing embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class MX4JSystemManager
    extends AbstractSystemManager
    implements Parameterizable, Serviceable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( MX4JSystemManager.class );

    //private Parameters m_parameters;
    private MBeanServer m_mBeanServer;

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
        // m_parameters = parameters;
    }

    /**
     * Retrieve relevant services needed to deploy.
     *
     * @param serviceManager the ComponentManager
     * @throws ServiceException if an error occurs
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_embeddor = (Embeddor)serviceManager.lookup( Embeddor.ROLE );
        m_kernel = (Kernel)serviceManager.lookup( Kernel.ROLE );
        m_deployer = (Deployer)serviceManager.lookup( Deployer.ROLE );
        m_repository = (ConfigurationRepository)serviceManager.lookup( ConfigurationRepository.ROLE );
        m_logManager = (LogManager)serviceManager.lookup( LogManager.ROLE );
        m_extensionManager = (PackageRepository)serviceManager.lookup( PackageRepository.ROLE );
    }

    public void initialize()
        throws Exception
    {
        m_mBeanServer = MBeanServerFactory.createMBeanServer( "Phoenix" );

        final ObjectName adaptorName = new ObjectName( "Http:name=HttpAdaptor" );
        m_mBeanServer.createMBean( "mx4j.adaptor.http.HttpAdaptor", adaptorName, null );
        m_mBeanServer.setAttribute( adaptorName, new Attribute( "Port", new Integer( 8083 ) ) );

        /**
         // add user names
         m_mBeanServer.invoke(adaptorName, "addAuthorization", new Object[] {"mx4j", "mx4j"}, new String[] {"java.lang.String", "java.lang.String"});

         // use basic authentication
         m_mBeanServer.setAttribute(adaptorName, new Attribute("AuthenticationMethod", "basic"));
         */

        ObjectName processorName = new ObjectName( "Http:name=XSLTProcessor" );
        m_mBeanServer.createMBean( "mx4j.adaptor.http.XSLTProcessor", processorName, null );
        /*
                if( path != null )
                {
                    m_mBeanServer.setAttribute( processorName, new Attribute( "File", path ) );
                }
        */
        m_mBeanServer.setAttribute( processorName, new Attribute( "UseCache", new Boolean( false ) ) );
        /*
                if( pathInJar != null )
                {
                    m_mBeanServer.setAttribute( processorName, new Attribute( "PathInJar", pathInJar ) );
                }
        */

        m_mBeanServer.setAttribute( adaptorName, new Attribute( "ProcessorName", processorName ) );

        // starts the server
        m_mBeanServer.invoke( adaptorName, "start", null, null );

        //TODO: SystemManager itself aswell???
        //FIXME: All this stuff should be done by embeddor and read out of a config file
        register( "Kernel", m_kernel, new Class[]{KernelMBean.class} );
        register( "ExtensionManager", m_extensionManager, new Class[]{ExtensionManagerMBean.class} );
        register( "Embeddor", m_embeddor, new Class[]{EmbeddorMBean.class} );
        register( "Deployer", m_deployer, new Class[]{DeployerMBean.class} );
        register( "LogManager", m_logManager );
        register( "ConfigurationRepository", m_repository );
    }

    public void start()
        throws Exception
    {
    }

    public void stop()
        throws Exception
    {
    }

    public void dispose()
    {
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
}
