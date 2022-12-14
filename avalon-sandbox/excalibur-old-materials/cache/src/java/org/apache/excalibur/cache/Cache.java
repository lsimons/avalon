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
package org.apache.excalibur.cache;

import org.apache.avalon.framework.component.Component;

/**
 * This is a cache that caches objects for reuse.
 * Key and value are must not be <code>null</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface Cache
    extends Component
{
    /**
     * Add listener.
     *
     * @param listener listener instance to add
     */
    void addListener( CacheListener listener );

    /**
     * Remove listener.
     *
     * @param listener listener instance to remove
     */
    void removeListener( CacheListener listener );

    /**
     * Return capacity of cache.
     *
     * @return capacity of cache
     */
    int capacity();

    /**
     * Return size of cache.
     *
     * @return the number of key-value mappings in this cache
     */
    int size();

    /**
     * Puts a new item in the cache. If the cache is full, remove the selected item.
     *
     * @param key key for the item
     * @param value item
     * @return old value. null if old value not exists
     */
    Object put( Object key, Object value );

    /**
     * Get an item from the cache.
     *
     * @param key key to lookup the item
     * @return the matching object in the cache. null if item not exists
     */
    Object get( Object key );

    /**
     * Removes an item from the cache.
     *
     * @param key key to remove
     * @return the value removed. null if old value not exists
     */
    Object remove( Object key );

    /**
     * Clear cache.
     */
    void clear();
}
