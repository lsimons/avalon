/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.adaptor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.io.ObjectInputStream;

import javax.management.*;

/**
 * This is the RMI connection representing an MBeanServer. It is
 * identical to the <code>MBeanServer</code> interface, except
 * it throws exceptions related to remote operations.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class RMIAdaptorImpl extends UnicastRemoteObject implements RMIAdaptor
{
    private MBeanServer server;

    public RMIAdaptorImpl(MBeanServer server) throws RemoteException {
        super();
        this.server = server;
    }


    /////////////////////////////////
    /// RMIADAPTOR IMPLEMENTATION ///
    /////////////////////////////////
    public Object instantiate(String className)
        throws ReflectionException, MBeanException, RemoteException {
            return server.instantiate(className);
        }
    public Object instantiate(String className, ObjectName loaderName)
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException {
            return server.instantiate(className, loaderName);
        }
    public Object instantiate(String className, Object params[], String[] signature)
        throws ReflectionException, MBeanException, RemoteException {
            return server.instantiate(className, params, signature);
        }
    public Object instantiate(  String className,
                                ObjectName loaderName,
                                Object params[],
                                String[] signature)
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException {
            return server.instantiate(className,loaderName, params, signature);
        }

    public ObjectInstance createMBean(String className, ObjectName name)
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, RemoteException {
            return server.createMBean(className,name);
        }
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName)
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException {
            return server.createMBean(className, name, loaderName);
        }
    public ObjectInstance createMBean(  String className,
                                        ObjectName name,
                                        Object[] params,
                                        String[] signature)
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, RemoteException {
            return server.createMBean(className, name, params, signature);
        }
    public ObjectInstance createMBean(  String className,
                                        ObjectName name,
                                        ObjectName loaderName,
                                        Object[] params,
                                        String[] signature)
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException {
            return server.createMBean(className, name, loaderName, params, signature);
        }

    public ObjectInstance registerMBean(Object object, ObjectName name)
        throws InstanceAlreadyExistsException, MBeanRegistrationException,
        NotCompliantMBeanException, RemoteException {
            return server.registerMBean(object, name);
        }

    public void unregisterMBean(ObjectName name)
        throws InstanceNotFoundException, MBeanRegistrationException, RemoteException {
            server.unregisterMBean(name);
        }

    public ObjectInstance getObjectInstance(ObjectName name)
        throws InstanceNotFoundException, RemoteException {
            return server.getObjectInstance(name);
        }

    public Set queryMBeans(ObjectName name, QueryExp query) throws RemoteException {
        return server.queryMBeans(name, query);
    }
    public Set queryNames(ObjectName name, QueryExp query) throws RemoteException {
        return server.queryMBeans(name, query);
    }

    public boolean isRegistered(ObjectName name) throws RemoteException {
        return server.isRegistered(name);
    }

    public Integer getMBeanCount() throws RemoteException {
        return server.getMBeanCount();
    }
    public Object getAttribute(ObjectName name, String attribute)
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException,
        ReflectionException, RemoteException {
            return server.getAttribute(name, attribute);
        }
    public AttributeList getAttributes(ObjectName name, String[] attributes)
        throws InstanceNotFoundException, ReflectionException {
            return server.getAttributes(name, attributes);
        }
    public void setAttribute(ObjectName name, Attribute attribute)
        throws InstanceNotFoundException, AttributeNotFoundException,
        InvalidAttributeValueException, MBeanException, ReflectionException, RemoteException {
            server.setAttribute(name, attribute);
        }
    public AttributeList setAttributes(ObjectName name, AttributeList attributes)
        throws InstanceNotFoundException, ReflectionException, RemoteException {
            return server.setAttributes(name, attributes);
        }
    public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature)
        throws InstanceNotFoundException, MBeanException, ReflectionException, RemoteException {
            return server.invoke(name, operationName, params, signature);
        }
    public String getDefaultDomain() throws RemoteException {
        return server.getDefaultDomain();
    }
    public void addNotificationListener(    ObjectName name,
                                            NotificationListener listener,
                                            NotificationFilter filter,
                                            Object handback)
        throws InstanceNotFoundException, RemoteException {
            server.addNotificationListener(name, listener, filter, handback);
        }
    public void addNotificationListener(    ObjectName name,
                                            ObjectName listener,
                                            NotificationFilter filter,
                                            Object handback)
        throws InstanceNotFoundException, RemoteException {
            server.addNotificationListener(name, listener, filter, handback);
        }
    public void removeNotificationListener(ObjectName name, NotificationListener listener)
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
            server.removeNotificationListener(name, listener);
        }
    public void removeNotificationListener(ObjectName name, ObjectName listener)
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
            server.removeNotificationListener(name, listener);
        }
    public MBeanInfo getMBeanInfo(ObjectName name)
        throws InstanceNotFoundException, IntrospectionException, ReflectionException,
        RemoteException {
            return server.getMBeanInfo(name);
        }
    public boolean isInstanceOf(ObjectName name, String className)
        throws InstanceNotFoundException, RemoteException {
            return server.isInstanceOf(name,className);
        }
    public ObjectInputStream deserialize(ObjectName name, byte[] data)
        throws InstanceNotFoundException, OperationsException, RemoteException {
            return server.deserialize(name, data);
        }
    public ObjectInputStream deserialize(String className, byte[] data)
        throws OperationsException, ReflectionException, RemoteException {
            return server.deserialize(className, data);
        }
    public ObjectInputStream deserialize(String className, ObjectName loaderName, byte[] data)
        throws InstanceNotFoundException, OperationsException, ReflectionException,
        RemoteException {
            return server.deserialize(className, loaderName, data);
        }
}