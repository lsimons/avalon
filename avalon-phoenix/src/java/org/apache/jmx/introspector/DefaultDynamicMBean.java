/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.ReflectionException;

/**
 * TODO: the equals() methods do not work for the MBeanXXXInfo classes;
 * these need to be replaced with correct ones. Is this a bug in
 * javax.management???
 *
 * This class is used by DynamicMBeanFactory to create DynamicMBeans.
 * It can represent any object. Notifications are not supported.
 *
 * You can easily create custom DynamicMBeans by extending this class
 * and replacing the createXXX() methods. TODO: Or, you can replace the
 * getDescriptionFor() methods to add sensible documentation to your
 * DynamicMBean, making it easier to create Open MBeans if you wish
 * to do so.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
class DefaultDynamicMBean 
    implements DynamicMBean
{
    /** The object represented by this MBean */
    protected Object m_obj;
    /** The BeanInfo representing this MBean */
    protected BeanInfo m_beanInfo = null;
    /** The MBeanInfo object. */
    protected MBeanInfo m_mBeanInfo = null;

    /** The human readable description of the class. */
    protected String m_description = null;
    /** Tells if this is an open MBean. */
    protected boolean m_isOpen = false;
    /** The MBean attribute descriptors. */
    protected MBeanAttributeInfo[] m_attributes = null;
    /** The MBean operation descriptors. */
    protected MBeanOperationInfo[] m_operations = null;
    /** The MBean constructor descriptors. */
    protected MBeanConstructorInfo[] m_constructors = null;
    /** The MBean notification descriptors. */
    protected MBeanNotificationInfo[] m_notifications = null;

    public DefaultDynamicMBean( ) 
    {
    }

    public DefaultDynamicMBean( final Object obj ) throws IllegalArgumentException
    {
        m_obj = obj;
        final Class clazz = obj.getClass();
        try { m_beanInfo = Introspector.getBeanInfo( clazz ); }
        catch( Exception e ) { throw new IllegalArgumentException( "Not a compliant javabean!" ); }

        m_constructors = createConstructorInfo( clazz.getConstructors() );
        m_operations = createOperationInfo( m_beanInfo.getMethodDescriptors() );
        m_attributes = createAttributeInfo( m_beanInfo.getPropertyDescriptors() );
        m_notifications = new MBeanNotificationInfo[0];
        m_description = m_beanInfo.getBeanDescriptor().getShortDescription();
    }
    DefaultDynamicMBean( final Object obj, final Class[] interfaces )
        throws IllegalArgumentException
    {
        this( obj );

        restrictOperationsTo( interfaces );
        restrictAttributesTo( interfaces );
    }
    DefaultDynamicMBean( final Object obj, final MBeanInfo mBeanInfo )
        throws IllegalArgumentException
    {
        this( obj );

        // make sure the MBeanInfo is correct
        try {
            if( !( Class.forName( mBeanInfo.getClassName() ) == obj.getClass() ) )
            {
                throw new IllegalArgumentException( "The supplied mBeanInfo does not describe the object!" );
            }
            if( mBeanInfo.getNotifications() != m_notifications )
            {
                throw new IllegalArgumentException( "The DefaultDynamicMBean does not support notifications!" );
            }
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new IllegalArgumentException( "Unable to find the class specified by the MBeanInfo!" );
        }
        m_description = mBeanInfo.getDescription();
        m_mBeanInfo = mBeanInfo;
    }

    ///////////////////////////////
    /// DYNAMIC MBEAN INTERFACE ///
    ///////////////////////////////
    /**
     * Obtains the value of a specific attribute of the Dynamic MBean.
     */
    public Object getAttribute( final String attribute )
        throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        for( int i = 0; i < m_attributes.length; i++ )
        {
            if( m_attributes[i].getName().equals( attribute ) )
            {
                if( m_attributes[i].isReadable() )
                {
                    final PropertyDescriptor[] m = m_beanInfo.getPropertyDescriptors();
                    for( int j = 0; j < m.length; j++ )
                    {
                        if( m[i].getName().equals( attribute ) )
                        {
                            try
                            {
                                return m[i].getReadMethod().invoke( m_obj, new Object[0] );
                            }
                            catch( InvocationTargetException ite )
                            {
                                throw new MBeanException( ite );
                            }
                            catch( Exception e )
                            {
                                throw new ReflectionException( e );
                            }
                        }
                    }
                }
            }
        }
        throw new AttributeNotFoundException();
    }

    /**
     * Sets the value of a specific attribute of the Dynamic MBean
     */
    public void setAttribute( final Attribute attribute )
        throws AttributeNotFoundException, InvalidAttributeValueException,
        MBeanException, ReflectionException
    {
        for( int i = 0; i < m_attributes.length; i++ )
        {
            if( m_attributes[i].getName().equals( attribute.getName() ) )
            {
                if( m_attributes[i].isWritable() )
                {
                    final PropertyDescriptor[] m = m_beanInfo.getPropertyDescriptors();
                    for( int j = 0; j < m.length; j++ )
                    {
                        if( m[i].getName().equals( attribute.getName() ) )
                        {
                            try
                            {
                                m[i].getWriteMethod().invoke( m_obj, new Object[] { attribute.getValue() } );
                            }
                            catch( IllegalArgumentException iae )
                            {
                                throw new InvalidAttributeValueException();
                            }
                            catch( InvocationTargetException ite )
                            {
                                throw new MBeanException( ite );
                            }
                            catch( Exception e )
                            {
                                throw new ReflectionException( e );
                            }
                        }
                    }
                }
            }
        }
        throw new AttributeNotFoundException();
    }

    /**
     * Enables the values of several attributes of the Dynamic MBean.
     */
    public AttributeList getAttributes( final String[] atts )
    {
        AttributeList attList = new AttributeList();
        for( int i = 0; i < atts.length; i++ )
        {
            for( int j = 0; j < m_attributes.length; j++ )
            {
                if( m_attributes[j].getName().equals( atts[i] ) )
                    attList.add( m_attributes[j] );
            }
        }
        return attList;
    }

    /**
     * Sets the values of several attributes of the Dynamic MBean.
     */
    public AttributeList setAttributes( final AttributeList atts )
    {
        final AttributeList attList = new AttributeList();
        for( int i = 0; i < atts.size(); i++ )
        {
            try
            {
                final Attribute attr = (Attribute)atts.get(i);
                setAttribute( attr );
                attList.add( attr );
            }
            catch( Exception e ) {}
        }
        return attList;
    }

    /**
     * Allows an action to be invoked on the Dynamic MBean.
     *
     * @param actionName The name of the action to be invoked.
     * @param params An array containing the parameters to be set when the action is
     * invoked.
     * @param signature An array containing the signature of the action. The class objects will
     * be loaded through the same class loader as the one used for loading the
     * MBean on which the action is invoked.
     *
     * @return  The object returned by the action, which represents the result of
     * invoking the action on the MBean specified.
     * @exception MBeanException  Wraps a <CODE>java.lang.Exception</CODE> thrown by the MBean's invoked method.
     * @exception ReflectionException  Wraps a <CODEjava.lang.Exception</CODE thrown while trying to invoke the method
     */
    public Object invoke( final String actionName, final Object[] params, final String[] signature )
        throws MBeanException, ReflectionException
    {
        final Method m = getMethodFor( actionName, params, signature );
        try
        {
            return m.invoke( m_obj, params );
        }
        catch( InvocationTargetException ite )
        {
            throw new MBeanException( ite );
        }
        catch( Exception e )
        {
            throw new ReflectionException( e );
        }
    }
        /**
         * Helper method for invoke() that finds the correct method.
         * Returns null on error.
         */
        private Method getMethodFor(
            final String actionName,
            final Object[] params,
            final String[] signature ) throws ReflectionException
        {
            Method m;
            Class[] classes = new Class[signature.length];
            for( int i = 0; i < classes.length; i++ )
            {
                try { classes[i] = Class.forName( signature[i] ); }
                catch( Exception e ) { throw new ReflectionException( e ); }
            }
            for( int i = 0; i < m_operations.length; i++ )
            {
                if( m_operations[i].getName().equals( actionName ) )
                {
                    boolean isEqual = true;
                    final MBeanParameterInfo[] paramInfo = m_operations[i].getSignature();
                    if( !(paramInfo.length == signature.length) )
                        continue;
                    for( int j = 0; j < paramInfo.length; j++ )
                    {
                        Class clazz = null;
                        try { clazz = Class.forName( signature[j] ); }
                        catch( Exception e ) {}
                        if( !paramInfo.getClass().equals( clazz ) )
                        {
                            isEqual = false;
                            break;
                        }
                    }
                    if( isEqual )
                    {
                        final MethodDescriptor[] md = m_beanInfo.getMethodDescriptors();
                        for( int j = 0; j < md.length; j++ )
                        {
                            if( md[i].getName().equals( actionName ) )
                            {
                                return md[i].getMethod();
                            }
                        }
                    }
                }
            }
            throw new ReflectionException( new Exception() );
        }

    /**
     * Provides the exposed attributes and actions of the Dynamic MBean using an MBeanInfo object.
     *
     * @return  An instance of <CODE>MBeanInfo</CODE> allowing all attributes and actions
     * exposed by this Dynamic MBean to be retrieved.
     */
    public MBeanInfo getMBeanInfo()
    {
        if( m_mBeanInfo == null )
            return new MBeanInfo(
                m_obj.getClass().getName(),
                m_description,
                m_attributes,
                m_constructors,
                m_operations,
                m_notifications );
        return m_mBeanInfo;
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    /**
     * Creates an MBeanConstructorInfo array with an entry for each of the
     * supplied constructors.
     */
    protected MBeanConstructorInfo[] createConstructorInfo( final Constructor[] constructors )
    {
        final MBeanConstructorInfo[] info = new MBeanConstructorInfo[constructors.length];

        for( int i = 0; i < constructors.length; i++ )
        {
            info[i] = new MBeanConstructorInfo(
                "Creates a new instance of this MBean.",constructors[i] );
        }
        return info;
    }
    /**
     * Creates an MBeanOperationInfo array with an entry for each supplied
     * method that is an operation.
     */
    protected MBeanOperationInfo[] createOperationInfo( final MethodDescriptor[] methodDescriptor )
    {
        MBeanOperationInfo[] info = new MBeanOperationInfo[methodDescriptor.length];

        for( int i = 0; i < info.length; i++ )
        {
            info[i] = new MBeanOperationInfo(
                methodDescriptor[i].getShortDescription(),
                methodDescriptor[i].getMethod() );
        }
        return info;
    }
    /**
     * Creates an MBeanAttributeInfo array with an entry for every attribute.
     */
    protected MBeanAttributeInfo[] createAttributeInfo(
        final PropertyDescriptor[] propertyDescriptor ) throws IllegalArgumentException
    {
        MBeanAttributeInfo[] info = new MBeanAttributeInfo[propertyDescriptor.length];

        for( int i = 0; i < info.length; i++ )
        {
            try
            {
            info[i] = new MBeanAttributeInfo(
                propertyDescriptor[i].getName(),
                propertyDescriptor[i].getShortDescription(),
                propertyDescriptor[i].getReadMethod(),
                propertyDescriptor[i].getWriteMethod() );
            }
            catch( IntrospectionException ie )
            {
                throw new IllegalArgumentException( "Error introspecting properties." );
            }
        }
        return info;
    }

    /**
     * Strips the operations array from unwanted entries.
     */
    /**
     * Strips the attributes array from unwanted entries.
     */
    protected void restrictOperationsTo( Class[] interfaces ) throws IllegalArgumentException
    {
        ArrayList allowedOperations = new ArrayList();
        ArrayList operationInfo = new ArrayList();

        for( int i = 0; i < interfaces.length; i++ )
        {
            BeanInfo beanInfo = null;
            try { beanInfo = Introspector.getBeanInfo( interfaces[i] ); }
            catch( Exception e ) {
                throw new IllegalArgumentException(
                    "The supplied interfaces are not all valid javabeans!" ); }
            final MethodDescriptor[] md = beanInfo.getMethodDescriptors();
            for( int j = 0; j < md.length; j++ )
            {
                final MBeanOperationInfo info = new MBeanOperationInfo(
                    md[i].getShortDescription(),
                    md[i].getMethod() );
                allowedOperations.add( info );
            }
        }
        MBeanOperationInfo[] m =
            (MBeanOperationInfo[])allowedOperations.toArray( new MBeanOperationInfo[0] );

        for( int i = 0; i < m.length; i++ )
        {
            for( int j = 0; j < m_operations.length; j++ )
            {
                if( m[i].equals( m_operations[j] ) )
                    operationInfo.add( m_operations[j] );
            }
        }
        m_operations = (MBeanOperationInfo[])operationInfo.toArray( new MBeanOperationInfo[0] );
    }
    /**
     * Strips the attributes array from unwanted entries.
     */
    protected void restrictAttributesTo( Class[] interfaces ) throws IllegalArgumentException
    {
        ArrayList allowedAttributes = new ArrayList();
        ArrayList attributeInfo = new ArrayList();

        for( int i = 0; i < interfaces.length; i++ )
        {
            BeanInfo beanInfo = null;
            try { beanInfo = Introspector.getBeanInfo( interfaces[i] ); }
            catch( Exception e ) {
                throw new IllegalArgumentException(
                    "The supplied interfaces are not all valid javabeans!" ); }
            final PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
            for( int j = 0; j < pd.length; j++ )
            {
                try
                {
                final MBeanAttributeInfo info = new MBeanAttributeInfo(
                    pd[i].getName(),
                    pd[i].getShortDescription(),
                    pd[i].getReadMethod(),
                    pd[i].getWriteMethod() );
                allowedAttributes.add( info );
                }
                catch( IntrospectionException ie )
                {
                    throw new IllegalArgumentException( "Error introspecting properties" );
                }
            }
        }
        MBeanAttributeInfo[] m =
            (MBeanAttributeInfo[])allowedAttributes.toArray( new MBeanAttributeInfo[0] );

        for( int i = 0; i < m.length; i++ )
        {
            for( int j = 0; j < m_attributes.length; j++ )
            {
                if( m[i].equals( m_attributes[j] ) )
                    attributeInfo.add( m_attributes[j] );
            }
        }
        m_attributes = (MBeanAttributeInfo[])attributeInfo.toArray( new MBeanAttributeInfo[0] );
    }
 }
