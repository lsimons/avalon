/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.management.DynamicMBean;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.NotCompliantMBeanException;
import javax.management.ReflectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.IntrospectionException;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcaster;

/**
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
class DefaultDynamicMBean implements DynamicMBean {
    private static final String DEFAULT_DESCRIPTION = "A manageable object.";

    /** The object represented by this MBean */
    protected final Object obj;
    /** The MBeanInfo object. */
    protected MBeanInfo mBeanInfo = null;

    /** The human readable description of the class. */
    protected String description = null;
    /** Tells if this is an open MBean. */
    protected boolean isOpen = false;
    /** The MBean attribute descriptors. */
    protected MBeanAttributeInfo[] attributes = null;
    /** The MBean operation descriptors. */
    protected MBeanOperationInfo[] operations = null;
    /** The MBean constructor descriptors. */
    protected MBeanConstructorInfo[] constructors = null;
    /** The MBean notification descriptors. */
    protected MBeanNotificationInfo[] notifications = null;

    DefaultDynamicMBean( Object obj ) throws NotCompliantMBeanException
    {
        this.obj = obj;
        final Class clazz = obj.getClass();

        final Constructor[] constructors = clazz.getConstructors();
        final Method[] methods = clazz.getMethods();

        this.constructors = createConstructorInfo( constructors );
        this.operations = createOperationInfo( methods );
        this.attributes = createAttributeInfo( methods );
        this.notifications = new MBeanNotificationInfo[0];
        this.description = this.DEFAULT_DESCRIPTION;

        createMBeanInfo();
    }
    DefaultDynamicMBean( Object obj, MBeanInfo mBeanInfo )
        throws IllegalArgumentException, NotCompliantMBeanException
    {
        this(obj);

        this.mBeanInfo = mBeanInfo;

        // make sure the MBeanInfo is correct
        try {
            if(!( Class.forName( mBeanInfo.getClassName() ) == obj.getClass() ) )
            {
                throw new IllegalArgumentException( "The supplied mBeanInfo does not describe the object!" );
            }
            if( mBeanInfo.getNotifications() != this.notifications )
            {
                throw new IllegalArgumentException( "The DefaultDynamicMBean does not support notifications!" );
            }
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new IllegalArgumentException( "Unable to find the class specified by the MBeanInfo!" );
        }
        this.description = mBeanInfo.getDescription();
    }

    ///////////////////////////////
    /// DYNAMIC MBEAN INTERFACE ///
    ///////////////////////////////
    /**
     * Obtains the value of a specific attribute of the Dynamic MBean.
     */
    public Object getAttribute( String attribute )
        throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        final String methodName = "get" + attribute.substring(0,1).toUpperCase() + attribute.substring(1);
        final String methodName2 = "is" + attribute.substring(0,1).toUpperCase() + attribute.substring(1);
        Method m;
        try
        {
            m = this.obj.getClass().getMethod(
                methodName,
                new Class[0] );
        } catch( Exception e )
        {
            try
            {
                m = this.obj.getClass().getMethod(
                    methodName2,
                    new Class[0] );
            }
            catch( Exception ee )
            {
                throw new AttributeNotFoundException();
            }
        }
        try
        {
            return m.invoke( this.obj, new Object[0] );
        }
        catch( Exception e )
        {
            throw new MBeanException( e );
        }
    }

    /**
     * Sets the value of a specific attribute of the Dynamic MBean
     */
    public void setAttribute( Attribute attribute )
        throws AttributeNotFoundException, InvalidAttributeValueException,
        MBeanException, ReflectionException
    {
        final String methodName = "set" + attribute.getName().substring(0,1).toUpperCase() + attribute.getName().substring(1);
        Method m;
        try
        {
            m = this.obj.getClass().getMethod(
                methodName,
                new Class[] { attribute.getValue().getClass() } );
        } catch( Exception e )
        {
            throw new AttributeNotFoundException();
        }
        try
        {
            m.invoke( this.obj, new Object[] { attribute.getValue() } );
        }
        catch( Exception e )
        {
            throw new MBeanException( e );
        }
    }

    /**
     * Enables the values of several attributes of the Dynamic MBean.
     */
    public AttributeList getAttributes( String[] atts )
    {
        AttributeList attList = new AttributeList();
        for( int i = 0; i < atts.length; i++ )
        {
            final String name = atts[i];
            final String methodName = "get" + atts[i].substring(0,1).toUpperCase() + atts[i].substring(1);
            final String methodName2 = "is" + atts[i].substring(0,1).toUpperCase() + atts[i].substring(1);
            Object value = null;
            Method m;
            try { m = this.obj.getClass().getMethod( methodName, new Class[0] ); }
            catch( Exception e )
            {
                try { m = this.obj.getClass().getMethod( methodName2, new Class[0] ); }
                catch( Exception ee ) { continue; }
            }
            try { value = m.invoke(this.obj, new Object[0] ); }
            catch( Exception e ) {}
            if( value != null )
                attList.add( new Attribute( name, value ) );
        }
        return attList;
    }

    /**
     * Sets the values of several attributes of the Dynamic MBean.
     */
    public AttributeList setAttributes( AttributeList atts )
    {
        AttributeList attList = new AttributeList();
        for( int i = 0; i < atts.size(); i++ )
        {
            final Attribute att = (Attribute)atts.get(i);
            final String name = att.getName();
            final String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
            Object value = null;
            Method m;
            try { m = this.obj.getClass().getMethod( methodName, new Class[] { att.getValue().getClass() } ); }
            catch( Exception e ) { continue; }
            try { value = m.invoke(this.obj, new Object[] { att.getValue() } ); }
            catch( Exception e ) {}
            if( value != null )
                attList.add( new Attribute( name, value ) );
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
    public Object invoke( String actionName, Object[] params, String[] signature )
        throws MBeanException, ReflectionException
    {
        Method m;
        try
        {
            Class[] classes = new Class[signature.length];
            for( int i = 0; i < classes.length; i++ )
            {
                classes[i] = Class.forName( signature[i] );
            }
            m = this.obj.getClass().getMethod( actionName, classes );
        }
        catch( Exception e )
        {
            throw new ReflectionException( e );
        }
        try
        {
            return m.invoke( this.obj, params );
        }
        catch( Exception e )
        {
            throw new MBeanException( e );
        }
    }

    /**
     * Provides the exposed attributes and actions of the Dynamic MBean using an MBeanInfo object.
     *
     * @return  An instance of <CODE>MBeanInfo</CODE> allowing all attributes and actions
     * exposed by this Dynamic MBean to be retrieved.
     */
    public MBeanInfo getMBeanInfo()
    {
        return this.mBeanInfo;
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    /**
     * Creates an MBeanConstructorInfo array with an entry for each of the
     * supplied constructors.
     */
    protected MBeanConstructorInfo[] createConstructorInfo( Constructor[] constructors )
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
     * method that is an operation. A method is an operation when it is not
     * named getXxxx, setXxxx or isXxxx.
     */
    protected MBeanOperationInfo[] createOperationInfo( Method[] methods )
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
     * Extract all the Methods from the supplied array that are
     * operations.
     */
    protected Method[] getOperationFor( Method[] methods )
    {
        final ArrayList ops = new ArrayList();
        for( int i = 0; i < methods.length; i++ )
        {
            final String name = methods[i].getName();
            final String uppercaseChars = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            final boolean isGetter =
                ( name.startsWith("get") &&
                  ( uppercaseChars.indexOf( name.substring(3,4) ) != -1 ) );
            final boolean isSetter =
                ( name.startsWith("set") &&
                  ( uppercaseChars.indexOf( name.substring(3,4) ) != -1 ) );
            final boolean isIs =
                ( name.startsWith("is") &&
                  ( uppercaseChars.indexOf( name.substring(2,3) ) != -1 ) );
            if( !(isGetter || isSetter || isIs) )
            {
                ops.add(methods[i]);
            }
        }
        return (Method[])ops.toArray();
    }
    /**
     * Creates an MBeanAttributeInfo array with an entry for every attribute.
     * The exposed attributes are found by taking a look at all methods that
     * are named getXxx and isXxx. While this method finds the corresponding
     * setXxx method for these, it does not add an entry into the returned
     * array for write-only attributes.
     *
     * TODO:
     * @throws NotCompliantMBeanException when the getXxx, isXxx or setXxx don't have the right number of arguments (0, 0 and 1, respectively).
     */
    protected MBeanAttributeInfo[] createAttributeInfo( Method[] methods )
        throws NotCompliantMBeanException
    {
        // only getXXX, setXXX and isXXX expose attributes, so remove
        // all others
        final Method[][] m = getAttributeFor( methods );

        // all getter and isser methods mean an attribute
        final MBeanAttributeInfo[] info = new MBeanAttributeInfo[(m[0].length+m[2].length)];
        // fill info from this point
        int beginIndex = 0;

        // create MBeanAttributeInfo's for all getter methods
        for( int i = 0; i < m[0].length; i++ )
        {
            final Method getter = m[0][i];
            String attributeName = getter.getName().substring(3);
            Method setter = null;
            for( int j = 0; j < m[1].length; j++ )
            {
                String setterName = m[1][j].getName();
                if( setterName.equals( "set"+attributeName ) )
                {
                    setter = m[0][j];
                    break;
                }
            }
            attributeName = attributeName.substring(0,1).toLowerCase() + attributeName.substring(1);
            try {
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
            final String attributeName = isser.getName().substring(2);
            Method setter = null;
            for( int j = 0; j < m[1].length; j++ )
            {
                String setterName = m[1][j].getName();
                if( setterName.equals( "set"+attributeName ) )
                {
                    setter = m[0][j];
                    break;
                }
            }
            try
            {
            info[beginIndex] = new MBeanAttributeInfo( attributeName, "", isser, setter );
            }
            catch( IntrospectionException ie )
            {
                throw new NotCompliantMBeanException();
            }
            beginIndex++;
        }
        return info;
    }
    /**
     * Extract all Methods from the supplied array that are of
     * the form getXxx, setXxx or isXxx.
     *
     * @return A multi-dimensional array, with the first element being an array of getter methods, the second element being an array of setter methods, and the third element being an array of isser methods.
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
            final String uppercaseChars = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            final boolean isGetter =
                ( name.startsWith("get") &&
                  ( uppercaseChars.indexOf( name.substring(3,4) ) != -1 ) );
            final boolean isSetter =
                ( name.startsWith("set") &&
                  ( uppercaseChars.indexOf( name.substring(3,4) ) != -1 ) );
            final boolean isIs =
                ( name.startsWith("is") &&
                  ( uppercaseChars.indexOf( name.substring(2,3) ) != -1 ) );
            if( isGetter )
                getters.add(methods[i]);
            if( isSetter )
                setters.add(methods[i]);
            if( isIs )
                issers.add(methods[i]);
        }
        m[0] = (Method[])getters.toArray();
        m[1] = (Method[])setters.toArray();
        m[2] = (Method[])issers.toArray();
        return m;
    }
    protected void createMBeanInfo()
    {
        this.mBeanInfo = new MBeanInfo(
            this.obj.getClass().getName(),
            this.description,
            this.attributes,
            this.constructors,
            this.operations,
            this.notifications );
    }
 }
