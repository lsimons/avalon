/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.management.AttributeList;
import javax.management.IntrospectionException;
import javax.management.MBeanOperationInfo;

/**
 * Class to automatically manage a JavaBean. A BeanInfo is created for the
 * object to be managed and its propertys are translated into attributes,
 * and it's Methods translated into operations.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.3 $ $Date: 2001/09/29 00:07:01 $
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
        super( object );
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
    }

    JavaBeanMBean( final Object object, final Class[] interfaces )
        throws IllegalArgumentException
    {
        this( object );

        //restrictOperationsTo( interfaces );
        //restrictAttributesTo( interfaces );
    }

    /**
     * Utility method called by initialize to create description.
     *
     * @return the Description
     */
    protected synchronized String createDescription()
    {
        return m_beanInfo.getBeanDescriptor().getShortDescription();
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
