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
    protected final Map                       m_contextData;
    protected final Context                   m_parent;

    public DefaultContext( final Map contextData, final Context parent )
    {
        m_parent = parent;
        m_contextData = contextData;
    }

    public DefaultContext( final Map contextData )
    {
        this( contextData, null );
    }

    public DefaultContext( final Context parent )
    {
        this( new Hashtable(), parent );
    }

    public DefaultContext()
    {
        this( (Context)null );
    }

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

    public void put( final Object key, final Object value )
    {
        m_contextData.put( key, value );
    }
}
