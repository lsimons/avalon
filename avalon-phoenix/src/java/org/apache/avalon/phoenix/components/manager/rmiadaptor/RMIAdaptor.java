/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager.rmiadaptor;

import java.io.ObjectInputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
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
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;

/**
 * This is the RMI connection representing an MBeanServer. It is identical to
 * the <code>MBeanServer</code> interface, except it throws exceptions related
 * to remote operations.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/25 08:28:09 $
 */
public interface RMIAdaptor
    extends Remote
{
    /**
     * Instantiates an object using the list of all class loaders registered in
     * the MBean server ({@link javax.management.loading.DefaultLoaderRepository
     * Default Loader Repository}). The object's class should have a public
     * constructor. It returns a reference to the newly created object. The
     * newly created object is not registered in the MBean server.
     *
     * @param className The class name of the object to be instantiated.
     * @return The newly instantiated object.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or the <CODE>
     *      java.lang.Exception</CODE> that occurred when trying to invoke the
     *      object's constructor.
     * @exception javax.management.MBeanException The constructor of the object has thrown an
     *      exception
     * @exception java.rmi.RemoteException RMI Exception
     */
    Object instantiate( String className )
        throws ReflectionException, MBeanException, RemoteException;

    /**
     * Instantiates an object using the class Loader specified by its <CODE>
     * ObjectName</CODE> . If the loader name is null, the ClassLoader that
     * loaded the MBean Server will be used. The object's class should have a
     * public constructor. It returns a reference to the newly created object.
     * The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param loaderName The object name of the class loader to be used.
     * @return The newly instantiated object.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or the <CODE>
     *      java.lang.Exception</CODE> that occurred when trying to invoke the
     *      object's constructor.
     * @exception javax.management.MBeanException The constructor of the object has thrown an
     *      exception.
     * @exception javax.management.InstanceNotFoundException The specified class loader is not
     *      registered in the MBaenServer.
     * @exception java.rmi.RemoteException RMI Exception
     */
    Object instantiate( String className, ObjectName loaderName )
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException;

    /**
     * Instantiates an object using the list of all class loaders registered in
     * the MBean server ({@link javax.management.loading.DefaultLoaderRepository
     * Default Loader Repository}). The object's class should have a public
     * constructor. The call returns a reference to the newly created object.
     * The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the constructor to be
     *      invoked.
     * @param signature An array containing the signature of the constructor to
     *      be invoked.
     * @return The newly instantiated object.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or the <CODE>
     *      java.lang.Exception</CODE> that occurred when trying to invoke the
     *      object's constructor.
     * @exception javax.management.MBeanException The constructor of the object has thrown an
     *      exception
     * @exception java.rmi.RemoteException RMI Exception
     */
    Object instantiate( String className, Object params[], String signature[] )
        throws ReflectionException, MBeanException, RemoteException;

    /**
     * Instantiates an object. The class loader to be used is identified by its
     * object name. If the object name of the loader is null, the ClassLoader
     * that loaded the MBean server will be used. The object's class should have
     * a public constructor. The call returns a reference to the newly created
     * object. The newly created object is not registered in the MBean server.
     *
     * @param className The class name of the object to be instantiated.
     * @param params An array containing the parameters of the constructor to be
     *      invoked.
     * @param signature An array containing the signature of the constructor to
     *      be invoked.
     * @param loaderName The object name of the class loader to be used.
     * @return The newly instantiated object.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or the <CODE>
     *      java.lang.Exception</CODE> that occurred when trying to invoke the
     *      object's constructor.
     * @exception javax.management.MBeanException The constructor of the object has thrown an
     *      exception
     * @exception javax.management.InstanceNotFoundException The specified class loader is not
     *      registered in the MBean server.
     * @exception java.rmi.RemoteException RMI Exception
     */
    Object instantiate( String className, ObjectName loaderName, Object params[], String signature[] )
        throws ReflectionException, MBeanException, InstanceNotFoundException, RemoteException;

    /**
     * Instantiates and registers an MBean in the MBean server. The MBean server
     * will use the {@link javax.management.loading.DefaultLoaderRepository
     * Default Loader Repository} to load the class of the MBean. An object name
     * is associated to the MBean. If the object name given is null, the MBean
     * can automatically provide its own name by implementing the {@link
     * javax.management.MBeanRegistration MBeanRegistration} interface. The call
     * returns an <CODE>ObjectInstance</CODE> object representing the newly
     * created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @return An <CODE>ObjectInstance</CODE> , containing the <CODE>ObjectName
     *      </CODE>and the Java class name of the newly instantiated MBean.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or a <CODE><CODE>
     *      java.lang.Exception</CODE></CODE> that occurred when trying to
     *      invoke the MBean's constructor.
     * @exception javax.management.InstanceAlreadyExistsException The MBean is already under the
     *      control of the MBean server.
     * @exception javax.management.MBeanRegistrationException The <CODE>preRegister</CODE> (
     *      <CODE>MBeanRegistration</CODE> interface) method of the MBean has
     *      thrown an exception. The MBean will not be registered.
     * @exception javax.management.MBeanException The constructor of the MBean has thrown an
     *      exception
     * @exception javax.management.NotCompliantMBeanException This class is not a JMX compliant
     *      MBean
     * @exception java.rmi.RemoteException RMI Exception
     */
    ObjectInstance createMBean( String className, ObjectName name )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, RemoteException;

    /**
     * Instantiates and registers an MBean in the MBean server. The class loader
     * to be used is identified by its object name. An object name is associated
     * to the MBean. If the object name of the loader is null, the ClassLoader
     * that loaded the MBean server will be used. If the MBean's object name
     * given is null, the MBean can automatically provide its own name by
     * implementing the {@link javax.management.MBeanRegistration
     * MBeanRegistration} interface. The call returns an <CODE>ObjectInstance
     * </CODE>object representing the newly created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param loaderName The object name of the class loader to be used.
     * @return An <CODE>ObjectInstance</CODE> , containing the <CODE>ObjectName
     *      </CODE>and the Java class name of the newly instantiated MBean.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or a <CODE>
     *      java.lang.Exception</CODE> that occurred when trying to invoke the
     *      MBean's constructor.
     * @exception javax.management.InstanceAlreadyExistsException The MBean is already under the
     *      control of the MBean server.
     * @exception javax.management.MBeanRegistrationException The <CODE>preRegister</CODE> (
     *      <CODE>MBeanRegistration</CODE> interface) method of the MBean has
     *      thrown an exception. The MBean will not be registered.
     * @exception javax.management.MBeanException The constructor of the MBean has thrown an
     *      exception
     * @exception javax.management.NotCompliantMBeanException This class is not a JMX compliant
     *      MBean
     * @exception javax.management.InstanceNotFoundException The specified class loader is not
     *      registered in the MBean server.
     * @exception java.rmi.RemoteException RMI Exception
     */
    ObjectInstance createMBean( String className, ObjectName name, ObjectName loaderName )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException;

    /**
     * Instantiates and registers an MBean in the MBean server. The MBean server
     * will use the {@link javax.management.loading.DefaultLoaderRepository
     * Default Loader Repository} to load the class of the MBean. An object name
     * is associated to the MBean. If the object name given is null, the MBean
     * can automatically provide its own name by implementing the {@link
     * javax.management.MBeanRegistration MBeanRegistration} interface. The call
     * returns an <CODE>ObjectInstance</CODE> object representing the newly
     * created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param params An array containing the parameters of the constructor to be
     *      invoked.
     * @param signature An array containing the signature of the constructor to
     *      be invoked.
     * @return An <CODE>ObjectInstance</CODE> , containing the <CODE>ObjectName
     *      </CODE>and the Java class name of the newly instantiated MBean.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or a <CODE>
     *      java.lang.Exception</CODE> that occurred when trying to invoke the
     *      MBean's constructor.
     * @exception javax.management.InstanceAlreadyExistsException The MBean is already under the
     *      control of the MBean server.
     * @exception javax.management.MBeanRegistrationException The <CODE>preRegister</CODE> (
     *      <CODE>MBeanRegistration</CODE> interface) method of the MBean has
     *      thrown an exception. The MBean will not be registered.
     * @exception javax.management.MBeanException The constructor of the MBean has thrown an
     *      exception
     * @exception javax.management.NotCompliantMBeanException If the mBean is invalid form
     * @exception java.rmi.RemoteException RMI Exception
     */
    ObjectInstance createMBean( String className, ObjectName name, Object params[], String signature[] )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, RemoteException;

    /**
     * Instantiates and registers an MBean in the MBean server. The class loader
     * to be used is identified by its object name. An object name is associated
     * to the MBean. If the object name of the loader is not specified, the
     * ClassLoader that loaded the MBean server will be used. If the MBean
     * object name given is null, the MBean can automatically provide its own
     * name by implementing the {@link javax.management.MBeanRegistration
     * MBeanRegistration} interface. The call returns an <CODE>ObjectInstance
     * </CODE>object representing the newly created MBean.
     *
     * @param className The class name of the MBean to be instantiated.
     * @param name The object name of the MBean. May be null.
     * @param params An array containing the parameters of the constructor to be
     *      invoked.
     * @param signature An array containing the signature of the constructor to
     *      be invoked.
     * @param loaderName The object name of the class loader to be used.
     * @return An <CODE>ObjectInstance</CODE> , containing the <CODE>ObjectName
     *      </CODE>and the Java class name of the newly instantiated MBean.
     * @exception javax.management.ReflectionException Wraps a <CODE>
     *      java.lang.ClassNotFoundException</CODE> or a <CODE>
     *      java.lang.Exception</CODE> that occurred when trying to invoke the
     *      MBean's constructor.
     * @exception javax.management.InstanceAlreadyExistsException The MBean is already under the
     *      control of the MBean server.
     * @exception javax.management.MBeanRegistrationException The <CODE>preRegister</CODE> (
     *      <CODE>MBeanRegistration</CODE> interface) method of the MBean has
     *      thrown an exception. The MBean will not be registered.
     * @exception javax.management.MBeanException The constructor of the MBean has thrown an
     *      exception
     * @exception javax.management.InstanceNotFoundException The specified class loader is not
     *      registered in the MBean server.
     * @exception javax.management.NotCompliantMBeanException DOC: Insert Description of
     *      Exception
     * @exception java.rmi.RemoteException RMI Exception
     */
    ObjectInstance createMBean( String className,
                                ObjectName name,
                                ObjectName loaderName,
                                Object params[],
                                String signature[] )
        throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException,
        MBeanException, NotCompliantMBeanException, InstanceNotFoundException, RemoteException;

    /**
     * Registers a pre-existing object as an MBean with the MBean server. If the
     * object name given is null, the MBean may automatically provide its own
     * name by implementing the {@link javax.management.MBeanRegistration
     * MBeanRegistration} interface. The call returns an <CODE>ObjectInstance
     * </CODE>object representing the registered MBean.
     *
     * @param object The MBean to be registered as an MBean.
     * @param name The object name of the MBean. May be null.
     * @return The <CODE>ObjectInstance</CODE> for the MBean that has been
     *      registered.
     * @exception javax.management.InstanceAlreadyExistsException The MBean is already under the
     *      control of the MBean server.
     * @exception javax.management.MBeanRegistrationException The <CODE>preRegister</CODE> (
     *      <CODE>MBeanRegistration</CODE> interface) method of the MBean has
     *      thrown an exception. The MBean will not be registered.
     * @exception javax.management.NotCompliantMBeanException This object is not a JMX compliant
     *      MBean
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    ObjectInstance registerMBean( Object object, ObjectName name )
        throws InstanceAlreadyExistsException, MBeanRegistrationException,
        NotCompliantMBeanException, RemoteException;

    /**
     * De-registers an MBean from the MBean server. The MBean is identified by
     * its object name. Once the method has been invoked, the MBean may no
     * longer be accessed by its object name.
     *
     * @param name The object name of the MBean to be de-registered.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception javax.management.MBeanRegistrationException The preDeregister (( <CODE>
     *      MBeanRegistration</CODE> interface) method of the MBean has thrown
     *      an exception.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    void unregisterMBean( ObjectName name )
        throws InstanceNotFoundException, MBeanRegistrationException, RemoteException;

    /**
     * Gets the <CODE>ObjectInstance</CODE> for a given MBean registered with
     * the MBean server.
     *
     * @param name The object name of the MBean.
     * @return The <CODE>ObjectInstance</CODE> associated to the MBean specified
     *      by <VAR>name</VAR> .
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    ObjectInstance getObjectInstance( ObjectName name )
        throws InstanceNotFoundException, RemoteException;

    /**
     * Gets MBeans controlled by the MBean server. This method allows any of the
     * following to be obtained: All MBeans, a set of MBeans specified by
     * pattern matching on the <CODE>ObjectName</CODE> and/or a Query
     * expression, a specific MBean. When the object name is null or no domain
     * and key properties are specified, all objects are to be selected (and
     * filtered if a query is specified). It returns the set of <CODE>
     * ObjectInstance</CODE> objects (containing the <CODE>ObjectName</CODE> and
     * the Java Class name) for the selected MBeans.
     *
     * @param name The object name pattern identifying the MBeans to be
     *      retrieved. If null or no domain and key properties are specified,
     *      all the MBeans registered will be retrieved.
     * @param query The query expression to be applied for selecting MBeans. If
     *      null no query expression will be applied for selecting MBeans.
     * @return A set containing the <CODE>ObjectInstance</CODE> objects for the
     *      selected MBeans. If no MBean satisfies the query an empty list is
     *      returned.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    Set queryMBeans( ObjectName name, QueryExp query )
        throws RemoteException;

    /**
     * Gets the names of MBeans controlled by the MBean server. This method
     * enables any of the following to be obtained: The names of all MBeans, the
     * names of a set of MBeans specified by pattern matching on the <CODE>
     * ObjectName</CODE> and/or a Query expression, a specific MBean name
     * (equivalent to testing whether an MBean is registered). When the object
     * name is null or no domain and key properties are specified, all objects
     * are selected (and filtered if a query is specified). It returns the set
     * of ObjectNames for the MBeans selected.
     *
     * @param name The object name pattern identifying the MBeans to be
     *      retrieved. If null or no domain and key properties are specified,
     *      all the MBeans registered will be retrieved.
     * @param query The query expression to be applied for selecting MBeans. If
     *      null no query expression will be applied for selecting MBeans.
     * @return A set containing the ObjectNames for the MBeans selected. If no
     *      MBean satisfies the query, an empty list is returned.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    Set queryNames( ObjectName name, QueryExp query )
        throws RemoteException;

    /**
     * Checks whether an MBean, identified by its object name, is already
     * registered with the MBean server.
     *
     * @param name The object name of the MBean to be checked.
     * @return True if the MBean is already registered in the MBean server,
     *      false otherwise.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    boolean isRegistered( ObjectName name )
        throws RemoteException;

    /**
     * Returns the number of MBeans registered in the MBean server.
     *
     * @return The MBeanCount value
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    Integer getMBeanCount()
        throws RemoteException;

    /**
     * Gets the value of a specific attribute of a named MBean. The MBean is
     * identified by its object name.
     *
     * @param name The object name of the MBean from which the attribute is to
     *      be retrieved.
     * @param attribute A String specifying the name of the attribute to be
     *      retrieved.
     * @return The value of the retrieved attribute.
     * @exception javax.management.AttributeNotFoundException The attribute specified is not
     *      accessible in the MBean.
     * @exception javax.management.MBeanException Wraps an exception thrown by the MBean's
     *      getter.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception javax.management.ReflectionException Wraps a <CODE>java.lang.Exception</CODE>
     *      thrown when trying to invoke the setter.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    Object getAttribute( ObjectName name, String attribute )
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException,
        ReflectionException, RemoteException;

    /**
     * Enables the values of several attributes of a named MBean. The MBean is
     * identified by its object name.
     *
     * @param name The object name of the MBean from which the attributes are
     *      retrieved.
     * @param attributes A list of the attributes to be retrieved.
     * @return The list of the retrieved attributes.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception javax.management.ReflectionException An exception occurred when trying to
     *      invoke the getAttributes method of a Dynamic MBean.
     * @exception java.rmi.RemoteException RMI Exception
     */
    AttributeList getAttributes( ObjectName name, String[] attributes )
        throws InstanceNotFoundException, ReflectionException, RemoteException;

    /**
     * Sets the value of a specific attribute of a named MBean. The MBean is
     * identified by its object name.
     *
     * @param name The name of the MBean within which the attribute is to be
     *      set.
     * @param attribute The identification of the attribute to be set and the
     *      value it is to be set to.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception javax.management.AttributeNotFoundException The attribute specified is not
     *      accessible in the MBean.
     * @exception javax.management.InvalidAttributeValueException The value specified for the
     *      attribute is not valid.
     * @exception javax.management.MBeanException Wraps an exception thrown by the MBean's
     *      setter.
     * @exception javax.management.ReflectionException Wraps a <CODE>java.lang.Exception</CODE>
     *      thrown when trying to invoke the setter.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    void setAttribute( ObjectName name, Attribute attribute )
        throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException,
        MBeanException, ReflectionException, RemoteException;

    /**
     * Sets the values of several attributes of a named MBean. The MBean is
     * identified by its object name.
     *
     * @param name The object name of the MBean within which the attributes are
     *      to be set.
     * @param attributes A list of attributes: The identification of the
     *      attributes to be set and the values they are to be set to.
     * @return The list of attributes that were set, with their new values.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception javax.management.ReflectionException An exception occurred when trying to
     *      invoke the getAttributes method of a Dynamic MBean.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    AttributeList setAttributes( ObjectName name, AttributeList attributes )
        throws InstanceNotFoundException, ReflectionException, RemoteException;

    /**
     * Invokes an operation on an MBean.
     *
     * @param name The object name of the MBean on which the method is to be
     *      invoked.
     * @param operationName The name of the operation to be invoked.
     * @param params An array containing the parameters to be set when the
     *      operation is invoked
     * @param signature An array containing the signature of the operation. The
     *      class objects will be loaded using the same class loader as the one
     *      used for loading the MBean on which the operation was invoked.
     * @return The object returned by the operation, which represents the result
     *      ofinvoking the operation on the MBean specified.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception javax.management.MBeanException Wraps an exception thrown by the MBean's
     *      invoked method.
     * @exception javax.management.ReflectionException Wraps a <CODE>java.lang.Exception</CODE>
     *      thrown while trying to invoke the method.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    Object invoke( ObjectName name, String operationName, Object params[], String signature[] )
        throws InstanceNotFoundException, MBeanException, ReflectionException, RemoteException;

    /**
     * Returns the default domain used for naming the MBean. The default domain
     * name is used as the domain part in the ObjectName of MBeans if no domain
     * is specified by the user.
     *
     * @return The DefaultDomain value
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    String getDefaultDomain()
        throws RemoteException;

    /**
     * Enables to add a listener to a registered MBean.
     *
     * @param name The name of the MBean on which the listener should be added.
     * @param listener The listener object which will handle the notifications
     *      emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no filtering will be
     *      performed before handling notifications.
     * @param handback The context to be sent to the listener when a
     *      notification is emitted.
     * @exception javax.management.InstanceNotFoundException The MBean name provided does not
     *      match any of the registered MBeans.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
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
     * @param listener The object name of the listener which will handle the
     *      notifications emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no filtering will be
     *      performed before handling notifications.
     * @param handback The context to be sent to the listener when a
     *      notification is emitted.
     * @exception javax.management.InstanceNotFoundException The MBean name of the notification
     *      listener or of the notification broadcaster does not match any of
     *      the registered MBeans.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    void addNotificationListener( ObjectName name,
                                  ObjectName listener,
                                  NotificationFilter filter,
                                  Object handback )
        throws InstanceNotFoundException, RemoteException;

    /**
     * Enables to remove a listener from a registered MBean.
     *
     * @param name The name of the MBean on which the listener should be
     *      removed.
     * @param listener The listener object which will handle the notifications
     *      emitted by the registered MBean. This method will remove all the
     *      information related to this listener.
     * @exception javax.management.InstanceNotFoundException The MBean name provided does not
     *      match any of the registered MBeans.
     * @exception javax.management.ListenerNotFoundException The listener is not registered in
     *      the MBean.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    void removeNotificationListener( ObjectName name, NotificationListener listener )
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;

    /**
     * Enables to remove a listener from a registered MBean.
     *
     * @param name The name of the MBean on which the listener should be
     *      removed.
     * @param listener The object name of the listener which will handle the
     *      notifications emitted by the registered MBean. This method will
     *      remove all the information related to this listener.
     * @exception javax.management.InstanceNotFoundException The MBean name provided does not
     *      match any of the registered MBeans.
     * @exception javax.management.ListenerNotFoundException The listener is not registered in
     *      the MBean.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    void removeNotificationListener( ObjectName name, ObjectName listener )
        throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;

    /**
     * This method discovers the attributes and operations that an MBean exposes
     * for management.
     *
     * @param name The name of the MBean to analyze
     * @return An instance of <CODE>MBeanInfo</CODE> allowing the retrieval of
     *      all attributes and operations of this MBean.
     * @exception javax.management.IntrospectionException An exception occurs during
     *      introspection.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not found.
     * @exception javax.management.ReflectionException An exception occurred when trying to
     *      invoke the getMBeanInfo of a Dynamic MBean.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    MBeanInfo getMBeanInfo( ObjectName name )
        throws InstanceNotFoundException, IntrospectionException, ReflectionException, RemoteException;

    /**
     * Returns true if the MBean specified is an instance of the specified
     * class, false otherwise.
     *
     * @param name The <CODE>ObjectName</CODE> of the MBean.
     * @param className The name of the class.
     * @return true if the MBean specified is an instance of the specified
     *      class, false otherwise.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not
     *      registered in the MBean server.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    boolean isInstanceOf( ObjectName name, String className )
        throws InstanceNotFoundException, RemoteException;

    /**
     * De-serializes a byte array in the context of the class loader of an
     * MBean.
     *
     * @param name The name of the MBean whose class loader should be used for
     *      the de-serialization.
     * @param data The byte array to be de-sererialized.
     * @return The de-serialized object stream.
     * @exception javax.management.InstanceNotFoundException The MBean specified is not found.
     * @exception javax.management.OperationsException Any of the usual Input/Output related
     *      exceptions.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    ObjectInputStream deserialize( ObjectName name, byte[] data )
        throws InstanceNotFoundException, OperationsException, RemoteException;

    /**
     * De-serializes a byte array in the context of a given MBean class loader.
     * The class loader is the one that loaded the class with name "className".
     *
     * @param data The byte array to be de-sererialized.
     * @param className DOC: Insert Description of Parameter
     * @return The de-serialized object stream.
     * @exception javax.management.OperationsException Any of the usual Input/Output related
     *      exceptions.
     * @exception javax.management.ReflectionException The specified class could not be loaded by
     *      the default loader repository
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    ObjectInputStream deserialize( String className, byte[] data )
        throws OperationsException, ReflectionException, RemoteException;

    /**
     * De-serializes a byte array in the context of a given MBean class loader.
     * The class loader is the one that loaded the class with name "className".
     * The name of the class loader to be used for loading the specified class
     * is specified. If null, the MBean Server's class loader will be used.
     *
     * @param data The byte array to be de-sererialized.
     * @param loaderName The name of the class loader to be used for loading the
     *      specified class. If null, the MBean Server's class loader will be
     *      used.
     * @param className DOC: Insert Description of Parameter
     * @return The de-serialized object stream.
     * @exception javax.management.InstanceNotFoundException The specified class loader MBean is
     *      not found.
     * @exception javax.management.OperationsException Any of the usual Input/Output related
     *      exceptions.
     * @exception javax.management.ReflectionException The specified class could not be loaded by
     *      the specified class loader.
     * @exception java.rmi.RemoteException DOC: Insert Description of Exception
     */
    ObjectInputStream deserialize( String className, ObjectName loaderName, byte[] data )
        throws InstanceNotFoundException, OperationsException, ReflectionException, RemoteException;
}
