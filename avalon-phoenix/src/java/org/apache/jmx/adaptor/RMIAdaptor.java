/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.adaptor;

import java.io.ObjectInputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
import javax.management.*;

/**
 * This is the RMI connection representing an MBeanServer. It is
 * identical to the <code>MBeanServer</code> interface, except
 * it throws exceptions related to remote operations.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface RMIAdaptor 
    extends Remote
{
    /**
     * Instantiates an object using the list of all class loaders registered
     * in the MBean server ({@link javax.management.loading.DefaultLoaderRepository Default Loader Repository}).
     * The object's class should have a public constructor.
     * It returns a reference to the newly created object.
     * The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the object to be instantiated.
     *
     * @return The newly instantiated object.
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or the <CODE>java.lang.Exception</CODE> that
     * occurred when trying to invoke the object's constructor.
     * @exception MBeanException The constructor of the object has thrown an exception
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: The className passed in parameter is null.
     *
     */
    Object instantiate( String className ) 
        throws ReflectionException, MBeanException, RemoteException;

    /**
     * Instantiates an object using the class Loader specified by its <CODE>ObjectName</CODE>.
     * If the loader name is null, the ClassLoader that loaded the MBean Server will be used.
     * The object's class should have a public constructor.
     * It returns a reference to the newly created object.
     * The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param loaderName The object name of the class loader to be used.
     *
     * @return The newly instantiated object.
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or the <CODE>java.lang.Exception</CODE> that
     * occurred when trying to invoke the object's constructor.
     * @exception MBeanException The constructor of the object has thrown an exception.
     * @exception InstanceNotFoundException The specified class loader is not registered in the MBaenServer.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: The className passed in parameter is null.
     *
     */
    Object instantiate( String className, ObjectName loaderName )
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException;

    /**
     * Instantiates an object using the list of all class loaders registered
     * in the MBean server ({@link javax.management.loading.DefaultLoaderRepository Default Loader Repository}).
     * The object's class should have a public constructor.
     * The call returns a reference to the newly created object.
     * The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the constructor to be invoked.
     * @param signature An array containing the signature of the constructor to be invoked.
     *
     * @return The newly instantiated object.
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or the <CODE>java.lang.Exception</CODE> that
     * occurred when trying to invoke the object's constructor.
     * @exception MBeanException The constructor of the object has thrown an exception
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: The className passed in parameter is null.
     *
     */
    Object instantiate( String className, Object params[], String signature[] )
        throws ReflectionException, MBeanException, RemoteException;

    /**
     * Instantiates an object. The class loader to be used is identified by its object
     * name. If the object name of the loader is null, the ClassLoader that loaded the MBean server will be used.
     * The object's class should have a public constructor.
     * The call returns a reference to the newly created object.
     * The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the constructor to be invoked.
     * @param signature An array containing the signature of the constructor to be invoked.
     * @param loaderName The object name of the class loader to be used.
     *
     * @return The newly instantiated object.
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or the <CODE>java.lang.Exception</CODE> that
     * occurred when trying to invoke the object's constructor.
     * @exception MBeanException The constructor of the object has thrown an exception
     * @exception InstanceNotFoundException The specified class loader is not registered in the MBean server.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: The className passed in parameter is null.
     *
     */
    Object instantiate( String className, ObjectName loaderName, Object params[], String signature[] )
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException;

    /**
     * Instantiates and registers an MBean in the MBean server.
     * The MBean server will use the {@link javax.management.loading.DefaultLoaderRepository Default Loader Repository}
     * to load the class of the MBean.
     * An object name is associated to the MBean.
     * If the object name given is null, the MBean can automatically provide its
     * own name by implementing the {@link javax.management.MBeanRegistration MBeanRegistration} interface.
     * The call returns an <CODE>ObjectInstance</CODE> object representing the newly created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     *
     * @return  An <CODE>ObjectInstance</CODE>, containing the <CODE>ObjectName</CODE> and the Java class name
     * of the newly instantiated MBean.
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or a <CODE><CODE>java.lang.Exception</CODE></CODE> that occurred
     * when trying to invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already under the control of the MBean server.
     * @exception MBeanRegistrationException The <CODE>preRegister</CODE> (<CODE>MBeanRegistration</CODE> interface) method of the MBean
     * has thrown an exception. The MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has thrown an exception
     * @exception NotCompliantMBeanException This class is not a JMX compliant MBean
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>:
     * The className passed in parameter is null, the <CODE>ObjectName</CODE> passed in parameter contains a pattern or no <CODE>ObjectName</CODE> is specified
     * for the MBean.
     *
     */
    ObjectInstance createMBean( String className, ObjectName name )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, 
               MBeanException, NotCompliantMBeanException, RemoteException;

    /**
     * Instantiates and registers an MBean in the MBean server.
     * The class loader to be used is identified by its object  name. An object name is associated to the MBean.
     * If the object name  of the loader is null, the ClassLoader that loaded the MBean server will be used.
     * If the MBean's object name given is null, the MBean can automatically provide its
     * own name by implementing the {@link javax.management.MBeanRegistration MBeanRegistration} interface.
     * The call returns an <CODE>ObjectInstance</CODE> object representing the newly created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param loaderName The object name of the class loader to be used.
     *
     * @return  An <CODE>ObjectInstance</CODE>, containing the <CODE>ObjectName</CODE> and the Java class name
     * of the newly instantiated MBean.
     *
     * @exception ReflectionException  Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or a <CODE>java.lang.Exception</CODE> that occurred
     * when trying to invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already under the control of the MBean server.
     * @exception MBeanRegistrationException The <CODE>preRegister</CODE> (<CODE>MBeanRegistration</CODE>  interface) method of the MBean
     * has thrown an exception. The MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has thrown an exception
     * @exception NotCompliantMBeanException This class is not a JMX compliant MBean
     * @exception InstanceNotFoundException The specified class loader is not registered in the MBean server.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: The className passed in parameter is null,
     * the <CODE>ObjectName</CODE> passed in parameter contains a pattern or no <CODE>ObjectName</CODE> is specified for the MBean.
     */
    ObjectInstance createMBean( String className, ObjectName name, ObjectName loaderName )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, 
               MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException;


    /**
     * Instantiates and registers an MBean in the MBean server.
     * The MBean server will use the {@link javax.management.loading.DefaultLoaderRepository 
     * Default Loader Repository} to load the class of the MBean.
     * An object name is associated to the MBean.
     * If the object name given is null, the MBean can automatically provide its
     * own name by implementing the {@link javax.management.MBeanRegistration MBeanRegistration} interface.
     * The call returns an <CODE>ObjectInstance</CODE> object representing the newly created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param params An array containing the parameters of the constructor to be invoked.
     * @param signature An array containing the signature of the constructor to be invoked.
     *
     * @return An <CODE>ObjectInstance</CODE>, containing the <CODE>ObjectName</CODE> 
     *         and the Java class name of the newly instantiated MBean.
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> 
     * or a <CODE>java.lang.Exception</CODE> that occurred
     * when trying to invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already under the control of the MBean server.
     * @exception MBeanRegistrationException The <CODE>preRegister</CODE> 
     * (<CODE>MBeanRegistration</CODE>  interface) method of the MBean
     * has thrown an exception. The MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has thrown an exception
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The className passed in parameter is null, the <CODE>ObjectName</CODE> passed in 
     * parameter contains a pattern or no <CODE>ObjectName</CODE> is specified for the MBean.
     *
     */
    ObjectInstance createMBean( String className, ObjectName name, Object params[], String signature[] )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, 
               MBeanException, NotCompliantMBeanException, RemoteException;

    /**
     * Instantiates and registers an MBean in the MBean server.
     * The class loader to be used is identified by its object
     * name. An object name is associated to the MBean. If the object name
     * of the loader is not specified, the ClassLoader that loaded the MBean server will be used.
     * If  the MBean object name given is null, the MBean can automatically provide its
     * own name by implementing the {@link javax.management.MBeanRegistration MBeanRegistration} interface.
     * The call returns an <CODE>ObjectInstance</CODE> object representing the newly created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param params An array containing the parameters of the constructor to be invoked.
     * @param signature An array containing the signature of the constructor to be invoked.
     * @param loaderName The object name of the class loader to be used.
     *
     * @return  An <CODE>ObjectInstance</CODE>, containing the <CODE>ObjectName</CODE> and 
     * the Java class name of the newly instantiated MBean.
     *
     * @exception ReflectionException Wraps a <CODE>java.lang.ClassNotFoundException</CODE> or 
     * a <CODE>java.lang.Exception</CODE> that occurred when trying to invoke the MBean's constructor.
     * @exception InstanceAlreadyExistsException The MBean is already under the control 
     * of the MBean server.
     * @exception MBeanRegistrationException The <CODE>preRegister</CODE> 
     * (<CODE>MBeanRegistration</CODE>  interface) method of the MBean has thrown an 
     * exception. The MBean will not be registered.
     * @exception MBeanException The constructor of the MBean has thrown an exception
     * @exception InstanceNotFoundException The specified class loader is not registered 
     * in the MBean server.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The className passed in parameter is null, the <CODE>ObjectName</CODE> passed in 
     * parameter contains a pattern or no <CODE>ObjectName</CODE> is specified for the MBean.
     */
    ObjectInstance createMBean( String className, 
                                ObjectName name, 
                                ObjectName loaderName, 
                                Object params[], 
                                String signature[] )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, 
               MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException;

    /**
     * Registers a pre-existing object as an MBean with the MBean server. If the object name given is
     * null, the MBean may automatically provide its own name by implementing the
     * {@link javax.management.MBeanRegistration MBeanRegistration}  interface.
     * The call returns an <CODE>ObjectInstance</CODE> object representing the registered MBean.
     *
     * @param object The  MBean to be registered as an MBean.
     * @param name The object name of the MBean. May be null.
     *
     * @return  The <CODE>ObjectInstance</CODE> for the MBean that has been registered.
     *
     * @exception InstanceAlreadyExistsException The MBean is already under the control of the MBean server.
     * @exception MBeanRegistrationException The <CODE>preRegister</CODE> 
     * (<CODE>MBeanRegistration</CODE>  interface) method of the MBean
     * has thrown an exception. The MBean will not be registered.
     * @exception NotCompliantMBeanException This object is not a JMX compliant MBean
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The object passed in parameter is null or no object name is specified.
     */
    ObjectInstance registerMBean( Object object, ObjectName name )
        throws InstanceAlreadyExistsException, MBeanRegistrationException,
               NotCompliantMBeanException, RemoteException;

    /**
     * De-registers an MBean from the MBean server. The MBean is identified by
     * its object name. Once the method has been invoked, the MBean may
     * no longer be accessed by its object name.
     *
     * @param name The object name of the MBean to be de-registered.
     *
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     * @exception MBeanRegistrationException The preDeregister 
     * ((<CODE>MBeanRegistration</CODE> interface) method of the MBean has thrown an exception.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The object name in parameter is null or the MBean you are when trying to de-register 
     * is the {@link javax.management.MBeanServerDelegate MBeanServerDelegate} MBean.
     */
    void unregisterMBean( ObjectName name )
        throws InstanceNotFoundException, MBeanRegistrationException, RemoteException;

    /**
     * Gets the <CODE>ObjectInstance</CODE> for a given MBean registered with the MBean server.
     *
     * @param name The object name of the MBean.
     *
     * @return The <CODE>ObjectInstance</CODE> associated to the MBean specified by <VAR>name</VAR>.
     *
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     */
    ObjectInstance getObjectInstance( ObjectName name ) 
        throws InstanceNotFoundException, RemoteException;

    /**
     * Gets MBeans controlled by the MBean server. This method allows any
     * of the following to be obtained: All MBeans, a set of MBeans specified
     * by pattern matching on the <CODE>ObjectName</CODE> and/or a Query expression, a
     * specific MBean. When the object name is null or no domain and key properties are specified,
     * all objects are to be selected (and filtered if a query is specified). It returns the
     * set of <CODE>ObjectInstance</CODE> objects (containing the <CODE>ObjectName</CODE> 
     * and the Java Class name) for the selected MBeans.
     *
     * @param name The object name pattern identifying the MBeans to be retrieved. If
     * null or no domain and key properties are specified, all the MBeans registered will be retrieved.
     * @param query The query expression to be applied for selecting MBeans. If null
     * no query expression will be applied for selecting MBeans.
     *
     * @return  A set containing the <CODE>ObjectInstance</CODE> objects for the selected MBeans.
     * If no MBean satisfies the query an empty list is returned.
     */
    Set queryMBeans( ObjectName name, QueryExp query ) 
        throws RemoteException;

    /**
     * Gets the names of MBeans controlled by the MBean server. This method
     * enables any of the following to be obtained: The names of all MBeans,
     * the names of a set of MBeans specified by pattern matching on the
     * <CODE>ObjectName</CODE> and/or a Query expression, a specific MBean name (equivalent to
     * testing whether an MBean is registered). When the object name is
     * null or no domain and key properties are specified, all objects are selected (and filtered if a
     * query is specified). It returns the set of ObjectNames for the MBeans
     * selected.
     *
     * @param name The object name pattern identifying the MBeans to be retrieved. If
     * null or no domain and key properties are specified, all the MBeans registered will be retrieved.
     * @param query The query expression to be applied for selecting MBeans. If null
     * no query expression will be applied for selecting MBeans.
     *
     * @return  A set containing the ObjectNames for the MBeans selected.
     * If no MBean satisfies the query, an empty list is returned.
     *
     */
    Set queryNames( ObjectName name, QueryExp query ) 
        throws RemoteException;

    /**
     * Checks whether an MBean, identified by its object name, is already registered
     * with the MBean server.
     *
     * @param name The object name of the MBean to be checked.
     *
     * @return  True if the MBean is already registered in the MBean server, false otherwise.
     *
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The object name in parameter is null.
     */
    boolean isRegistered( ObjectName name ) 
        throws RemoteException;

    /**
     * Returns the number of MBeans registered in the MBean server.
     */
    Integer getMBeanCount() 
        throws RemoteException;

    /**
     * Gets the value of a specific attribute of a named MBean. The MBean
     * is identified by its object name.
     *
     * @param name The object name of the MBean from which the attribute is to be retrieved.
     * @param attribute A String specifying the name of the attribute to be
     * retrieved.
     *
     * @return  The value of the retrieved attribute.
     *
     * @exception AttributeNotFoundException The attribute specified is not accessible in the MBean.
     * @exception MBeanException  Wraps an exception thrown by the MBean's getter.
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     * @exception ReflectionException  Wraps a <CODE>java.lang.Exception</CODE> thrown 
     * when trying to invoke the setter.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The object name in parameter is null or the attribute in parameter is null.
     */
    Object getAttribute( ObjectName name, String attribute ) 
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, 
               ReflectionException, RemoteException;

    /**
     * Enables the values of several attributes of a named MBean. The MBean
     * is identified by its object name.
     *
     * @param name The object name of the MBean from which the attributes are
     * retrieved.
     * @param attributes A list of the attributes to be retrieved.
     *
     * @return The list of the retrieved attributes.
     *
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     * @exception ReflectionException An exception occurred when trying to invoke the 
     * getAttributes method of a Dynamic MBean.
     * @exception RuntimeOperationsException Wrap a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The object name in parameter is null or attributes in parameter is null.
     */
    AttributeList getAttributes( ObjectName name, String[] attributes )
        throws InstanceNotFoundException, ReflectionException;

    /**
     * Sets the value of a specific attribute of a named MBean. The MBean
     * is identified by its object name.
     *
     * @param name The name of the MBean within which the attribute is to be set.
     * @param attribute The identification of the attribute to be set and the value it is to be set to.
     *
     * @return  The value of the attribute that has been set.
     *
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     * @exception AttributeNotFoundException The attribute specified is not accessible in the MBean.
     * @exception InvalidAttributeValueException The value specified for the attribute is not valid.
     * @exception MBeanException Wraps an exception thrown by the MBean's setter.
     * @exception ReflectionException  Wraps a <CODE>java.lang.Exception</CODE> thrown when 
     * trying to invoke the setter.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The object name in parameter is null or the attribute in parameter is null.
     */
    void setAttribute( ObjectName name, Attribute attribute ) 
        throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, 
               MBeanException, ReflectionException, RemoteException;

    /**
     * Sets the values of several attributes of a named MBean. The MBean is
     * identified by its object name.
     *
     * @param name The object name of the MBean within which the attributes are to
     * be set.
     * @param attributes A list of attributes: The identification of the
     * attributes to be set and  the values they are to be set to.
     *
     * @return  The list of attributes that were set, with their new values.
     *
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     * @exception ReflectionException An exception occurred when trying to invoke the 
     * getAttributes method of a Dynamic MBean.
     * @exception RuntimeOperationsException Wraps a <CODE>java.lang.IllegalArgumentException</CODE>: 
     * The object name in parameter is null or attributes in parameter is null.
     */
    AttributeList setAttributes( ObjectName name, AttributeList attributes )
        throws InstanceNotFoundException, ReflectionException, RemoteException;

    /**
     * Invokes an operation on an MBean.
     *
     * @param name The object name of the MBean on which the method is to be invoked.
     * @param operationName The name of the operation to be invoked.
     * @param params An array containing the parameters to be set when the operation is
     * invoked
     * @param signature An array containing the signature of the operation. The class objects will
     * be loaded using the same class loader as the one used for loading the MBean on which the operation was invoked.
     *
     * @return  The object returned by the operation, which represents the result ofinvoking the operation on the
     * MBean specified.
     *
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     * @exception MBeanException  Wraps an exception thrown by the MBean's invoked method.
     * @exception ReflectionException  Wraps a <CODE>java.lang.Exception</CODE> thrown 
     * while trying to invoke the method.
     */
    Object invoke( ObjectName name, String operationName, Object params[], String signature[] )
        throws InstanceNotFoundException, MBeanException, ReflectionException, RemoteException;

    /**
     * Returns the default domain used for naming the MBean.
     * The default domain name is used as the domain part in the ObjectName
     * of MBeans if no domain is specified by the user.
     */
    String getDefaultDomain() 
        throws RemoteException;

    /**
     * Enables to add a listener to a registered MBean.
     *
     * @param name The name of the MBean on which the listener should be added.
     * @param listener The listener object which will handle the notifications emitted 
     * by the registered MBean.
     * @param filter The filter object. If filter is null, no filtering will be 
     * performed before handling notifications.
     * @param handback The context to be sent to the listener when a notification is emitted.
     * @exception InstanceNotFoundException The MBean name provided does not match any of 
     * the registered MBeans.
     */
    void addNotificationListener( ObjectName name, 
                                  NotificationListener listener, 
                                  NotificationFilter filter, 
                                  Object handback )
        throws InstanceNotFoundException, RemoteException;

    /**
     * Enables to add a listener to a registered MBean.
     *
     * @param name The name of the MBean on which the listener should be added.
     * @param listener The object name of the listener which will handle the notifications 
     * emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no filtering will be performed before 
     * handling notifications.
     * @param handback The context to be sent to the listener when a notification is emitted.
     *
     * @exception InstanceNotFoundException The MBean name of the notification listener 
     * or of the notification broadcaster does not match any of the registered MBeans.
     */
    void addNotificationListener( ObjectName name, 
                                  ObjectName listener, 
                                  NotificationFilter filter, 
                                  Object handback )
        throws InstanceNotFoundException, RemoteException;

    /**
     * Enables to remove a listener from a registered MBean.
     *
     * @param name The name of the MBean on which the listener should be removed.
     * @param listener The listener object which will handle the notifications emitted by 
     * the registered MBean.
     * This method will remove all the information related to this listener.
     *
     * @exception InstanceNotFoundException The MBean name provided does not match any 
     * of the registered MBeans.
     * @exception ListenerNotFoundException The listener is not registered in the MBean.
     */
    void removeNotificationListener( ObjectName name, NotificationListener listener )
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;

    /**
     * Enables to remove a listener from a registered MBean.
     *
     * @param name The name of the MBean on which the listener should be removed.
     * @param listener The object name of the listener which will handle the 
     * notifications emitted by the registered MBean.
     * This method will remove all the information related to this listener.
     *
     * @exception InstanceNotFoundException The MBean name provided does not match any of 
     * the registered MBeans.
     * @exception ListenerNotFoundException The listener is not registered in the MBean.
     */
    void removeNotificationListener( ObjectName name, ObjectName listener )
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;

    /**
     * This method discovers the attributes and operations that an MBean exposes
     * for management.
     *
     * @param name The name of the MBean to analyze
     *
     * @return  An instance of <CODE>MBeanInfo</CODE> allowing the retrieval of 
     * all attributes and operations of this MBean.
     *
     * @exception IntrospectionException An exception occurs during introspection.
     * @exception InstanceNotFoundException The MBean specified is not found.
     * @exception ReflectionException An exception occurred when trying to invoke 
     * the getMBeanInfo of a Dynamic MBean.
     */
    MBeanInfo getMBeanInfo( ObjectName name ) 
        throws InstanceNotFoundException, IntrospectionException, ReflectionException, RemoteException;

    /** Returns true if the MBean specified is an instance of the specified class, false otherwise.
     *
     * @param name The <CODE>ObjectName</CODE> of the MBean.
     * @param className The name of the class.
     *
     * @return true if the MBean specified is an instance of the specified class, false otherwise.
     *
     * @exception InstanceNotFoundException The MBean specified is not registered in the MBean server.
     */
    boolean isInstanceOf( ObjectName name, String className ) 
        throws InstanceNotFoundException, RemoteException;

    /**
     * De-serializes a byte array in the context of the class loader
     * of an MBean.
     *
     * @param name The name of the MBean whose class loader should be used for the de-serialization.
     * @param data The byte array to be de-sererialized.
     *
     * @return  The de-serialized object stream.
     *
     * @exception InstanceNotFoundException The MBean specified is not found.
     * @exception OperationsException Any of the usual Input/Output related exceptions.
     */
    ObjectInputStream deserialize( ObjectName name, byte[] data )
        throws InstanceNotFoundException, OperationsException, RemoteException;

    /**
     * De-serializes a byte array in the context of a given MBean class loader.
     * The class loader is the one that loaded the class with name "className".
     *
     * @param name The name of the class whose class loader should be used for the de-serialization.
     * @param data The byte array to be de-sererialized.
     *
     * @return  The de-serialized object stream.
     *
     * @exception OperationsException Any of the usual Input/Output related exceptions.
     * @exception ReflectionException The specified class could not be loaded by the 
     * default loader repository
     */
    ObjectInputStream deserialize( String className, byte[] data )
        throws OperationsException, ReflectionException, RemoteException;

    /**
     * De-serializes a byte array in the context of a given MBean class loader.
     * The class loader is the one that loaded the class with name "className".
     * The name of the class loader to be used for loading the specified class is specified.
     * If null, the MBean Server's class loader will be used.
     *
     * @param name The name of the class whose class loader should be used for the de-serialization.
     * @param data The byte array to be de-sererialized.
     * @param loaderName The name of the class loader to be used for loading the specified class.
     * If null, the MBean Server's class loader will be used.
     *
     * @return  The de-serialized object stream.
     *
     * @exception InstanceNotFoundException The specified class loader MBean is not found.
     * @exception OperationsException Any of the usual Input/Output related exceptions.
     * @exception ReflectionException The specified class could not be loaded by the specified class loader.
     */
    ObjectInputStream deserialize( String className, ObjectName loaderName, byte[] data )
        throws InstanceNotFoundException, OperationsException, ReflectionException, RemoteException;
}
