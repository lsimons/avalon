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
package org.apache.excalibur.cache.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:lawrence_mccay-iii@hp.com">Larry McCay</a>
 */
public class FlipSpacesStore
    extends AbstractCacheStore
{
    /**
     * the data space which stores the most recently accessed objects
     */
    private Map m_newCache = null;

    /**
     * the data space which stores accessed items which have not been accessed
     * since the last space swap. At the time <code>copySpaces</code> is called,
     * objects still stored within this space are removed from the cache.
     */
    private Map m_oldCache = null;

    /**
     * the size at which the cache is deemed to be full
     */
    private int m_capacity;

    /**
     * Sets up the data spaces and sets the capacity of the cache
     *
     * @param capacity - the size at which the cache is deemed full
     */
    public FlipSpacesStore( final int capacity )
    {
        if( 1 > capacity )
        {
            throw new IllegalArgumentException( "Specified capacity must be at least 1" );
        }

        m_capacity = capacity;
        m_newCache = new HashMap( m_capacity );
        m_oldCache = new HashMap( m_capacity );
    }

    /**
     * Puts a given name value pair into the newCache.
     * By invoking a get for the Object associated with the name before doing
     * the actual put - we insure that the name value pair lives in the newCache
     * data space.  After executing the put - we determine if the cache is full
     * - if so swap the data spaces - effectively clearing the newCache.
     *
     * @param key - name or key of the Object to be cached
     * @param value - the actual cached Object
     * @return the Object previously associated with the given name or key
     */
    public Object put( final Object key, final Object value )
    {
        Object old = null;
        get( key );
        old = m_newCache.put( key, value );
        if( isFull() ) // cache full?
        {
            copySpaces();
        }
        return old;
    }

    /**
     * Removes the Object associated with the given name from the both spaces of
     * this cache store. By doing a get before removing the object we insure
     * that the object if in the cache has been moved to the newCache
     *
     * @param key - name or key associated with the Object to be removed
     * @return the removed Object
     */
    public Object remove( final Object key )
    {
        Object cr = get( key );

        return m_newCache.remove( key );
    }

    /**
     * Gets the cached object associated with the given name.
     * If the object does not exist within the newCache the old is checked.
     * If the cache is determined to be full the spaces are swapped
     * - effectively clearing the newCache.  The object is then put into the newCache.
     *
     * @param key - the name or key of the requested object
     * @return the requested Object
     */
    public Object get( final Object key )
    {
        Object value = null;
        if( m_newCache.containsKey( key ) )
        {
            value = m_newCache.get( key ); // try new space
        }
        else
        {
            if( m_oldCache.containsKey( key ) )
            {
                value = m_oldCache.get( key ); // try old space
                if( isFull() ) // cache full?
                {
                    copySpaces();
                }
                m_oldCache.remove( key ); // remove from old space
                m_newCache.put( key, value ); // move to new space
            }
        }
        return value;
    }

    /**
     * Erase the oldCache - releasing those objects that are still considered
     * old by the time the newCache has been determined to be full.
     * Move the newCache to old and the previously oldCache to newCache
     * effectively clearing it. Over time accessing objects will move them from
     * the oldCache to the newCache leaving those objects behind that shall be
     * cleared as the newCache is determined to be full again.
     */
    private void copySpaces()
    {
        m_oldCache.clear(); // erase old space
        final Map temp = m_oldCache; // flip spaces
        m_oldCache = m_newCache;
        m_newCache = temp;
    }

    /**
     * Gets the current size of the newCache.
     * @return newCacheSize
     */
    public int size()
    {
        return m_newCache.size();
    }

    /**
     * Gets the capacity for the cache.
     * Once the cache size has reached the capacity it is considered full.
     *
     * @return cache capacity
     */
    public int capacity()
    {
        return m_capacity;
    }

    /**
     * Checks if a given key exists within either of the spaces - old and new Caches.
     *
     * @return true if the key exists within this cache
     */
    public boolean containsKey( final Object key )
    {
        boolean rc = m_newCache.containsKey( key );
        if( !rc )
        {
            rc = m_oldCache.containsKey( key );
        }
        return rc;
    }

    /**
     * Gets array of keys from both caches or spaces.
     *
     * @return array of all the keys within this cache
     */
    public Object[] keys()
    {
        final Set newKeys = m_newCache.keySet();
        final Set oldKeys = m_oldCache.keySet();
        final ArrayList keys = new ArrayList( newKeys );
        keys.addAll( oldKeys );
        return keys.toArray();
    }
}