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
package org.apache.avalon.excalibur.monitor.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.excalibur.monitor.Monitor;
import org.apache.avalon.excalibur.monitor.Resource;

/**
 * The AbstractMonitor class is a useful base class which all Monitors
 * can extend. The particular monitoring policy is defined by the particular
 * implementation.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Id: AbstractMonitor.java,v 1.10 2003/03/22 12:46:50 leosimons Exp $
 */
public abstract class AbstractMonitor
    implements Monitor
{
    /**
     * The set of resources that the monitor is monitoring.
     */
    private Map m_resources = new HashMap();

    /**
     * Add an array of resources to monitor.
     *
     * @param resources the resources to monitor
     */
    public final void addResources( final Resource[] resources )
    {
        for( int i = 0; i < resources.length; i++ )
        {
            addResource( resources[ i ] );
        }
    }

    /**
     * Add a resource to monitor.  The resource key referenced in the other
     * interfaces is derived from the resource object.
     */
    public final void addResource( final Resource resource )
    {
        synchronized( m_resources )
        {
            final String resourceKey = resource.getResourceKey();
            if( m_resources.containsKey( resourceKey ) )
            {
                final Resource original =
                    (Resource)m_resources.get( resourceKey );
                original.addPropertyChangeListenersFrom( resource );
            }
            else
            {
                m_resources.put( resourceKey, resource );
            }
        }
    }

    /**
     * Find a monitored resource.  If no resource is available, return null
     */
    public Resource getResource( final String key )
    {
        synchronized( m_resources )
        {
            return (Resource)m_resources.get( key );
        }
    }

    /**
     * Remove a monitored resource by key.
     */
    public final void removeResource( final String key )
    {
        synchronized( m_resources )
        {
            final Resource resource =
                (Resource)m_resources.remove( key );
            resource.removeAllPropertyChangeListeners();
        }
    }

    /**
     * Remove a monitored resource by reference.
     */
    public final void removeResource( final Resource resource )
    {
        removeResource( resource.getResourceKey() );
    }

    /**
     * Return an array containing all the resources currently monitored.
     *
     * @return an array containing all the resources currently monitored.
     */
    protected Resource[] getResources()
    {
        final Collection collection = m_resources.values();
        return (Resource[])collection.toArray( new Resource[ collection.size() ] );
    }

    /**
     * Scan through all resources to determine if they have changed.
     */
    protected void scanAllResources()
    {
        final long currentTestTime = System.currentTimeMillis();
        final Resource[] resources = getResources();
        for( int i = 0; i < resources.length; i++ )
        {
            resources[ i ].testModifiedAfter( currentTestTime );
        }
    }
}
