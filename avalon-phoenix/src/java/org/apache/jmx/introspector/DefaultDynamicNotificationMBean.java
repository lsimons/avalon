/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.jmx.introspector;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 * This class is used by DynamicMBeanFactory to create DynamicMBeans. It extends
 * DefaultDynamicMBean, adding support for notifications.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/24 05:00:53 $
 */
public class DefaultDynamicNotificationMBean
    extends DefaultDynamicMBean
    implements NotificationBroadcaster
{
    public DefaultDynamicNotificationMBean( final NotificationBroadcaster object )
        throws NotCompliantMBeanException
    {
        super( object );

        m_notifications = object.getNotificationInfo();
        createMBeanInfo();
    }

    public DefaultDynamicNotificationMBean( final NotificationBroadcaster object,
                                            final MBeanInfo mBeanInfo )
        throws NotCompliantMBeanException
    {
        super( object, mBeanInfo );
        m_notifications = object.getNotificationInfo();
        createMBeanInfo();
    }

    public MBeanNotificationInfo[] getNotificationInfo()
    {
        return m_notifications;
    }

    /////////////////////////////////////////
    /// NOTIFICATIONBROADCASTER INTERFACE ///
    /////////////////////////////////////////
    public void addNotificationListener( final NotificationListener listener,
                                         final NotificationFilter filter,
                                         final Object handback )
        throws java.lang.IllegalArgumentException
    {
        ((NotificationBroadcaster)m_object ).addNotificationListener( listener, filter, handback );
    }

    public void removeNotificationListener( final NotificationListener listener )
        throws ListenerNotFoundException
    {
        ((NotificationBroadcaster)m_object).removeNotificationListener( listener );
    }
}
