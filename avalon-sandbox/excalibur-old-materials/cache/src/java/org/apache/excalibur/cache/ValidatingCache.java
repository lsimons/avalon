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

/**
 * Validating cache proxy.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @author <a href="mailto:anryoshi@user.sourceforge.net">Antti Koivunen"</a>
 */
public final class ValidatingCache
    implements Cache
{
    private Cache m_cache;
    private CacheValidator m_validator;

    /**
     * Cache with validator.
     *
     * @param cache
     * @param validator object validator
     */
    public ValidatingCache( final Cache cache, final CacheValidator validator )
    {
        m_cache = cache;
        m_validator = validator;
    }

    /**
     * Validate cached item.
     *
     * @param key the key of cached item
     * @param value the value of cached item
     * @return true if cached item is valid otherwise false
     */
    private boolean validate( final Object key, final Object value )
    {
        if( null == m_validator )
        {
            return true;
        }
        else
        {
            return m_validator.validate( key, value );
        }
    }

    public void addListener( final CacheListener listener )
    {
        m_cache.addListener( listener );
    }

    public void removeListener( final CacheListener listener )
    {
        m_cache.removeListener( listener );
    }

    public int capacity()
    {
        return m_cache.capacity();
    }

    public int size()
    {
        return m_cache.size();
    }

    public Object put( final Object key, final Object value )
    {
        return m_cache.put( key, value );
    }

    public Object get( final Object key )
    {
        Object value = m_cache.get( key );

        if( null != value && !validate( key, value ) )
        {
            remove( key );
            value = null;
        }

        return value;
    }

    public Object remove( final Object key )
    {
        return m_cache.remove( key );
    }

    public void clear()
    {
        m_cache.clear();
    }
}
