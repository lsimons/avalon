/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager.rmiadaptor;

import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import org.apache.avalon.phoenix.components.manager.rmiadaptor.RMIAdaptor;

/**
 * This is the RMI connection representing an MBeanServer. It is
 * identical to the <code>MBeanServer</code> interface, except
 * it throws exceptions related to remote operations.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class RMIAdaptorImpl
    extends RemoteServer
    implements RMIAdaptor
{
    private MBeanServer m_server;

    public RMIAdaptorImpl( final MBeanServer server )
        throws RemoteException
    {
        m_server = server;
    }

    /////////////////////////////////
    /// RMIADAPTOR IMPLEMENTATION ///
    /////////////////////////////////
    public Object instantiate( final String className )
        throws ReflectionException, MBeanException, RemoteException
    {
        return m_server.instantiate( className );
    }

    public Object instantiate( final String className, final ObjectName loaderName )
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException
    {
        return m_server.instantiate( className, loaderName );
    }

    public Object instantiate( final String className,
                               final Object[] params,
                               final String[] signature )
        throws ReflectionException, MBeanException, RemoteException
    {
        return m_server.instantiate( className, params, signature );
    }

    public Object instantiate( final String className,
                               final ObjectName loaderName,
                               final Object[] params,
                               final String[] signature )
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException
    {
        return m_server.instantiate( className, loaderName, params, signature );
    }

    public ObjectInstance createMBean( final String className, final ObjectName name )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, RemoteException
    {
        return m_server.createMBean( className, name );
    }

    public ObjectInstance createMBean( final String className,
                                       final ObjectName name,
                                       final ObjectName loaderName )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException
    {
        return m_server.createMBean( className, name, loaderName );
    }

    public ObjectInstance createMBean( final String className,
                                       final ObjectName name,
                                       final Object[] params,
                                       final String[] signature )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, RemoteException
    {
        return m_server.createMBean( className, name, params, signature );
    }

    public ObjectInstance createMBean( final String className,
                                       final ObjectName name,
                                       final ObjectName loaderName,
                                       final Object[] params,
                                       final String[] signature )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException
    {
        return m_server.createMBean( className, name, loaderName, params, signature );
    }

    public ObjectInstance registerMBean( final Object object, final ObjectName name )
        throws InstanceAlreadyExistsException, MBeanRegistrationException,
        NotCompliantMBeanException, RemoteException
    {
        return m_server.registerMBean( object, name );
    }

    public void unregisterMBean( final ObjectName name )
        throws InstanceNotFoundException, MBeanRegistrationException, RemoteException
    {
        m_server.unregisterMBean( name );
    }

    public ObjectInstance getObjectInstance( final ObjectName name )
        throws InstanceNotFoundException, RemoteException
    {
        return m_server.getObjectInstance( name );
    }

    public Set queryMBeans( final ObjectName name, final QueryExp query )
        throws RemoteException
    {
        return m_server.queryMBeans( name, query );
    }

    public Set queryNames( final ObjectName name, final QueryExp query )
        throws RemoteException
    {
        return m_server.queryMBeans( name, query );
    }

    public boolean isRegistered( final ObjectName name )
        throws RemoteException
    {
        return m_server.isRegistered( name );
    }

    public Integer getMBeanCount()
        throws RemoteException
    {
        return m_server.getMBeanCount();
    }

    public Object getAttribute( final ObjectName name, final String attribute )
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException,
        ReflectionException, RemoteException
    {
        return m_server.getAttribute( name, attribute );
    }

    public AttributeList getAttributes( final ObjectName name, final String[] attributes )
        throws InstanceNotFoundException, ReflectionException, RemoteException
    {
        return m_server.getAttributes( name, attributes );
    }

    public void setAttribute( final ObjectName name, final Attribute attribute )
        throws InstanceNotFoundException, AttributeNotFoundException,
        InvalidAttributeValueException, MBeanException, ReflectionException, RemoteException
    {
        m_server.setAttribute( name, attribute );
    }

    public AttributeList setAttributes( final ObjectName name, final AttributeList attributes )
        throws InstanceNotFoundException, ReflectionException, RemoteException
    {
        return m_server.setAttributes( name, attributes );
    }

    public Object invoke( final ObjectName name,
                          final String operationName,
                          final Object[] params,
                          final String[] signature )
        throws InstanceNotFoundException, MBeanException, ReflectionException, RemoteException
    {
        return m_server.invoke( name, operationName, params, signature );
    }

    public String getDefaultDomain()
        throws RemoteException
    {
        return m_server.getDefaultDomain();
    }

    public void addNotificationListener( final ObjectName name,
                                         final NotificationListener listener,
                                         final NotificationFilter filter,
                                         final Object handback )
        throws InstanceNotFoundException, RemoteException
    {
        m_server.addNotificationListener( name, listener, filter, handback );
    }

    public void addNotificationListener( final ObjectName name,
                                         final ObjectName listener,
                                         final NotificationFilter filter,
                                         final Object handback )
        throws InstanceNotFoundException, RemoteException
    {
        m_server.addNotificationListener( name, listener, filter, handback );
    }

    public void removeNotificationListener( final ObjectName name, final NotificationListener listener )
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException
    {
        m_server.removeNotificationListener( name, listener );
    }

    public void removeNotificationListener( final ObjectName name, final ObjectName listener )
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException
    {
        m_server.removeNotificationListener( name, listener );
    }

    public MBeanInfo getMBeanInfo( final ObjectName name )
        throws InstanceNotFoundException, IntrospectionException, ReflectionException, RemoteException
    {
        return m_server.getMBeanInfo( name );
    }

    public boolean isInstanceOf( final ObjectName name, final String className )
        throws InstanceNotFoundException, RemoteException
    {
        return m_server.isInstanceOf( name, className );
    }

    public ObjectInputStream deserialize( final ObjectName name, final byte[] data )
        throws InstanceNotFoundException, OperationsException, RemoteException
    {
        return m_server.deserialize( name, data );
    }

    public ObjectInputStream deserialize( final String className, final byte[] data )
        throws OperationsException, ReflectionException, RemoteException
    {
        return m_server.deserialize( className, data );
    }

    public ObjectInputStream deserialize( final String className,
                                          final ObjectName loaderName,
                                          final byte[] data )
        throws InstanceNotFoundException, OperationsException, ReflectionException,
        RemoteException
    {
        return m_server.deserialize( className, loaderName, data );
    }
}
