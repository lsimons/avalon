/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;

/**
 * This class uses introspection to create DynamicMBeans for any java object. It
 * exposes all public methods. If you wish to provide human-readable information
 * about the exposed properties and methods, you can provide your own MBeanInfo
 * object.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/24 05:00:53 $
 */

public class DynamicMBeanFactory
{
    /**
     * All DynamicMBeanFactory methods are static. It is unneccessary to create
     * instances.
     */
    private DynamicMBeanFactory()
    {
    }

    /**
     * Get a <code>DynamicMBean</code> that represents the supplied object.
     *
     * @param obj DOC: Insert Description of Parameter
     * @return DOC: Insert Description of the Returned Value
     * @exception NotCompliantMBeanException DOC: Insert Description of
     *      Exception
     */
    public static DynamicMBean create( final Object object )
        throws NotCompliantMBeanException
    {
        if( object instanceof NotificationBroadcaster )
        {
            return new DefaultDynamicNotificationMBean( (NotificationBroadcaster)object );
        }
        else
        {
            return new DefaultDynamicMBean( object );
        }
    }

    /**
     * Get a <code>DynamicMBean</code> that represents the supplied object,
     * using the supplied <code>MBeanInfo</code> object.
     *
     * @exception IllegalArgumentException if the supplied object does not
     *      implement the class specified by the supplied MBeanInfo's
     *      getClassName() method.
     */
    public static DynamicMBean create( final Object object, final MBeanInfo mBeanInfo )
        throws IllegalArgumentException, NotCompliantMBeanException
    {
        if( object instanceof NotificationBroadcaster )
        {
            return new DefaultDynamicNotificationMBean( (NotificationBroadcaster)object, mBeanInfo );
        }
        else
        {
            return new DefaultDynamicMBean( object, mBeanInfo );
        }
    }
}
