/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor.test;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.excalibur.monitor.FileResource;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

class MonitorTestCaseListener
    extends AbstractLogEnabled
    implements PropertyChangeListener
{
    private volatile boolean m_hasChanged = false;

    public boolean hasBeenModified()
    {
        return m_hasChanged;
    }

    public void reset()
    {
        m_hasChanged = false;
    }

    public void propertyChange( final PropertyChangeEvent propertyChangeEvent )
    {
        m_hasChanged = true;

        if( getLogger().isInfoEnabled() )
        {
            getLogger().info( "NOTIFICATION LATENCY: " + ( System.currentTimeMillis() -
                                                           ( (Long)propertyChangeEvent.getNewValue() ).longValue() ) +
                              "ms" );
            getLogger().info( "Received notification for " +
                              ( (FileResource)propertyChangeEvent.getSource() ).getResourceKey() );
            getLogger().info( propertyChangeEvent.getPropertyName() +
                              "\n  IS::" + (Long)propertyChangeEvent.getNewValue() +
                              "\n  WAS::" + (Long)propertyChangeEvent.getOldValue() +
                              "\n  TIME SINCE LAST MOD::" +
                              ( ( (Long)propertyChangeEvent.getNewValue() ).longValue() -
                                ( (Long)propertyChangeEvent.getOldValue() ).longValue() ) );
        }
    }
}
