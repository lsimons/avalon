/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import java.lang.reflect.Constructor;
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
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.ReflectionException;

/**
 * This class is used by DynamicMBeanFactory to create DynamicMBeans. It can
 * represent any object. Notifications are not supported. You can easily create
 * custom DynamicMBeans by extending this class and replacing the createXXX()
 * methods. TODO: Or, you can replace the getDescriptionFor() methods to add
 * sensible documentation to your DynamicMBean, making it easier to create Open
 * MBeans if you wish to do so.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/24 05:00:53 $
 */
class DefaultDynamicMBean 
    implements DynamicMBean
{
    private final static String DEFAULT_DESCRIPTION = "A manageable object.";

    /**
     * The object represented by this MBean
     */
    protected final Object m_object;
    /**
     * The MBeanInfo object.
     */
    protected MBeanInfo m_mBeanInfo;

    /**
     * The human readable description of the class.
     */
    protected String m_description;
    /**
     * Tells if this is an open MBean.
     */
    protected boolean m_isOpen;
    /**
     * The MBean attribute descriptors.
     */
    protected MBeanAttributeInfo[] m_attributes;
    /**
     * The MBean operation descriptors.
     */
    protected MBeanOperationInfo[] m_operations;
    /**
     * The MBean constructor descriptors.
     */
    protected MBeanConstructorInfo[] m_constructors;
    /**
     * The MBean notification descriptors.
     */
    protected MBeanNotificationInfo[] m_notifications;

    DefaultDynamicMBean( final Object object )
        throws NotCompliantMBeanException
    {
        m_object = object;
        final Class clazz = object.getClass();

        final Constructor[] constructors = clazz.getConstructors();
        final Method[] methods = clazz.getMethods();

        m_constructors = createConstructorInfo( constructors );
        m_operations = createOperationInfo( methods );
        m_attributes = createAttributeInfo( methods );
        m_notifications = new MBeanNotificationInfo[ 0 ];
        m_description = DEFAULT_DESCRIPTION;

        createMBeanInfo();
    }

    DefaultDynamicMBean( final Object object, final MBeanInfo mBeanInfo )
        throws IllegalArgumentException, NotCompliantMBeanException
    {
        this( object );

        m_mBeanInfo = mBeanInfo;

        // make sure the MBeanInfo is correct
        try
        {
            if( !( Class.forName( mBeanInfo.getClassName() ) == object.getClass() ) )
            {
                throw new IllegalArgumentException( "The supplied mBeanInfo does not " + 
                                                    "describe the object!" );
            }
            if( mBeanInfo.getNotifications() != m_notifications )
            {
                throw new IllegalArgumentException( "The DefaultDynamicMBean does " + 
                                                    "not support notifications!" );
            }
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new IllegalArgumentException( "Unable to find the class specified by the MBeanInfo!" );
        }

        m_description = mBeanInfo.getDescription();
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
        final String methodName = 
            "get" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 );
        final String methodName2 = 
            "is" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 );

        final Class clazz = m_object.getClass();

        Method method;
        try
        {
            method = clazz.getMethod( methodName, new Class[0] );
        }
        catch( final Exception e )
        {
            try
            {
                method = clazz.getMethod( methodName2, new Class[0] );
            }
            catch( final Exception ee )
            {
                throw new AttributeNotFoundException();
            }
        }

        try
        {
            return method.invoke( m_object, new Object[0] );
        }
        catch( final Exception e )
        {
            throw new MBeanException( e );
        }
    }

    /**
     * Enables the values of several attributes of the Dynamic MBean.
     */
    public AttributeList getAttributes( final String[] atts )
    {
        AttributeList attList = new AttributeList();
        for( int i = 0; i < atts.length; i++ )
        {
            final String name = atts[i];
            final String methodName = 
                "get" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );
            final String methodName2 = 
                "is" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );

            final Class clazz = m_object.getClass();

            Object value = null;
            Method method;
            
            try
            {
                method = clazz.getMethod( methodName, new Class[0] );
            }
            catch( final Exception e )
            {
                try
                {
                    method = clazz.getMethod( methodName2, new Class[0] );
                }
                catch( final Exception ee )
                {
                    continue;
                }
            }

            try
            {
                value = method.invoke( m_object, new Object[0] );
            }
            catch( final Exception e )
            {
            }

            if( value != null )
            {
                attList.add( new Attribute( name, value ) );
            }
        }

        return attList;
    }

    /**
     * Provides the exposed attributes and actions of the Dynamic MBean using an
     * MBeanInfo object.
     *
     * @return An instance of <CODE>MBeanInfo</CODE> allowing all attributes and
     *      actions exposed by this Dynamic MBean to be retrieved.
     */
    public MBeanInfo getMBeanInfo()
    {
        return m_mBeanInfo;
    }

    /**
     * Allows an action to be invoked on the Dynamic MBean.
     *
     * @param actionName The name of the action to be invoked.
     * @param params An array containing the parameters to be set when the
     *      action is invoked.
     * @param signature An array containing the signature of the action. The
     *      class objects will be loaded through the same class loader as the
     *      one used for loading the MBean on which the action is invoked.
     * @return The object returned by the action, which represents the result of
     *      invoking the action on the MBean specified.
     * @exception MBeanException Wraps a <CODE>java.lang.Exception</CODE> thrown
     *      by the MBean's invoked method.
     * @exception ReflectionException Wraps a <CODEjava.lang.Exception</CODE
     *      thrown while trying to invoke the method
     */
    public Object invoke( final String actionName, final Object[] params, final String[] signature )
        throws MBeanException, ReflectionException
    {
        Method method;

        try
        {
            final Class[] classes = new Class[ signature.length ];

            for( int i = 0; i < classes.length; i++ )
            {
                classes[ i ] = Class.forName( signature[ i ] );
            }

            method = m_object.getClass().getMethod( actionName, classes );
        }
        catch( final Exception e )
        {
            throw new ReflectionException( e );
        }

        try
        {
            return method.invoke( m_object, params );
        }
        catch( final Exception e )
        {
            throw new MBeanException( e );
        }
    }

    /**
     * Sets the value of a specific attribute of the Dynamic MBean
     */
    public void setAttribute( final Attribute attribute )
        throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, 
               ReflectionException
    {
        final String name = attribute.getName();
        final String methodName = 
            "set" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );

        Method method;
        try
        {
            final Class clazz = m_object.getClass();
            method = clazz.getMethod( methodName,
                                      new Class[] { attribute.getValue().getClass() } );
        }
        catch( final Exception e )
        {
            throw new AttributeNotFoundException();
        }

        try
        {
            method.invoke( m_object, new Object[] { attribute.getValue() } );
        }
        catch( final Exception e )
        {
            throw new MBeanException( e );
        }
    }

    /**
     * Sets the values of several attributes of the Dynamic MBean.
     */
    public AttributeList setAttributes( final AttributeList atts )
    {
        final AttributeList attList = new AttributeList();

        for( int i = 0; i < atts.size(); i++ )
        {
            final Attribute att = (Attribute)atts.get( i );
            final String name = att.getName();
            final String methodName = "set" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );

            Object value = null;
            Method m;
            try
            {
                m = m_object.getClass().getMethod( methodName, new Class[]{att.getValue().getClass()} );
            }
            catch( Exception e )
            {
                continue;
            }
            try
            {
                value = m.invoke( m_object, new Object[]{att.getValue()} );
            }
            catch( Exception e )
            {
            }
            if( value != null )
            {
                attList.add( new Attribute( name, value ) );
            }
        }
        return attList;
    }

    /**
     * Extract all the Methods from the supplied array that are operations.
     */
    protected Method[] getOperationFor( Method[] methods )
    {
        final ArrayList ops = new ArrayList();
        for( int i = 0; i < methods.length; i++ )
        {
            final String name = methods[i].getName();
            final String uppercaseChars = new String( "ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
            final boolean isGetter =
                ( name.startsWith( "get" ) &&
                  ( uppercaseChars.indexOf( name.substring( 3, 4 ) ) != -1 ) );
            final boolean isSetter =
                ( name.startsWith( "set" ) &&
                  ( uppercaseChars.indexOf( name.substring( 3, 4 ) ) != -1 ) );
            final boolean isIs =
                ( name.startsWith( "is" ) &&
                  ( uppercaseChars.indexOf( name.substring( 2, 3 ) ) != -1 ) );
            if( !( isGetter || isSetter || isIs ) )
            {
                ops.add( methods[i] );
            }
        }
        return (Method[])ops.toArray();
    }

    /**
     * Extract all Methods from the supplied array that are of the form getXxx,
     * setXxx or isXxx.
     *
     * @return A multi-dimensional array, with the first element being an array
     *      of getter methods, the second element being an array of setter
     *      methods, and the third element being an array of isser methods.
     */
    protected Method[][] getAttributeFor( Method[] methods )
    {
        final ArrayList getters = new ArrayList();
        final ArrayList setters = new ArrayList();
        final ArrayList issers = new ArrayList();

        Method[][] m = new Method[3][];
        for( int i = 0; i < methods.length; i++ )
        {
            final String name = methods[i].getName();
            final String uppercaseChars = new String( "ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
            final boolean isGetter =
                ( name.startsWith( "get" ) &&
                  ( uppercaseChars.indexOf( name.substring( 3, 4 ) ) != -1 ) );
            final boolean isSetter =
                ( name.startsWith( "set" ) &&
                  ( uppercaseChars.indexOf( name.substring( 3, 4 ) ) != -1 ) );
            final boolean isIs =
                ( name.startsWith( "is" ) &&
                  ( uppercaseChars.indexOf( name.substring( 2, 3 ) ) != -1 ) );
            if( isGetter )
            {
                getters.add( methods[i] );
            }
            if( isSetter )
            {
                setters.add( methods[i] );
            }
            if( isIs )
            {
                issers.add( methods[i] );
            }
        }
        m[0] = (Method[])getters.toArray();
        m[1] = (Method[])setters.toArray();
        m[2] = (Method[])issers.toArray();
        return m;
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
        final MBeanConstructorInfo[] info = new MBeanConstructorInfo[ constructors.length ];

        for( int i = 0; i < constructors.length; i++ )
        {
            info[i] = new MBeanConstructorInfo( "Creates a new instance of this MBean.", constructors[i] );
        }

        return info;
    }

    /**
     * Creates an MBeanOperationInfo array with an entry for each supplied
     * method that is an operation. A method is an operation when it is not
     * named getXxxx, setXxxx or isXxxx.
     */
    protected MBeanOperationInfo[] createOperationInfo( final Method[] methods )
    {
        // getXXX, setXXX and isXXX are not operations, so remove those
        final Method[] ops = getOperationFor( methods );

        MBeanOperationInfo[] info = new MBeanOperationInfo[ops.length];

        for( int i = 0; i < ops.length; i++ )
        {
            info[i] = new MBeanOperationInfo(
                                             "Call a method on this MBean", ops[i] );
        }
        return info;
    }

    /**
     * Creates an MBeanAttributeInfo array with an entry for every attribute.
     * The exposed attributes are found by taking a look at all methods that are
     * named getXxx and isXxx. While this method finds the corresponding setXxx
     * method for these, it does not add an entry into the returned array for
     * write-only attributes. TODO:

     * @throws NotCompliantMBeanException when the getXxx, isXxx or setXxx don't
     *      have the right number of arguments (0, 0 and 1, respectively).
     */
    protected MBeanAttributeInfo[] createAttributeInfo( Method[] methods )
        throws NotCompliantMBeanException
    {
        // only getXXX, setXXX and isXXX expose attributes, so remove
        // all others
        final Method[][] m = getAttributeFor( methods );

        // all getter and isser methods mean an attribute
        final MBeanAttributeInfo[] info = new MBeanAttributeInfo[( m[0].length + m[2].length )];
        // fill info from this point
        int beginIndex = 0;

        // create MBeanAttributeInfo's for all getter methods
        for( int i = 0; i < m[0].length; i++ )
        {
            final Method getter = m[0][i];
            String attributeName = getter.getName().substring( 3 );
            Method setter = null;
            for( int j = 0; j < m[1].length; j++ )
            {
                String setterName = m[1][j].getName();
                if( setterName.equals( "set" + attributeName ) )
                {
                    setter = m[0][j];
                    break;
                }
            }
            attributeName = attributeName.substring( 0, 1 ).toLowerCase() + attributeName.substring( 1 );
            try
            {
                info[beginIndex] = new MBeanAttributeInfo( attributeName, "", getter, setter );
            }
            catch( IntrospectionException ie )
            {
                throw new NotCompliantMBeanException();
            }
            beginIndex++;
        }

        // create MBeanAttributeInfo's for all isser methods
        for( int i = 0; i < m[2].length; i++ )
        {
            final Method isser = m[2][i];
            final String attributeName = isser.getName().substring( 2 );
            Method setter = null;
            for( int j = 0; j < m[1].length; j++ )
            {
                String setterName = m[1][j].getName();
                if( setterName.equals( "set" + attributeName ) )
                {
                    setter = m[0][j];
                    break;
                }
            }

            try
            {
                info[ beginIndex ] = new MBeanAttributeInfo( attributeName, "", isser, setter );
            }
            catch( IntrospectionException ie )
            {
                throw new NotCompliantMBeanException();
            }
            beginIndex++;
        }
        return info;
    }

    protected void createMBeanInfo()
    {
        m_mBeanInfo = 
            new MBeanInfo( m_object.getClass().getName(),
                           m_description,
                           m_attributes,
                           m_constructors,
                           m_operations,
                           m_notifications );
    }
}
