/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.fortress.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <code>CachedArrayList</code> class.
 *
 * <p>
 * This class wraps a synchronized ArrayList to provide an optimized
 * <code>toArray()</code> method that returns an internally cached array,
 * rather than a new array generated per <code>toArray()</code>
 * invocation.
 * </p>
 *
 * <p>
 * Use of the class by the Manager results in <code>toArray()</code>
 * being invoked far more often than any other method. Caching the value
 * <code>toArray</code> normally returns is intended to be a performance
 * optimization.
 * </p>
 *
 * <p>
 * The cached array value is updated upon each write operation to the
 * List.
 * </p>
 *
 * <p>
 * @todo(MC) investigate using FastArrayList from collections ?
 * </p>
 */
public final class CachedArrayList
{
    // Empty array constant
    private final Object[] EMPTY_ARRAY = new Object[ 0 ];

    // Actual list for storing elements
    private final List m_proxy = Collections.synchronizedList( new java.util.ArrayList() );

    // Proxy cache, saves unnecessary conversions from List to Array
    private Object[] m_cache = EMPTY_ARRAY;

    /**
     * Add an object to the list
     *
     * @param object an <code>Object</code> value
     */
    public void add( final Object object )
    {
        m_proxy.add( object );
        m_cache = m_proxy.toArray();
    }

    /**
     * Insert an object into a particular position in the list
     *
     * @param position an <code>int</code> value
     * @param object an <code>Object</code> value
     */
    public void insert( final int position, final Object object )
    {
        m_proxy.add( position, object );
        m_cache = m_proxy.toArray();
    }

    /**
     * Remove an object from the list
     *
     * @param position an <code>int</code> value
     * @return a <code>Object</code> value
     */
    public Object remove( final int position )
    {
        final Object object = m_proxy.remove( position );
        m_cache = m_proxy.toArray();
        return object;
    }

    /**
     * Obtain an iterator
     *
     * @return an <code>Iterator</code> value
     */
    public Iterator iterator()
    {
        return m_proxy.iterator();
    }

    /**
     * Obtain the size of the list
     *
     * @return an <code>int</code> value
     */
    public int size()
    {
        return m_proxy.size();
    }

    /**
     * Access an object that is in the list
     *
     * @param index an <code>int</code> value
     * @return a <code>Object</code> value
     */
    public Object get( final int index )
    {
        return m_proxy.get( index );
    }

    /**
     * Find out the index of an object in the list
     *
     * @param object an <code>Object</code> value
     * @return an <code>int</code> value
     */
    public int indexOf( final Object object )
    {
        return m_proxy.indexOf( object );
    }

    /**
     * Clear the list
     */
    public void clear()
    {
        m_proxy.clear();
        m_cache = EMPTY_ARRAY;
    }

    /**
     * Obtain the list as an array. Subsequents calls to this method
     * will return the same array object, until a write operation is
     * performed on the list.
     *
     * @return an <code>Object[]</code> value
     */
    public Object[] toArray()
    {
        return m_cache;
    }
}

