/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Managed Resource.  All resources must have a constructor that takes a String
 * and converts it to the needed format (i.e. File).  A Managed Resource in the
 * Monitor section has only one property needed to be changed: last modified.
 * The property name for the last modified event will be the same as the resource
 * key.  Implementations may add additional properties, but for most instances the
 * last modified property will be enough.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: Resource.java,v 1.8 2002/05/13 12:17:40 donaldp Exp $
 */
public abstract class Resource
    implements Modifiable
{
    protected static final String MODIFIED = "last-modified";
    protected PropertyChangeSupport m_eventSupport = new PropertyChangeSupport( this );
    private final String m_resourceKey;
    protected long m_previousModified = 0L;
    private static final Set m_propertyListeners = Collections.synchronizedSet( new HashSet() );

    /**
     * Required constructor.  The <code>String</code> location is transformed by
     * the specific resource monitor.  For instance, a FileResource will be able
     * to convert a string representation of a path to the proper File object.
     */
    public Resource( String location ) throws Exception
    {
        m_resourceKey = location;
    }

    /**
     * Return the key for the resource.
     */
    public final String getResourceKey()
    {
        return m_resourceKey;
    }

    /**
     * The time this was last modified.
     */
    public abstract long lastModified();

    /**
     * Test whether this has been modified since time X
     */
    public void testModifiedAfter( long time )
    {
        long lastModified = this.lastModified();

        if( lastModified > m_previousModified || lastModified > time )
        {
            m_eventSupport.firePropertyChange( Resource.MODIFIED,
                                               new Long( m_previousModified ),
                                               new Long( lastModified ) );
            m_previousModified = lastModified;
        }
    }

    /**
     * Abstract method to add the PropertyChangeListeners in another Resource to
     * this one.
     */
    protected void addPropertyChangeListenersFrom( Resource other )
    {
        PropertyChangeListener[] listeners = (PropertyChangeListener[])
            other.m_propertyListeners.toArray( new PropertyChangeListener[]{} );

        for( int i = 0; i < listeners.length; i++ )
        {
            this.addPropertyChangeListener( listeners[ i ] );
        }
    }

    /**
     * This is the prefered method of registering a <code>PropertyChangeListender</code>.
     * It automatically registers the listener for the last modified event.
     */
    public final void addPropertyChangeListener( PropertyChangeListener listener )
    {
        m_eventSupport.addPropertyChangeListener( listener );
        m_propertyListeners.add( listener );
    }

    /**
     * This is a convenience if you want to expose other properties for the Resource.
     * It is protected now, but you may override it with public access later.
     */
    protected void addPropertyChangeListener( String property, PropertyChangeListener listener )
    {
        m_eventSupport.addPropertyChangeListener( property, listener );
        m_propertyListeners.add( listener );
    }

    /**
     * This is the prefered method of unregistering a <code>PropertyChangeListender</code>.
     * It automatically registers the listener for the last modified event.
     */
    public final void removePropertyChangeListener( PropertyChangeListener listener )
    {
        m_eventSupport.removePropertyChangeListener( listener );
        m_propertyListeners.remove( listener );
    }

    /**
     * This is a convenience if you want to expose other properties for the Resource.
     * It is protected now, but you may override it with public access later.
     */
    protected void removePropertyChangeListener( String property, PropertyChangeListener listener )
    {
        m_eventSupport.removePropertyChangeListener( property, listener );
        m_propertyListeners.remove( listener );
    }

    /**
     * This is the preferred method of determining if a Resource has listeners.
     */
    public final boolean hasListeners()
    {
        return m_eventSupport.hasListeners( this.getResourceKey() );
    }

    /**
     * This cleanup method removes all listeners
     */
    protected void removeAllPropertyChangeListeners()
    {
        PropertyChangeListener[] listeners = (PropertyChangeListener[])
            m_propertyListeners.toArray( new PropertyChangeListener[]{} );

        for( int i = 0; i < listeners.length; i++ )
        {
            this.removePropertyChangeListener( listeners[ i ] );
        }
    }

    /**
     * This is a convenience if you want to expose other properties for the Resource.
     * It is protected now, but you may override it with public access later.
     */
    protected boolean hasListeners( String property )
    {
        return m_eventSupport.hasListeners( property );
    }
}
