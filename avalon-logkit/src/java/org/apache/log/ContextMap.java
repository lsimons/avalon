/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

import java.util.Hashtable;

/**
 * The ContextMap contains non-hierarchial context information 
 * relevent to a particular LogEvent. It may include information
 * such as;
 *
 * <ul>
 * <li>user      -&gt;fred</li>
 * <li>hostname  -&gt;helm.realityforge.org</li>
 * <li>ipaddress -&gt;1.2.3.4</li>
 * <li>interface -&gt;127.0.0.1</li>
 * <li>caller    -&gt;com.biz.MyCaller.method(MyCaller.java:18)</li>
 * <li>source    -&gt;1.6.3.2:33</li>
 * </ul>
 * The context is bound to a thread (and inherited by sub-threads) but
 * it can also be added to by LogTargets.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class ContextMap 
{
    ///Thread local for holding instance of map associated with current thread
    private static final ThreadLocal c_context   = new InheritableThreadLocal();

    private ContextMap m_parent;

    ///Container to hold map of elements
    private Hashtable  m_map       = new Hashtable();

    ///Flag indicating whether this map should be readonly
    private boolean    m_readOnly;

    /**
     * Get the Current ContextMap.
     * This method returns a ContextMap associated with current thread. If the
     * thread doesn't have a ContextMap associated with it then a new
     * ContextMap is created.
     *
     * @return the current ContextMap
     */
    public final static ContextMap getCurrentContext()
    {
        return getCurrentContext( true );
    }

    /**
     * Get the Current ContextMap.
     * This method returns a ContextMap associated with current thread. 
     * If the thread doesn't have a ContextMap associated with it and
     * autocreate is true then a new ContextMap is created.
     *
     * @param autocreate true if a ContextMap is to be created if it doesn't exist
     * @return the current ContextMap
     */
    public final static ContextMap getCurrentContext( final boolean autocreate )
    {
        ContextMap context = (ContextMap)c_context.get();

        if( null == context && autocreate )
        {
            context = new ContextMap();
            c_context.set( context );
        }

        return context;
    }

    /**
     * Bind a particular ContextMap to current thread.
     *
     * @param context the context map (may be null)
     */
    public final static void bind( final ContextMap context )
    {
        c_context.set( context );
    }

    /**
     * Default constructor.
     */
    public ContextMap()
    {
    }

    /**
     * Constructor that sets parent contextMap.
     *
     * @param parent the parent ContextMap
     */
    public ContextMap( final ContextMap parent )
    {
        m_parent = parent;
    }

    /**
     * Make the context read-only. 
     * This makes it safe to allow untrusted code reference
     * to ContextMap.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Empty the context map.
     *
     */
    public void clear()
    {
        m_map.clear();
    }

    /**
     * Get an entry from the context.
     *
     * @param key the key to map
     * @param default a default object to return if key does not exist
     * @return the object in context
     */
    public Object get( final String key, final Object defaultObject )
    {
        final Object object = get( key );

        if( null != object ) return object;
        else return defaultObject;
    }

    /**
     * Get an entry from the context.
     *
     * @param key the key to map
     * @return the object in context or null if none with specified key
     */
    public Object get( final String key )
    {
        final Object result = m_map.get( key );
        
        if( null == result && null != m_parent )
        {
            return m_parent.get( key );
        }
        
        return result;
    }
  
    /**
     * Set a value in context
     *
     * @param key the key
     * @param value the value (may be null)
     */
    public void set( final String key, final Object value )
    {
        if( m_readOnly )
        {
            throw new IllegalStateException( "ContextMap is read only and can not be modified" );
        }

        if( value == null )
        {
            m_map.remove( key );
        }
        else
        {
            m_map.put( key, value );
        }
    }

    /**
     * Retrieve keys of entries into context map.
     *
     * @return the keys of items in context
     */
    /*
    public String[] getKeys()
    {
        return (String[])m_map.keySet().toArray( new String[ 0 ] );
    }
    */
  
    /**
     * Get the number of contexts in map.
     *
     * @return the number of contexts in map
     */
    public int getSize()
    {
        return m_map.size();
    }
}
