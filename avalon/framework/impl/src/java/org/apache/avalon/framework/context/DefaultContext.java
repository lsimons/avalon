/*
 * Copyright 1997-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.framework.context;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * Default implementation of Context.
 * This implementation is a static hierarchial store.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.27 $ $Date: 2004/02/11 14:34:26 $
 */
public class DefaultContext
    implements Context
{
    private static final class Hidden implements Serializable
    {
    }

    private static final Hidden HIDDEN_MAKER = new Hidden();

    private final Map m_contextData;
    private final Context m_parent;
    private boolean m_readOnly;

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
     * @throws ContextException if item not present
     */
    public Object get( final Object key )
        throws ContextException
    {
        final Object data = m_contextData.get( key );

        if( null != data )
        {
            if( data instanceof Hidden )
            {
                // Always fail.
                final String message = "Unable to locate " + key;
                throw new ContextException( message );
            }

            if( data instanceof Resolvable )
            {
                return ( (Resolvable)data ).resolve( this );
            }

            return data;
        }

        // If data was null, check the parent
        if( null == m_parent )
        {
            // There was no parent, and no data
            final String message =
                "Unable to resolve context key: " + key;
            throw new ContextException( message );
        }

        return m_parent.get( key );
    }

    /**
     * Helper method fo adding items to Context.
     *
     * @param key the items key
     * @param value the item
     * @throws IllegalStateException if context is read only
     */
    public void put( final Object key, final Object value )
        throws IllegalStateException
    {
        checkWriteable();
        if( null == value )
        {
            m_contextData.remove( key );
        }
        else
        {
            m_contextData.put( key, value );
        }
    }

    /**
     * Hides the item in the context.
     * After remove(key) has been called, a get(key)
     * will always fail, even if the parent context
     * has such a mapping.
     *
     * @param key the items key
     * @throws IllegalStateException if context is read only
     */
    public void hide( final Object key )
        throws IllegalStateException
    {
        checkWriteable();
        m_contextData.put( key, HIDDEN_MAKER );
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
     * Get parent context if any.
     *
     * @return the parent Context (may be null)
     */
    protected final Context getParent()
    {
        return m_parent;
    }

    /**
     * Make the context read-only.
     * Any attempt to write to the context via put()
     * will result in an IllegalStateException.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Utility method to check if context is writeable and if not throw exception.
     *
     * @throws IllegalStateException if context is read only
     */
    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            final String message =
                "Context is read only and can not be modified";
            throw new IllegalStateException( message );
        }
    }
}
