/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.jmx.introspector;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import javax.management.IntrospectionException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/**
 * Base Class to allow MBeans to be constructed programatically via
 * simple method calls. The attributes and operations of MBean are
 * verified by reflection,
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.6 $ $Date: 2002/03/16 00:11:57 $
 */
public abstract class ConstructiveMBean
    extends AbstractMBean
{
    private static final String[] EMPTY_STR_ARRAY = new String[ 0 ];

    protected final static int ACTION = MBeanOperationInfo.ACTION;
    protected final static int ACTION_INFO = MBeanOperationInfo.ACTION_INFO;
    protected final static int INFO = MBeanOperationInfo.INFO;
    protected final static int UNKNOWN = MBeanOperationInfo.UNKNOWN;

    /**
     * The temporary list of operations used to create operation list.
     */
    private ArrayList m_operations = new ArrayList();

    /**
     * The temporary list of attributes used to create attribute list.
     */
    private ArrayList m_attributes = new ArrayList();

    public ConstructiveMBean( final Object object )
        throws IllegalArgumentException
    {
        this( object, true );
    }

    public ConstructiveMBean( final Object object, final boolean secure )
        throws IllegalArgumentException
    {
        super( object );

        if( secure )
        {
            final SecurityManager sm = System.getSecurityManager();
            if( null != sm )
            {
                final String classname = object.getClass().getName();
                final JMXPermission permission = new JMXPermission( "create", classname );
                sm.checkPermission( permission );
            }
        }

        defineObject();

        initialize();

        m_attributes.clear();
        m_operations.clear();
        m_attributes = null;
        m_operations = null;
    }

    /**
     * Utility method called to define manageable
     * objects attributes and operations.
     */
    protected abstract void defineObject();

    /**
     * Utility method to define an attribute with specified name.
     *
     * @param name the attribute name
     */
    protected final void addAttribute( final String name )
    {
        addAttribute( name, true );
    }

    /**
     * Utility method to define an attribute with specified name and specify whether it is writeable.
     *
     * @param name the attribute name
     * @param isWriteable true if attribute is writeable, false otherwise
     */
    protected final void addAttribute( final String name,
                                       final boolean isWriteable )
    {
        addAttribute( name, isWriteable, null );
    }

    /**
     * Utility method to define an attribute with specified name and description.
     *
     * @param name the attribute name
     * @param isWriteable true if attribute is writeable, false otherwise
     * @param description the description
     */
    protected final void addAttribute( final String name,
                                       final boolean isWriteable,
                                       final String description )
    {
        final String property = Introspector.decapitalize( name );
        final String methodPropertyName =
            property.substring( 0, 1 ).toUpperCase() + property.substring( 1 );

        final String getAccessorName = "get" + methodPropertyName;
        final String isAccessorName = "is" + methodPropertyName;
        final String mutatorName = "set" + methodPropertyName;

        Method accessor = null;

        final Class clazz = getObject().getClass();
        final Method[] methods = clazz.getMethods();
        for( int i = 0; i < methods.length; i++ )
        {
            final Method method = methods[ i ];

            if( 0 == ( Modifier.PUBLIC & method.getModifiers() ) )
            {
                //If method is not public then skip it
                continue;
            }

            //Look for a accessor
            if( 0 == method.getParameterTypes().length )
            {
                if( method.getName().equals( getAccessorName ) ||
                    method.getName().equals( isAccessorName ) )
                {
                    accessor = method;
                    break;
                }
            }
        }

        if( null == accessor )
        {
            throw new IllegalArgumentException( "Unable to locate accessor for property " +
                                                name );
        }

        Method mutator = null;
        if( isWriteable )
        {
            final Class[] params = new Class[]{accessor.getReturnType()};
            try
            {
                mutator = clazz.getMethod( mutatorName, params );
            }
            catch( final NoSuchMethodException nsme )
            {
                throw new IllegalArgumentException( "Unable to locate mutator for property " +
                                                    name );
            }
        }

        try
        {
            final AttributeEntry entry =
                new AttributeEntry( name, description, accessor, mutator );
            m_attributes.add( entry );
        }
        catch( final IntrospectionException ie )
        {
            throw new IllegalArgumentException( "Unable to add attribute due to " + ie );
        }
    }

    /**
     * Utility method to define an operation with specified name and impact.
     * Operation is parameter method.
     *
     * @param name the operation name
     * @param impact the operation impact
     */
    protected final void addOperation( final String name, final int impact )
    {
        addOperation( name, impact, null );
    }

    /**
     * Utility method to define an operation with specified name, impact and description.
     *
     * @param name the operation name
     * @param impact the operation impact
     * @param description the description of operation
     */
    protected final void addOperation( final String name,
                                       final int impact,
                                       final String description )
    {
        addOperation( name, EMPTY_STR_ARRAY, impact, description, EMPTY_STR_ARRAY );
    }

    /**
     * Utility method to define an operation with specified
     * name, parameter types and impact.
     *
     * @param name the operation name
     * @param params the class names of parameters
     * @param impact the operation impact
     */
    protected final void addOperation( final String name,
                                       final String[] params,
                                       final int impact )
    {

        addOperation( name, params, impact, null );
    }

    /**
     * Utility method to define an operation with specified
     * name, parameter types, impact and description.
     *
     * @param name the operation name
     * @param params the class names of parameters
     * @param impact the operation impact
     * @param description the description of operation
     */
    protected final void addOperation( final String name,
                                       final String[] params,
                                       final int impact,
                                       final String description )
    {
        addOperation( name, params, impact, description, null );
    }

    /**
     * Utility method to define an operation with specified
     * name, parameter types, impact and description.
     *
     * @param name the operation name
     * @param params the class names of parameters
     * @param impact the operation impact
     * @param description the description of operation
     * @param paramNames the names of all the parameters
     */
    protected final void addOperation( final String name,
                                       final String[] params,
                                       final int impact,
                                       final String description,
                                       final String[] paramNames )
    {
        addOperation( name, params, impact, description, null, null );
    }

    /**
     * Utility method to define an operation with specified
     * name, parameter types, impact, description and parameter
     * descriptions.
     *
     * @param name the operation name
     * @param params the class names of parameters
     * @param impact the operation impact
     * @param description the description of operation
     * @param paramNames the names of all the parameters
     * @param paramsDescription the description of parameters
     */
    protected final void addOperation( final String name,
                                       final String[] params,
                                       final int impact,
                                       final String description,
                                       final String[] paramNames,
                                       final String[] paramsDescription )
    {
        if( null != paramNames && params.length != paramNames.length )
        {
            final String message = "Params length not match param names length";
            throw new IllegalArgumentException( message );
        }

        if( null != paramsDescription && params.length != paramsDescription.length )
        {
            final String message = "Params length not match params description length";
            throw new IllegalArgumentException( message );
        }

        final Class clazz = getObject().getClass();
        final ClassLoader classLoader = clazz.getClassLoader();
        final Class[] paramTypes = new Class[ params.length ];
        final MBeanParameterInfo[] paramInfos = new MBeanParameterInfo[ params.length ];
        for( int i = 0; i < params.length; i++ )
        {
            final String param = params[ i ];
            final String paramName =
                ( null != paramNames ) ? paramNames[ i ] : "param" + i;
            final String paramDescription =
                ( null != paramsDescription ) ? paramsDescription[ i ] : null;

            paramInfos[ i ] = new MBeanParameterInfo( paramName, param, paramDescription );

            try
            {
                paramTypes[ i ] = classLoader.loadClass( param );
            }
            catch( final Exception e )
            {
                throw new IllegalArgumentException( "error loading param type (" + param +
                                                    ") due to " + e );
            }
        }

        Method method = null;
        try
        {
            method = clazz.getMethod( name, paramTypes );
        }
        catch( final Exception e )
        {
            throw new IllegalArgumentException( "error retrieving method due to " + e );
        }

        final String type = method.getReturnType().getName();
        final MBeanOperationInfo info =
            new MBeanOperationInfo( name, description, paramInfos, type, impact );
        final OperationEntry entry = new OperationEntry( info, method );
        m_operations.add( entry );
    }

    /**
     * Utility method called by initialize to create OperationEntry objects.
     *
     * @return the OperationEntry objests
     */
    protected OperationEntry[] createOperations()
    {
        return (OperationEntry[])m_operations.toArray( new OperationEntry[ 0 ] );
    }

    /**
     * Utility method called by initialize to create AttributeEntry objects.
     *
     * @return the AttributeEntry objests
     */
    protected synchronized AttributeEntry[] createAttributes()
        throws IllegalArgumentException
    {
        return (AttributeEntry[])m_attributes.toArray( new AttributeEntry[ 0 ] );
    }
}
