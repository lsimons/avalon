/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A BucketMap is an efficient thread-safe implementation of the
 * <code>java.util.Map</code>.  The map supports <code>get</code>,
 * <code>put</code>, and <code>contains</code> methods most efficiently.
 * The other methods are supported, but are ver inneficient compared to
 * other <code>java.util.Map</code> implementations.
 *
 * @deprecated use org.apache.commons.collections.StaticBucketMap instead
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:05 $
 * @since 4.0
 */
public final class BucketMap implements Map
{
    private static final int DEFAULT_BUCKETS = 256;
    private final Node[] m_buckets;
    private volatile int m_size = 0;

    /**
     * Initializes the map with the default number of buckets.
     */
    public BucketMap()
    {
        this( DEFAULT_BUCKETS );
    }

    /**
     * Initializes the map with a specified number of buckets.  The number
     * of buckets is never below 17, and is always an odd number (BucketMap
     * ensures this). The number of buckets is inversely proportional to the
     * chances for thread contention.  The fewer buckets, the more chances for
     * thread contention.  The more buckets the fewer chances for thread
     * contention.
     */
    public BucketMap( int numBuckets )
    {
        int size = Math.max( 17, numBuckets );

        m_buckets = new Node[size];

        for( int i = 0; i < size; i++ )
        {
            m_buckets[i] = new Node();
        }
    }

    /**
     * Determine the exact hash entry for the key.  The hash algorithm
     * is rather simplistic, but it does the job:
     *
     * <pre>
     *   He = |Hk mod n|
     * </pre>
     *
     * <p>
     *   He is the entry's hashCode, Hk is the key's hashCode, and n is
     *   the number of buckets.
     * </p>
     */
    private final int getHash( Object key )
    {
        int hash = key.hashCode();

        hash += ~(hash << 9);
        hash ^= (hash >>> 14);
        hash += (hash << 4);
        hash ^= (hash >>> 10);

        hash %= m_buckets.length;

        return (hash < 0) ? hash * -1 : hash;
    }

    /**
     * Obtain a Set for the keys.  This operation crosses bucket boundaries,
     * so it is less efficient, and greatly increases the chance for thread
     * contention.
     */
    public Set keySet()
    {
        Set keySet = new HashSet();

        for( int i = 0; i < m_buckets.length; i++ )
        {
            synchronized( m_buckets[i] )
            {
                Node n = m_buckets[i];

                while( n != null && n.key != null )
                {
                    keySet.add( n.key );
                    n = n.next;
                }
            }
        }

        return keySet;
    }

    /**
     * Returns the current number of key, value pairs.
     */
    public int size()
    {
        return m_size;
    }

    /**
     * Put a reference in the Map.
     */
    public Object put( final Object key, final Object value )
    {
        if( null == key || null == value )
        {
            return null;
        }

        int hash = getHash( key );

        synchronized( m_buckets[hash] )
        {
            Node n = m_buckets[hash];

            if( n.key == null )
            {
                n.key = key;
                n.value = value;
                m_size++;
                return value;
            }

            // Set n to the last node in the linked list.  Check each key along the way
            //  If the key is found, then change the value of that node and return
            //  the old value.
            for( Node next = n; next != null; next = next.next )
            {
                n = next;

                if( (n.key == key) || (n.key.equals( key )) )
                {
                    // do not adjust size because nothing was added
                    Object returnVal = n.value;
                    n.value = value;
                    return returnVal;
                }
            }

            // The key was not found in the current list of nodes, add it to the end
            //  in a new node.
            Node newNode = new Node();
            newNode.key = key;
            newNode.value = value;
            n.next = newNode;
            m_size++;
        }

        return null;
    }

    /**
     * Get an object from the Map by the key
     */
    public Object get( final Object key )
    {
        if( null == key )
        {
            return null;
        }

        int hash = getHash( key );

        synchronized( m_buckets[hash] )
        {
            Node n = m_buckets[hash];

            while( n != null && n.key != null )
            {
                if( (n.key == key) || (n.key.equals( key )) )
                {
                    return n.value;
                }

                n = n.next;
            }
        }

        return null;
    }

