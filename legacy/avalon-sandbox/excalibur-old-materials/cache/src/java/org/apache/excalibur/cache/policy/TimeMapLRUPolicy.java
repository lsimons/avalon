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
package org.apache.excalibur.cache.policy;

import java.util.HashMap;
import java.util.TreeMap;

import org.apache.excalibur.cache.ReplacementPolicy;

/**
 * TimeMapLRU(Least Recently Used) replacement policy.
 * Use a TreeMap with logical time to perform LRU <code>selectVictim</code> operations.
 * On large cache this implementation should be really faster (since it uses a
 * log(n) treemap plus an hashmap) than the current LRU implem that is working with a List.
 *
 * @author <a href="alag@users.sourceforge.net">Alexis Agahi</a>
 */
public class TimeMapLRUPolicy
    implements ReplacementPolicy
{
    private TreeMap m_timeToKeyMap;
    private HashMap m_keyToTimeMap;

    private long m_time;
    private Object m_lock;

    public TimeMapLRUPolicy()
    {
        m_timeToKeyMap = new TreeMap();
        m_keyToTimeMap = new HashMap();
        m_time = 0;
        m_lock = new Object();
    }

    public void hit( final Object key )
    {
        remove( key );
        add( key );
    }

    public void add( final Object key )
    {
        Long time = getTime();
        m_timeToKeyMap.put( time, key );
        m_keyToTimeMap.put( key, time );
    }

    public void remove( final Object key )
    {
        Long time = (Long)m_keyToTimeMap.remove( key );
        if( null != time )
        {
            m_timeToKeyMap.remove( time );
        }
    }

    public Object selectVictim()
    {
        Object time = m_timeToKeyMap.firstKey();
        Object key = m_timeToKeyMap.get( time );
        return key;
    }

    private Long getTime()
    {
        synchronized( m_lock )
        {
            return new Long( m_time++ );
        }
    }
}
