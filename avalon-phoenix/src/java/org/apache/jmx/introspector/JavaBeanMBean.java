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
 * TODO: the equals() methods do not work for the MBeanXXXInfo classes; these
 * need to be replaced with correct ones. Is this a bug in javax.management???
 * This class is used by DynamicMBeanFactory to create DynamicMBeans. It can
 * represent any object. Notifications are not supported. You can easily create
 * custom DynamicMBeans by extending this class and replacing the createXXX()
 * methods. TODO: Or, you can replace the getDescriptionFor() methods to add
 * sensible documentation to your DynamicMBean, making it easier to create Open
 * MBeans if you wish to do so.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/09/28 23:48:09 $
 */
public class JavaBeanMBean
    extends AbstractMBean
{
    /**
     * The BeanInfo representing this MBean
     */
    private BeanInfo m_beanInfo;

    public JavaBeanMBean( final Object object )
        throws IllegalArgumentException
    {
        super( object, null );
        final Class clazz = object.getClass();
        try
        {
            m_beanInfo = Introspector.getBeanInfo( clazz );
        }
        catch( final Exception e )
        {
            throw new IllegalArgumentException( "Not a compliant javabean!" );
        }

        initialize();
        //m_description = m_beanInfo.getBeanDescriptor().getShortDescription();
    }

    JavaBeanMBean( final Object object, final Class[] interfaces )
        throws IllegalArgumentException
    {
        this( object );

        //restrictOperationsTo( interfaces );
        //restrictAttributesTo( interfaces );
    }

    /**
     * Utility method called by initialize to create OperationEntry objects.
     *
     * @return the OperationEntry objests
     */
    protected OperationEntry[] createOperations()
    {
        final MethodDescriptor[] methods = m_beanInfo.getMethodDescriptors();
        final OperationEntry[] entrys = new OperationEntry[ methods.length ];

        for( int i = 0; i < entrys.length; i++ )
        {
            final MethodDescriptor descriptor = methods[ i ];
            final Method method = descriptor.getMethod();
            final MBeanOperationInfo info = 
                new MBeanOperationInfo( descriptor.getShortDescription(),
                                        method );

            entrys[ i ] = new OperationEntry( info, method );
        }

        return entrys;
    }

    /**
     * Utility method called by initialize to create AttributeEntry objects.
     *
     * @return the AttributeEntry objests
     */
    protected synchronized AttributeEntry[] createAttributes()
        throws IllegalArgumentException
    {
        final PropertyDescriptor[] propertys = m_beanInfo.getPropertyDescriptors();
        final AttributeEntry[] entrys = new AttributeEntry[ propertys.length ];

        for( int i = 0; i < entrys.length; i++ )
        {
            final PropertyDescriptor property = propertys[ i ];
            try
            {
                entrys[ i ] = 
                    new AttributeEntry( property.getName(),
                                        property.getShortDescription(),
                                        property.getReadMethod(),
                                        property.getWriteMethod() );
            }
            catch( final IntrospectionException ie )
            {
                throw new IllegalArgumentException( "Error introspecting properties." );
            }
        }
        
        return entrys;
    }

    /**
     * Strips the operations array from unwanted entries.
     *
     * @param interfaces DOC: Insert Description of Parameter
     * @exception IllegalArgumentException DOC: Insert Description of Exception
     */
/*
    protected void restrictOperationsTo( Class[] interfaces )
        throws IllegalArgumentException
    {
        ArrayList allowedOperations = new ArrayList();
        ArrayList operationInfo = new ArrayList();

        for( int i = 0; i < interfaces.length; i++ )
        {
            BeanInfo beanInfo = null;
            try
            {
                beanInfo = Introspector.getBeanInfo( interfaces[i] );
            }
            catch( Exception e )
            {
                throw new IllegalArgumentException(
                                                   "The supplied interfaces are not all valid javabeans!" );
            }
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
                {
                    operationInfo.add( m_operations[j] );
                }
            }
        }
        m_operations = (MBeanOperationInfo[])operationInfo.toArray( new MBeanOperationInfo[0] );
    }

    protected void restrictAttributesTo( Class[] interfaces )
        throws IllegalArgumentException
    {
        ArrayList allowedAttributes = new ArrayList();
        ArrayList attributeInfo = new ArrayList();

        for( int i = 0; i < interfaces.length; i++ )
        {
            BeanInfo beanInfo = null;
            try
            {
                beanInfo = Introspector.getBeanInfo( interfaces[i] );
            }
            catch( Exception e )
            {
                throw new IllegalArgumentException(
                                                   "The supplied interfaces are not all valid javabeans!" );
            }
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
                {
                    attributeInfo.add( m_attributes[j] );
                }
            }
        }
        m_attributes = (MBeanAttributeInfo[])attributeInfo.toArray( new MBeanAttributeInfo[0] );
    }
*/
}
