/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
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
 * @version $Id: Resource.java,v 1.20 2003/03/22 12:46:50 leosimons Exp $
 */
public abstract class Resource
    implements Modifiable
{
    protected static final String MODIFIED = "last-modified";

    /**
     * The set of listeners for this particular resource.
     */
    private final Set m_propertyListeners = Collections.synchronizedSet( new HashSet() );

    /**
     * @deprecated Developers should use the setter/getters
     *             associated with field.
     */
    protected PropertyChangeSupport m_eventSupport = new PropertyChangeSupport( this );

    /**
     * The resource key is the identifier of the resource.
     * ie A FileResource would have the filename as the resourceKey.
     */
    private final String m_resourceKey;

    /**
     * @deprecated Developers should use the setter/getters
     *             associated with field.
     */
    protected long m_previousModified = 0L;

    /**
     * Required constructor.  The {@link String} location is transformed by
     * the specific resource monitor.  For instance, a FileResource will be able
     * to convert a string representation of a path to the proper File object.
     */
    public Resource( final String resourceKey ) throws Exception
    {
        if( null == resourceKey )
        {
            throw new NullPointerException( "resourceKey" );
        }

        m_resourceKey = resourceKey;
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
    public void testModifiedAfter( final long time )
    {
        if( getPreviousModified() > time )
        {
            //The next line should be uncommented for complete
            //backward compatability. Unfortunately it does not
            //make sense to add it or else you could get multiple
            //notifications about a change.
            //fireAndSetModifiedTime( lastModified );
            return;
        }

        final long lastModified = lastModified();
        if( lastModified > getPreviousModified() || lastModified > time )
        {
            fireAndSetModifiedTime( lastModified );
        }
    }

    /**
     * Fire a modify event and set the lastModified time as appropraite.
     *
     * @param lastModified the time modified at
     */
    protected void fireAndSetModifiedTime( final long lastModified )
    {
        getEventSupport().firePropertyChange( Resource.MODIFIED,
                                              new Long( getPreviousModified() ),
                                              new Long( lastModified ) );
        setPreviousModified( lastModified );
    }

    /**
     * Abstract method to add the PropertyChangeListeners in another Resource to
     * this one.
     */
    public void addPropertyChangeListenersFrom( final Resource other )
    {
        PropertyChangeListener[] listeners = (PropertyChangeListener[])
            other.m_propertyListeners.toArray( new PropertyChangeListener[]{} );

        for( int i = 0; i < listeners.length; i++ )
        {
            addPropertyChangeListener( listeners[ i ] );
        }
    }

    /**
     * This is the prefered method of registering a {@link PropertyChangeListener}.
     * It automatically registers the listener for the last modified event.
     */
    public final void addPropertyChangeListener( final PropertyChangeListener listener )
    {
        getEventSupport().addPropertyChangeListener( listener );
        m_propertyListeners.add( listener );
    }

    /**
     * This is a convenience if you want to expose other properties for the Resource.
     * It is protected now, but you may override it with public access later.
     */
    protected void addPropertyChangeListener( final String property,
                                              final PropertyChangeListener listener )
    {
        getEventSupport().addPropertyChangeListener( property, listener );
        m_propertyListeners.add( listener );
    }

    /**
     * This is the prefered method of unregistering a {@link PropertyChangeListener}.
     * It automatically registers the listener for the last modified event.
     */
    public final void removePropertyChangeListener( final PropertyChangeListener listener )
    {
        getEventSupport().removePropertyChangeListener( listener );
        m_propertyListeners.remove( listener );
    }

    /**
     * This is a convenience if you want to expose other properties for the Resource.
     * It is protected now, but you may override it with public access later.
     */
    protected void removePropertyChangeListener( final String property,
                                                 final PropertyChangeListener listener )
    {
        getEventSupport().removePropertyChangeListener( property, listener );
        m_propertyListeners.remove( listener );
    }

    /**
     * This is the preferred method of determining if a Resource has listeners.
     */
    public final boolean hasListeners()
    {
        return getEventSupport().hasListeners( getResourceKey() );
    }

    /**
     * This cleanup method removes all listeners
     */
    public void removeAllPropertyChangeListeners()
    {
        PropertyChangeListener[] listeners = (PropertyChangeListener[])
            m_propertyListeners.toArray( new PropertyChangeListener[]{} );

        for( int i = 0; i < listeners.length; i++ )
        {
            removePropertyChangeListener( listeners[ i ] );
        }
    }

    /**
     * This is a convenience if you want to expose other properties for the Resource.
     * It is protected now, but you may override it with public access later.
     */
    protected boolean hasListeners( final String property )
    {
        return getEventSupport().hasListeners( property );
    }

    protected final long getPreviousModified()
    {
        return m_previousModified;
    }

    protected final void setPreviousModified( final long previousModified )
    {
        m_previousModified = previousModified;
    }

    protected final PropertyChangeSupport getEventSupport()
    {
        return m_eventSupport;
    }

    public String toString()
    {
        return m_resourceKey;
    }
}
