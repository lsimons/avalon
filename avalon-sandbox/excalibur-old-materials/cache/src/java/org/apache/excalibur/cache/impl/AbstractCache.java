/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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
package org.apache.excalibur.cache.impl;

import java.util.ArrayList;

import org.apache.excalibur.cache.Cache;
import org.apache.excalibur.cache.CacheEvent;
import org.apache.excalibur.cache.CacheListener;

/**
 * An abstract superclass for cache implementations.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @author <a href="mailto:anryoshi@users.sf.net">Antti Koivunen</a>
 */
public abstract class AbstractCache
    implements Cache
{
    private ArrayList m_listeners;

    public AbstractCache()
    {
        m_listeners = new ArrayList();
    }

    public void addListener( final CacheListener listener )
    {
        m_listeners.add( listener );
    }

    public void removeListener( final CacheListener listener )
    {
        m_listeners.remove( listener );
    }

    protected void notifyAdded( final Object key, final Object value )
    {
        CacheEvent event = null;

        final int s = m_listeners.size();
        if( 0 < s )
        {
            event = new CacheEvent( this, key, value );
        }
        for( int i = 0; i < s; i++ )
        {
            ( (CacheListener)m_listeners.get( i ) ).added( event );
        }
    }

    protected void notifyRemoved( final Object key, final Object value )
    {
        CacheEvent event = null;

        final int s = m_listeners.size();
        if( 0 < s )
        {
            event = new CacheEvent( this, key, value );
        }
        for( int i = 0; i < s; i++ )
        {
            ( (CacheListener)m_listeners.get( i ) ).removed( event );
        }
    }
}
