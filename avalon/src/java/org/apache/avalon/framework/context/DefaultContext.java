/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.context;

import java.util.Hashtable;
import java.util.Map;

/**
 * Default implementation of Context.
 * This implementation is a static hierarchial store.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultContext
    implements Context
{
    private final Map                       m_contextData;
    private final Context                   m_parent;

    /**
     * Create a Context with specified data and parent.
     *
     * @param contextData the context data
     * @param parent the parent Context (may be null)
     */
    public DefaultContext( final Map contextData, final Context parent )
    {
        m_parent = parent;
        m_contextData = contextData;
    }

    /**
     * Create a Context with specified data.
     *
     * @param contextData the context data
     */
    public DefaultContext( final Map contextData )
    {
        this( contextData, null );
    }

    /**
     * Create a Context with specified parent.
     *
     * @param parent the parent Context (may be null)
     */
    public DefaultContext( final Context parent )
    {
        this( new Hashtable(), parent );
    }

    /**
     * Create a Context with no parent.
     *
     */
    public DefaultContext()
    {
        this( (Context)null );
    }

    /**
     * Retrieve an item from the Context.
     *
     * @param key the key of item
     * @return the item stored in context
     * @exception ContextException if item not present
     */
    public Object get( final Object key )
        throws ContextException
    {
        final Object data = m_contextData.get( key );

        if( null != data )
        {
            return data;
        }

        //thus data == null
        if( null == m_parent )
        {
            throw new ContextException( "Unable to locate " + key );
        }

        return m_parent.get( key );
    }

    /**
     * Helper method fo adding items to Context.
     *
     * @param key the items key
     * @param value the item
     */
    public void put( final Object key, final Object value )
    {
        m_contextData.put( key, value );
    }

    /**
     * Utility method to retrieve context data.
     *
     * @return the context data
     */
    protected final Map getContextData()
    {
        return m_contextData;
    }

    /**
     * Gte parent context if any.
     *
     * @return the parent Context (may be null)
     */
    protected final Context getParent()
    {
        return m_parent;
    }
}
