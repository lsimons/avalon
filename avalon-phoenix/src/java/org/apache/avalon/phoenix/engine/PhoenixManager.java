/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.avalon.framework.atlantis.AbstractSystemManager;
import org.apache.avalon.framework.atlantis.ManagerException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.jmx.adaptor.RMIAdaptorImpl;
import org.apache.jmx.introspector.DynamicMBeanFactory;

/**
 * This component is responsible for managing phoenix instance.
 * This includes managing embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PhoenixManager
    extends AbstractSystemManager
    implements Parameterizable
{
    private Parameters      m_parameters;
    private MBeanServer     m_mBeanServer;
    private RMIAdaptorImpl  m_rmiAdaptor;
    private Registry        m_rmiRegistry;

    ///Name Adaptor registered with
    private String          m_name;

    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_parameters = parameters;
    }

    public void initialize()
        throws Exception
    {
        m_mBeanServer = createMBeanServer();
        m_rmiAdaptor = new RMIAdaptorImpl( m_mBeanServer );

        //TODO: Register everything here or in embeddor???
    }

    public void start()
        throws Exception
    {
        final int port = m_parameters.getParameterAsInteger( "manager-registry-port", 1111 );
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
        throws Exception
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
            //TODO: actually take some heed of interfaces parameter
            final DynamicMBean mBean = DynamicMBeanFactory.create( object );
            m_mBeanServer.registerMBean( mBean, new ObjectName( name ) );
            return mBean;
        }
        catch( final Exception e )
        {
            final String message = "Unable to export " + name + " as mBean";
            getLogger().error( message, e );
            throw new ManagerException( message, e );
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
            final String message = "Unable to unexport " + name + " as mBean";
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
            throw new ParameterException( "Failed to create MBean Server of class " + className, e );
        }
    }
}