    /**
     * Checks to see if the provided key exists in the Map.
     */
    public boolean containsKey( final Object key )
    {
        if( null == key )
        {
            return false;
        }

        int hash = getHash( key );

        synchronized( m_buckets[hash] )
        {
            Node n = m_buckets[hash];

            while( n != null && n.key != null )
            {
                if( (n.key == key) || (n.key.equals( key )) )
                {
                    return true;
                }

                n = n.next;
            }
        }

        return false;
    }

    /**
     * Checks to see if a value exists.  This operation crosses bucket
     * boundaries, so it is less efficient, and greatly increases the chance
     * for thread contention.
     */
    public boolean containsValue( final Object value )
    {
        if( null == value )
        {
            return false;
        }

        for( int i = 0; i < m_buckets.length; i++ )
        {
            synchronized( m_buckets[i] )
            {
                Node n = m_buckets[i];

                while( n != null && n.key != null )
                {
                    if( (n.value == value) || (n.value.equals( value )) )
                    {
                        return true;
                    }

                    n = n.next;
                }
            }
        }

        return false;
    }

    /**
     * Obtain a Set for the values.  This operation crosses bucket boundaries,
     * so it is less efficient, and greatly increases the chance for thread
     * contention.
     */
    public Collection values()
    {
        Set valueSet = new HashSet();

        for( int i = 0; i < m_buckets.length; i++ )
        {
            synchronized( m_buckets[i] )
            {
                Node n = m_buckets[i];

                while( n != null && n.key != null )
                {
                    valueSet.add( n.value );
                    n = n.next;
                }
            }
        }

        return valueSet;
    }

    /**
     * Obtain a Set for the entries.  This operation crosses bucket boundaries,
     * so it is less efficient, and greatly increases the chance for thread
     * contention.
     */
    public Set entrySet()
    {
        Set entrySet = new HashSet();

        for( int i = 0; i < m_buckets.length; i++ )
        {
            synchronized( m_buckets[i] )
            {
                Node n = m_buckets[i];

                while( n != null && n.key != null )
                {
                    entrySet.add( n );
                    n = n.next;
                }
            }
        }

        return entrySet;
    }

    /**
     * Add all the contents of one Map into this one.
     */
    public void putAll( Map other )
    {
        Iterator i = other.keySet().iterator();

        while( i.hasNext() )
        {
            Object key = i.next();
            put( key, other.get( key ) );
        }
    }

    /**
     * Removes the object from the Map based on the key.
     */
    public Object remove( Object key )
    {
        if( null == key )
        {
            return null;
        }

        int hash = getHash( key );

        synchronized( m_buckets[hash] )
        {
            Node n = m_buckets[hash];
            Node prev = null;

            while( n != null && n.key != null )
            {
                if( (n.key == key) || (n.key.equals( key )) )
                {
                    // Remove this node from the linked list of nodes.
                    if( null == prev )
                    {
                        // This node was the head, set the next node to be the new head.
                        m_buckets[hash] = (n.next == null ? new Node() : n.next);
                    }
                    else
                    {
                        // Set the next node of the previous node to be the node after this one.
                        prev.next = n.next;
                    }

                    m_size--;
                    return n.value;
                }

                prev = n;
                n = n.next;
            }
        }

        return null;
    }

    /**
     * Tests if the Map is empty.
     */
    public final boolean isEmpty()
    {
        return m_size == 0;
    }

    /**
     * Removes all the entries from the Map.
     */
    public final void clear()
    {
        for( int i = 0; i < m_buckets.length; i++ )
        {
            m_buckets[i] = null; // be explicit
            m_buckets[i] = new Node();
        }
    }

    /**
     * The Map.Entry for the BucketMap.
     */
    private final class Node implements Map.Entry
    {
        protected Object key;
        protected Object value;
        protected Node next;

        public Object getKey()
        {
            return key;
        }

        public Object getValue()
        {
            return value;
        }

        public int hashCode()
        {
            return getHash( key );
        }

        public Object setValue( Object val )
        {
            Object retVal = value;
            value = val;
            return retVal;
        }
    }
}
