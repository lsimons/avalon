/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.jmx.introspector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

/**
 * This is an abstract class that can be used to support creation
 * of <code>DynamicMBean</code> objects. The developer is expected to
 * overide the create...() methods to provide useful elements for their
 * particular purpose.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.6 $ $Date: 2002/03/16 00:11:57 $
 */
public abstract class AbstractMBean
    extends NotificationBroadcasterSupport
    implements DynamicMBean
{
    private final static Object[] EMPTY_OBJ_ARRAY = new Object[ 0 ];

    /**
     * The object represented by this MBean
     */
    private final Object m_object;

    /**
     * The MBeanInfo object.
     */
    private MBeanInfo m_mBeanInfo;

    /**
     * The human readable description of the class.
     */
    private String m_description;

    /**
     * The MBean attribute entrys.
     */
    private AttributeEntry[] m_attributes;

    /**
     * The MBean operation entrys.
     */
    private OperationEntry[] m_operations;

    /**
     * The MBean notification descriptors.
     */
    private MBeanNotificationInfo[] m_notifications;

    /**
     * Constructor that takes object managed and description of object.
     *
     * @param object the object to be managed (may be this)
     */
    protected AbstractMBean( final Object object )
    {
        m_object = object;
    }

    /**
     * Retrieve NotificationInfo objects that this
     * MBean exports.
     *
     * <p>Note to change the notifications supported the user
     * should overide the createNotificationInfos() method.
     *
     * @return the MBeanNotificationInfo objects
     */
    public final MBeanNotificationInfo[] getNotificationInfo()
    {
        return m_notifications;
    }

    /**
     * Provides the exposed attributes and actions of the Dynamic MBean using an
     * MBeanInfo object.
     *
     * @return An instance of <code>MBeanInfo</code> allowing all attributes and
     *      actions exposed by this Dynamic MBean to be retrieved.
     */
    public synchronized MBeanInfo getMBeanInfo()
    {
        return m_mBeanInfo;
    }

    /**
     * Obtains the value of a specific attribute of the Dynamic MBean.
     *
     * @param attribute The name of attribute to retrieve
     * @return The Attribute value
     * @exception AttributeNotFoundException If no such attribute exists
     * @exception MBeanException if an error occurs retrieving attribute
     * @exception ReflectionException if a reflection error occurs
     */
    public Object getAttribute( final String attribute )
        throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        final AttributeEntry entry = getAttributeEntry( attribute );
        final Method method = entry.getReadMethod();

        if( null == method )
        {
            new AttributeNotFoundException( attribute );
        }

        try
        {
            return method.invoke( getObject(), EMPTY_OBJ_ARRAY );
        }
        catch( final InvocationTargetException ite )
        {
            throw new MBeanException( ite );
        }
        catch( final Exception e )
        {
            throw new ReflectionException( e );
        }
    }

    /**
     * Enables the values of several attributes of the Dynamic MBean.
     *
     * @param names the names of attributes to get
     * @return The Attributes retrieved
     */
    public AttributeList getAttributes( final String[] names )
    {
        final AttributeList atributes = new AttributeList();
        for( int i = 0; i < names.length; i++ )
        {
            final String name = names[ i ];
            try
            {
                final Object value = getAttribute( name );
                final Attribute attribute = new Attribute( name, value );
                atributes.add( attribute );
            }
            catch( final Exception e )
            {
                //Not sure what todo here
            }
        }
        return atributes;
    }

    /**
     * Allows an action to be invoked on the Dynamic MBean.
     *
     * @param action The name of the action to be invoked.
     * @param params An array containing the parameters to be set when the
     *      action is invoked.
     * @param signature An array containing the signature of the action. The
     *      class objects will be loaded through the same class loader as the
     *      one used for loading the MBean on which the action is invoked.
     * @return The object returned by the action, which represents the result of
     *      invoking the action on the MBean specified.
     * @exception MBeanException Wraps a <code>java.lang.Exception</code> thrown
     *      by the MBean's invoked method.
     * @exception ReflectionException Wraps a <code>java.lang.Exception</code>
     *      thrown while trying to invoke the method
     */
    public Object invoke( final String action,
                          final Object[] params,
                          final String[] signature )
        throws MBeanException, ReflectionException
    {
        final OperationEntry entry = getOperationEntry( action, signature );
        final Method method = entry.getMethod();
        try
        {
            return method.invoke( getObject(), params );
        }
        catch( final InvocationTargetException ite )
        {
            throw new MBeanException( ite );
        }
        catch( final Exception e )
        {
            throw new ReflectionException( e );
        }
    }

    /**
     * Sets the value of a specific attribute of the Dynamic MBean
     *
     * @param attribute The new Attribute value
     * @exception AttributeNotFoundException if no such attribute exists
     * @exception InvalidAttributeValueException if provided value is invalid
     * @exception MBeanException if setting value raises exception
     * @exception ReflectionException if there is an error aquiring appropriate method
     */
    public void setAttribute( final Attribute attribute )
        throws AttributeNotFoundException, InvalidAttributeValueException,
        MBeanException, ReflectionException
    {
        final AttributeEntry entry = getAttributeEntry( attribute.getName() );
        final Method method = entry.getWriteMethod();

        if( null == method )
        {
            new AttributeNotFoundException( attribute.getName() );
        }

        try
        {
            method.invoke( getObject(), new Object[]{attribute.getValue()} );
        }
        catch( final IllegalArgumentException iae )
        {
            throw new InvalidAttributeValueException();
        }
        catch( final InvocationTargetException ite )
        {
            throw new MBeanException( ite );
        }
        catch( final Exception e )
        {
            throw new ReflectionException( e );
        }
    }

    /**
     * Sets the values of several attributes of the Dynamic MBean.
     *
     * @param input The new Attributes value
     * @return The attributes actually set
     */
    public AttributeList setAttributes( final AttributeList input )
    {
        final AttributeList atributes = new AttributeList();
        final int size = input.size();
        for( int i = 0; i < size; i++ )
        {
            try
            {
                final Attribute attribute = (Attribute)input.get( i );
                setAttribute( attribute );
                atributes.add( attribute );
            }
            catch( final Exception e )
            {
                //Not sure what todo here
            }
        }
        return atributes;
    }

    /**
     * Retrieve the underlying object that is being managed.
     *
     * @return the managed object
     */
    protected final Object getObject()
    {
        return m_object;
    }

    /**
     * Method that developer calls in subclass when they need to prepare the
     * object for use. This creates all the operation, attribute and
     * notification arrays and MBeanInfo by calling the respective create
     * methods.
     */
    protected synchronized void initialize()
    {
        m_description = createDescription();
        m_operations = createOperations();
        m_attributes = createAttributes();
        m_notifications = createNotificationInfos();
        m_mBeanInfo = createMBeanInfo();
    }

    /**
     * Utility method called by initialize to create description.
     * A developer should overide this method in subclasses to provide their own
     * description.
     *
     * @return the Description
     */
    protected synchronized String createDescription()
    {
        return null;
    }

    /**
     * Utility method called by initialize to create OperationEntry objects.
     * A developer should overide this method in subclasses to provide their own
     * operations.
     *
     * @return the OperationEntry objests
     */
    protected synchronized OperationEntry[] createOperations()
    {
        return new OperationEntry[ 0 ];
    }

    /**
     * Utility method called by initialize to create AttributeEntry objects.
     * A developer should overide this method in subclasses to provide their own
     * attributes.
     *
     * @return the AttributeEntry objests
     */
    protected synchronized AttributeEntry[] createAttributes()
    {
        return new AttributeEntry[ 0 ];
    }

    /**
     * Utility method called by initialize to create MBeanNotificationInfo objects.
     * A developer should overide this method in subclasses to provide their own
     * NotificationInfos.
     *
     * @return the MBeanNotificationInfo objests
     */
    protected synchronized MBeanNotificationInfo[] createNotificationInfos()
    {
        return new MBeanNotificationInfo[ 0 ];
    }

    /**
     * Helper method for get/setAttribute to retrieve AttributeEntry.
     *
     * @param name the name of attribute
     * @return the AttributeEntry
     * @exception AttributeNotFoundException if attribute not found
     */
    private synchronized AttributeEntry getAttributeEntry( final String name )
        throws AttributeNotFoundException
    {
        for( int i = 0; i < m_attributes.length; i++ )
        {
            final String other = m_attributes[ i ].getInfo().getName();
            if( other.equals( name ) )
            {
                return m_attributes[ i ];
            }
        }

        throw new AttributeNotFoundException();
    }

    /**
     * Helper method for invoke() that finds the correct OperationEntry.
     *
     * @param action the name of operation
     * @param params the parameters of operation
     * @return the OperationEntry
     * @exception ReflectionException if can not find operation
     */
    private synchronized OperationEntry getOperationEntry( final String action,
                                                           final String[] params )
        throws ReflectionException
    {
        for( int i = 0; i < m_operations.length; i++ )
        {
            final MBeanOperationInfo info = m_operations[ i ].getInfo();
            final MBeanParameterInfo[] paramInfos = info.getSignature();

            //If operation doesn't have same name and
            //same number of parameters then it is
            //not the droids we are looking for
            if( !info.getName().equals( action ) ||
                paramInfos.length != params.length )
            {
                continue;
            }

            boolean found = true;
            for( int j = 0; j < paramInfos.length; j++ )
            {
                final String param = paramInfos[ j ].getType();
                if( !params[ j ].equals( param ) )
                {
                    found = false;
                    break;
                }
            }

            //If all the parameters have same
            //type then we have found a match
            //so return it
            if( found )
            {
                return m_operations[ i ];
            }
        }

        throw new ReflectionException( new NoSuchMethodException( action ) );
    }

    /**
     * Utility method to create MBeanInfo.
     *
     * @return the new MBeanInfo
     */
    private synchronized MBeanInfo createMBeanInfo()
    {
        final MBeanAttributeInfo[] attributes =
            new MBeanAttributeInfo[ m_attributes.length ];
        for( int i = 0; i < attributes.length; i++ )
        {
            attributes[ i ] = m_attributes[ i ].getInfo();
        }

        final MBeanOperationInfo[] operations =
            new MBeanOperationInfo[ m_operations.length ];
        for( int i = 0; i < operations.length; i++ )
        {
            operations[ i ] = m_operations[ i ].getInfo();
        }

        return new MBeanInfo( getObject().getClass().getName(),
                              m_description,
                              attributes,
                              null,
                              operations,
                              m_notifications );
    }
}
