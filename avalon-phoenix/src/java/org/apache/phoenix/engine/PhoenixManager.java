/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.atlantis.SystemManager;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.parameters.ParameterException;
import org.apache.avalon.parameters.Parameterizable;
import org.apache.avalon.parameters.Parameters;
import org.apache.jmx.adaptor.RMIAdaptorImpl;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import javax.management.MBeanServer;

/**
 * This component is responsible for managing phoenix instance.
 * This includes managing embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PhoenixManager
    extends AbstractLoggable
    implements SystemManager, Parameterizable
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

    public void init()
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
