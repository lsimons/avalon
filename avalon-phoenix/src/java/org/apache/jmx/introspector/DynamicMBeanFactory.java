/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanInfo;
import javax.management.NotificationBroadcaster;

/**
 * This class uses introspection to create DynamicMBeans for
 * any java object. It exposes all public methods. If you wish
 * to provide human-readable information about the exposed
 * properties and methods, you can provide your own MBeanInfo
 * object.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class DynamicMBeanFactory
{
    /**
     * All DynamicMBeanFactory methods are static.
     * It is unneccessary to create instances.
     */
    private DynamicMBeanFactory()
    {
    }
    /**
     * Get a <code>DynamicMBean</code> that represents the
     * supplied object.
     *
     * @exception NotCompliantMBeanException if the supplied
     * object does not follow the rules for DynamicMBeans.
     */
    public static DynamicMBean create( Object obj ) throws NotCompliantMBeanException
    {
        if( obj instanceof NotificationBroadcaster )
        {
            return new DefaultDynamicNotificationMBean( (NotificationBroadcaster)obj );
        }
        else
        {
            return new DefaultDynamicMBean( obj );
        }
    }
    /**
     * Get a <code>DynamicMBean</code> that represents the
     * supplied object, using the supplied <code>MBeanInfo</code>
     * object.
     *
     * @exception IllegalArgumentException if the supplied object
     * does not implement the class specified by the supplied
     * MBeanInfo's getClassName() method.
     * @exception NotCompliantMBeanException if the supplied
     * object does not follow the rules for DynamicMBeans.
     */
    public static DynamicMBean create( Object obj, MBeanInfo mBeanInfo )
        throws IllegalArgumentException, NotCompliantMBeanException
    {
        if( obj instanceof NotificationBroadcaster )
        {
            return new DefaultDynamicNotificationMBean( (NotificationBroadcaster)obj, mBeanInfo );
        }
        else
        {
            return new DefaultDynamicMBean( obj, mBeanInfo );
        }
    }

    /**
     * Get a <code>DynamicMBean</code> that represents the
     * supplied object, exposing only the methods and properties
     * specified in the provided interfaces.
     *
     * @exception IllegalArgumentException if the supplied object
     * does not implement all the interfaces supplied, or if one
     * of the supplied classes isn't an interface.
     * @exception NotCompliantMBeanException if the supplied
     * object does not follow the rules for DynamicMBeans.
     */
    public static DynamicMBean create( Object obj, Class[] interfaces )
        throws IllegalArgumentException, NotCompliantMBeanException
    {
        if( obj instanceof NotificationBroadcaster )
        {
            return new DefaultDynamicNotificationMBean( (NotificationBroadcaster)obj, interfaces );
        }
        else
        {
            return new DefaultDynamicMBean( obj, interfaces );
        }
    }
}
