/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.MBeanNotificationInfo;


/**
 * This class is used by DynamicMBeanFactory to create DynamicMBeans.
 * It extends DefaultDynamicMBean, adding support for notifications.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class DefaultDynamicNotificationMBean
    extends DefaultDynamicMBean implements NotificationBroadcaster
{
    public DefaultDynamicNotificationMBean( NotificationBroadcaster obj )
        throws NotCompliantMBeanException
    {
        super( obj );

        this.notifications = obj.getNotificationInfo();
        this.createMBeanInfo();
    }
    public DefaultDynamicNotificationMBean( NotificationBroadcaster obj, MBeanInfo mBeanInfo )
        throws NotCompliantMBeanException
    {
        super( obj, mBeanInfo );
        this.notifications = obj.getNotificationInfo();
        this.createMBeanInfo();
    }

    /////////////////////////////////////////
    /// NOTIFICATIONBROADCASTER INTERFACE ///
    /////////////////////////////////////////
    public void addNotificationListener( NotificationListener listener, NotificationFilter filter, Object handback )
        throws java.lang.IllegalArgumentException
    {
        ((NotificationBroadcaster)this.obj).addNotificationListener( listener, filter, handback );
    }
    public void removeNotificationListener( NotificationListener listener )
        throws ListenerNotFoundException
    {
        ((NotificationBroadcaster)this.obj).removeNotificationListener( listener );
    }
    public MBeanNotificationInfo[] getNotificationInfo()
    {
        return this.notifications;
    }

}