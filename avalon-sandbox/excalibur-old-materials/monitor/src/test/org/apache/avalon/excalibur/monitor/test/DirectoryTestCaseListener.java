/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;
import org.apache.avalon.excalibur.monitor.DirectoryResource;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

class DirectoryTestCaseListener
    extends AbstractLogEnabled
    implements PropertyChangeListener
{
    private Set m_added = Collections.EMPTY_SET;
    private Set m_removed = Collections.EMPTY_SET;
    private Set m_modified = Collections.EMPTY_SET;

    void reset()
    {
        m_added = Collections.EMPTY_SET;
        m_removed = Collections.EMPTY_SET;
        m_modified = Collections.EMPTY_SET;
    }

    public Set getAdded()
    {
        return m_added;
    }

    public Set getRemoved()
    {
        return m_removed;
    }

    public Set getModified()
    {
        return m_modified;
    }

    public void propertyChange( final PropertyChangeEvent event )
    {
        final String name = event.getPropertyName();
        final Set newValue = (Set)event.getNewValue();
        if( name.equals( DirectoryResource.ADDED ) )
        {
            m_added = newValue;
        }
        else if( name.equals( DirectoryResource.REMOVED ) )
        {
            m_removed = newValue;
        }
        else
        {
            m_modified = newValue;
        }
    }
}
