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

import org.apache.excalibur.cache.CacheStore;
import org.apache.excalibur.cache.ReplacementPolicy;

/**
 * Default <code>Cache</code> implementation.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class DefaultCache
    extends AbstractCache
{
    private ReplacementPolicy m_policy;
    private CacheStore m_store;

    public DefaultCache( final ReplacementPolicy policy,
                         final CacheStore store )
    {
        m_policy = policy;
        m_store = store;
    }

    public int capacity()
    {
        return m_store.capacity();
    }

    public int size()
    {
        return m_store.size();
    }

    public Object put( final Object key, final Object value )
    {
        if ( null == key )
        {
            throw new NullPointerException( "Attempted to put null key to cache" );
        }
        if ( null == value )
        {
            throw new NullPointerException( "Attempted to put null value to cache" );
        }

        final Object oldValue = remove( key );

        if( m_store.isFull() )
        {
            remove( m_policy.selectVictim() );
        }

        m_store.put( key, value );
        m_policy.add( key );
        notifyAdded( key, value );

        return oldValue;
    }

    public Object get( final Object key )
    {
        if ( null == key )
        {
            throw new NullPointerException( "Attempted to put null key to cache" );
        }

        final Object value = m_store.get( key );
        m_policy.hit( key );

        return value;
    }

    public Object remove( final Object key )
    {
        if ( null == key )
        {
            throw new NullPointerException( "Attempted to put null key to cache" );
        }

        Object value = null;
        if( m_store.containsKey( key ) )
        {
            value = m_store.remove( key );
            m_policy.remove( key );
            notifyRemoved( key, value );
        }

        return value;
    }

    public void clear()
    {
        final Object[] keys = m_store.keys();
        for( int i = 0; i < keys.length; i++ )
        {
            remove( keys[ i ] );
        }
    }
}
