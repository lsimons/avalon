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
import java.beans.ParameterDescriptor;
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
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.5 $ $Date: 2001/11/19 12:21:31 $
 */
public class JavaBeanMBean
    extends AbstractMBean
{
    /**
     * The BeanInfo representing this MBean
     */
    private BeanInfo m_beanInfo;

    /**
     * Methods for Operations that are allowed to operate on object.
     */
    private MethodDescriptor[] m_allowedOperations;

    /**
     * Propertys for Attributes that are allowed for object.
     */
    private PropertyDescriptor[] m_allowedAttributes;

    public JavaBeanMBean( final Object object )
        throws IllegalArgumentException
    {
        this( object, null );
    }

    public JavaBeanMBean( final Object object, final Class[] interfaces )
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

        m_allowedOperations = getAllowedOperations( interfaces );
        m_allowedAttributes = getAllowedAttributes( interfaces );

        initialize();
        
        m_allowedOperations = null;
        m_allowedAttributes = null;
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
        final ArrayList entrys = new ArrayList();
        
        for( int i = 0; i < methods.length; i++ )
        {
            final MethodDescriptor descriptor = methods[ i ];

            //Skip disallowed operations
            if( !isAllowedOperation( descriptor ) ) continue;

            final Method method = descriptor.getMethod();
            final MBeanOperationInfo info = 
                new MBeanOperationInfo( descriptor.getShortDescription(),
                                        method );

            entrys.add( new OperationEntry( info, method ) );
        }

        return (OperationEntry[])entrys.toArray( new OperationEntry[ 0 ] );
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
        final ArrayList entrys = new ArrayList();

        for( int i = 0; i < propertys.length; i++ )
        {
            final PropertyDescriptor property = propertys[ i ];

            //Skip disallowed attributes
            if( !isAllowedAttribute( property ) ) continue;

            try
            {
                final AttributeEntry attribute =
                    new AttributeEntry( property.getName(),
                                        property.getShortDescription(),
                                        property.getReadMethod(),
                                        property.getWriteMethod() );
                entrys.add( attribute );
            }
            catch( final IntrospectionException ie )
            {
                throw new IllegalArgumentException( "Error introspecting properties." );
            }
        }

        return (AttributeEntry[])entrys.toArray( new AttributeEntry[ 0 ] );
    }

    /**
     * Retrieve a list of allowed operations. 
     * Allowed operations are based on interfaces passed in.
     */
    private MethodDescriptor[] getAllowedOperations( final Class[] interfaces )
        throws IllegalArgumentException
    {
        if( null == interfaces ) return null;

        final ArrayList operations = new ArrayList();
        final ArrayList names = new ArrayList();

        for( int i = 0; i < interfaces.length; i++ )
        {
            BeanInfo beanInfo = null;
            try
            {
                beanInfo = Introspector.getBeanInfo( interfaces[i] );
            }
            catch( final Exception e )
            {
                throw new IllegalArgumentException( "The supplied interfaces are " + 
                                                    "not all valid javabeans!" );
            }

            final MethodDescriptor[] methods = beanInfo.getMethodDescriptors();
            for( int j = 0; j < methods.length; j++ )
            {
                operations.add( methods[ j ] );
                names.add( methods[ j ].getName() );
            }
        }

        return (MethodDescriptor[])operations.toArray( new MethodDescriptor[ 0 ] );
    }

    /**
     * Retrieve a list of allowed attributes. 
     * Allowed attributes are based on interfaces passed in.
     */
    private PropertyDescriptor[] getAllowedAttributes( final Class[] interfaces )
        throws IllegalArgumentException
    {
        if( null == interfaces ) return null;

        final ArrayList attributes = new ArrayList();
        final ArrayList names = new ArrayList();

        for( int i = 0; i < interfaces.length; i++ )
        {
            BeanInfo beanInfo = null;
            try
            {
                beanInfo = Introspector.getBeanInfo( interfaces[i] );
            }
            catch( final Exception e )
            {
                throw new IllegalArgumentException( "The supplied interfaces are " + 
                                                    "not all valid javabeans!" );
            }

            final PropertyDescriptor[] propertys = beanInfo.getPropertyDescriptors();
            for( int j = 0; j < propertys.length; j++ )
            {
                attributes.add( propertys[ j ] );
                names.add( propertys[ j ].getName() );
            }
        }

        return (PropertyDescriptor[])attributes.toArray( new PropertyDescriptor[ 0 ] );
    }

    /**
     * Determine if specified descriptor describes an allowed operation.
     * If no interfaces were passed in then all operations are allowed.
     *
     * @param method the operation descriptor
     * @return true if allowed, false otherwise
     */
    private boolean isAllowedOperation( final MethodDescriptor method )
    {
        if( null == m_allowedOperations ) return true;

        final ParameterDescriptor[] params = method.getParameterDescriptors();

        for( int i = 0; i < m_allowedOperations.length; i++ )
        {
            final MethodDescriptor other = m_allowedOperations[ i ];
            final ParameterDescriptor[] otherParams = other.getParameterDescriptors();

            //If operation doesn't have same name and 
            //same number of parameters then it is 
            //not the droids we are looking for
            if( !other.getName().equals( method.getName() ) )
            {
                continue;
            }
            else if( null == params && null == otherParams )
            {
                return true;
            }
            else if ( otherParams.length != params.length )
            {
                continue;
            }

            boolean found = true;
            for( int j = 0; j < otherParams.length; j++ )
            {
                final String type = params[ j ].getName();
                final String otherType = otherParams[ j ].getName();
                if( !otherType.equals( type ) )
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
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if specified descriptor describes an allowed attribute.
     * If no interfaces were passed in then all attributes are allowed.
     *
     * @param method the attribute descriptor
     * @return true if allowed, false otherwise
     */
    private boolean isAllowedAttribute( final PropertyDescriptor property )
    {
        if( null == m_allowedOperations ) return true;

        for( int i = 0; i < m_allowedAttributes.length; i++ )
        {
            final PropertyDescriptor other = m_allowedAttributes[ i ];

            //If attribute has same name then we have a match
            if( other.getName().equals( property.getName() ) )
            {
                return true;
            }
        }

        return false;
    }
}
